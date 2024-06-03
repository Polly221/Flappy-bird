package com.example.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.awt.*;
import android.graphics.fonts.Font.Builder;
import android.graphics.fonts.FontFamily;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Random;

//Класс представляет собой окно игры
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint();
    private Paint whitePaint = new Paint();
    private Paint scorePaint = new Paint();
    private Paint highScorePaint = new Paint();
    private int scoreTextSize = 150;
    private int highScoreTextSize = 40;
    private Point screenSize = new Point(1080,2400); //Размер экрана, на который ориентируется игра
    private Point actualScreenSize;
    private Float screenScaleX = 1f;
    private Float screenScaleY = 1f;
    private float scale;
    private Point gap = new Point(0,0);
    private SurfaceHolder holder;
    private Random rand = new Random();
    private GameLoop gameLoop;
    public int score;
    public int highScore;
    public SharedPreferences score_pref;
    public int FPS;
    private boolean isScore = false;
    private Bird bird;
    private Bitmap birdSprite;
    private Pipe pipe;
    private Bitmap pipeSprite1;
    private Bitmap pipeSprite2;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public GamePanel(Context context) {
        super(context);

        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        whitePaint.setColor(Color.WHITE);

        scorePaint.setColor(Color.BLACK);
        highScorePaint.setColor(Color.BLACK);

        scorePaint.setTextSize(scoreTextSize);
        highScorePaint.setTextSize(highScoreTextSize);


        //Считываем рекорд из файла
        score_pref = context.getSharedPreferences("score_pref", Context.MODE_PRIVATE);
        highScore = score_pref.getInt("save_key_score", highScore);


        //Превращаем ресурсы спрайтов в bitmap
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.bird, null);
        birdSprite = ((BitmapDrawable)d).getBitmap();

        d = ResourcesCompat.getDrawable(getResources(), R.drawable.pipe1, null);
        pipeSprite1 = ((BitmapDrawable)d).getBitmap();

        d = ResourcesCompat.getDrawable(getResources(), R.drawable.pipe2, null);
        pipeSprite2 = ((BitmapDrawable)d).getBitmap();


        //Получаем размер текущего экрана из MainActivity
        actualScreenSize = MainActivity.actualScreenSize;
        //Рассчет множителя (разницы размеров текущего и стандартного экрана) по ширине и высоте экрана
        screenScaleX = actualScreenSize.x * 1f / screenSize.x;
        screenScaleY = actualScreenSize.y * 1f / screenSize.y;

        //Выбор наименьшего из двух множителей для увеличения/уменьшения окна игры и рассчет пробела
        if (screenScaleX > screenScaleY){
            scale = screenScaleY;
            gap.x = (int) ((actualScreenSize.x - screenSize.x * scale) / 2);
            gap.y = 0;
        }
        else if (screenScaleY > screenScaleX){
            scale = screenScaleX;
            gap.y = (int) ((actualScreenSize.y - screenSize.y * scale) / 2);
            gap.x = 0;
        }
        else if (screenScaleX.equals(screenScaleY) && (screenScaleX != 1 || screenScaleY != 1)) {
            scale = screenScaleX;
            gap.x = (int) ((actualScreenSize.x - screenSize.x * scale) / 2);
            gap.y = (int) ((actualScreenSize.y - screenSize.y * scale) / 2);
        }
        else {
            scale = 1;
            gap.x = 0;
            gap.y = 0;
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(actualScreenSize);
        }
        gameLoop = new GameLoop(this);

    }

    public void FPS(int fps) {
        FPS = fps;
    }

    //Класс птицы с позицией и размерами
    private class Bird{
        private Rect hitBox;
        private float sizeX, sizeY;
        private float x, y;
        private float fallMullt = 1f;
        //Конструктор для создания экземпляра
        public Bird(float sizeX, float sizeY, float x, float y){
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.x = x;
            this.y = y;
        }
        //Метод падения птицы
        public void fall(double delta){
            y += 1f * fallMullt * delta * 60;
            if (fallMullt < 50f) {
                fallMullt += 1f * scale; //Скорость падения увеличивается со временем до предела
            }
            //Соблюдение границ окна игры
            if (y > screenSize.y * scale - gap.y - bird.sizeY){
                y = screenSize.y * scale - gap.y - bird.sizeY;
            }
            else if (y < 0 + gap.y - bird.sizeY){
                y = 0 + gap.y - bird.sizeY;
            }
        }
    }
    //Класс трубы с позицией и размерами
    private class Pipe{
        private  Rect[] hitBox;
        private float sizeX, sizeY;
        private float x, y;
        private float speedMullt = 1f;
        public Pipe(float sizeX, float sizeY, float x, float y){
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.x = x;
            this.y = y;
        }
        //Метод движения трубы влево
        public void Move(double delta){
            x -= 5f * speedMullt * scale * delta * 60;
            if (x<0-sizeX + gap.x){
                Respawn(); // Труба появляется справа
            }
            //Если труба левее птицы
            if (x + pipe.sizeX <= bird.x){
                if (!isScore){
                    score += 1;
                    isScore = true; //Очко за трубу было получено и не может быть получено вновь
                    System.out.println(score);
                }
            }
        }
        //Метод для перемещения трубы за границу экрана справа
        private void Respawn(){
            x = screenSize.x * scale + gap.x + sizeX;
            y = rand.nextFloat() * (screenSize.y * scale - sizeY * 4) + sizeY * 1; //Случайная позиция с лимитами
            isScore = false; //Позволяет снова увеличить счет
        }

    }
    public boolean onTouchEvent(MotionEvent event){;
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            bird.y -= 1;
            bird.fallMullt = -15f * scale; //Птица получает ускорение вверх, которое со временем вернется в норму
            render();
            return true;
        }
        return false;
    }
    //Метод создает трубу и птицу с учетом разницы размеров экрана текущего и стандартного
    public void Start(){
        System.out.println(highScore);
        score = 0;
        bird = new Bird(150 * scale, 120 * scale, screenSize.x * scale / 2f - 50 * scale / 2 + gap.x, screenSize.y * scale / 2f - 50 * scale / 2 + gap.y);
        pipe = new Pipe(256 * scale, 350 * scale, screenSize.x * scale + gap.x, screenSize.y * scale / 2f - 300 * scale / 2f );
        render();

    }
    //Метод для перезапуска
    public void GameOver(){
        saveScore(); //Сохраняем счет
        Start();
    }
    //Вызывается в самом начале и меняет размеры спрайтов с учетом разницы размеров экрана
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Start();
        birdSprite = Bitmap.createScaledBitmap(birdSprite, (int) (200 * scale), (int) (200 * scale), false);
        pipeSprite1 = Bitmap.createScaledBitmap(pipeSprite1, (int) (pipe.sizeX * scale), (int) (2048 * scale), false);
        pipeSprite2 = Bitmap.createScaledBitmap(pipeSprite2, (int) (pipe.sizeX * scale), (int) (2048 * scale), false);

        gameLoop.startGameLoop();
    }


    //Метод вызывается при смене окна
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    //Метод вызывается при удаления окна игры
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    //Метод сохранения счета
    public void saveScore(){
        if (score > highScore){
            SharedPreferences.Editor editor = score_pref.edit(); //Редактируем файл
            editor.putInt("save_key_score", score);
            highScore = score;
            editor.commit();
        }

    }

    //Метод вызывает движение объектов, обновляет позиции и т.д.
    public void update(double delta) {

        bird.fall(delta);
        pipe.Move(delta);
        for (int i = 0; i < pipe.hitBox.length; i++){
            if (Rect.intersects(bird.hitBox, pipe.hitBox[i])){ //Если птица соприкасается с трубой
                GameOver();
            }
        }
    }
    //Метод отрисовывает игру
    public void render() {
        Canvas c = holder.lockCanvas(); //Создаем канвас
        c.drawColor(Color.WHITE); //Фон
        c.drawBitmap(birdSprite, bird.x, bird.y, null); //Птица
        //Хитбокс птицы для коллизии
        bird.hitBox = new Rect((int) (bird.x + 20 * scale), (int) (bird.y + 40 * scale), (int) (bird.x + 20 * scale + bird.sizeX), (int) (bird.y + 40 * scale + bird.sizeY));


        //Хитбокс трубы для колизии (хитбокса два, так как труба состоит из двух частей
        pipe.hitBox = new Rect[]{
                new Rect((int) pipe.x, 0, (int) (pipe.x + pipe.sizeX), (int) pipe.y),
                new Rect((int) pipe.x, (int) (pipe.y + pipe.sizeY), (int) (pipe.x + pipe.sizeX), (int) (screenSize.y * scale))
        };
        //Рисуем трубу
        c.drawBitmap(pipeSprite1, pipe.x, pipe.y + pipe.sizeY, null);
        c.drawBitmap(pipeSprite2, pipe.x, 0 + pipe.y - 2048 * scale, null);

        //Выводим счет и рекорд
        c.drawText(Integer.toString(score), screenSize.x * scale / 2f - 50 * scale / 2 + gap.x, screenSize.y * scale / 12f - 50 * scale / 2 + gap.y, scorePaint);
        c.drawText("highscore: " + Integer.toString(highScore), 10,80, highScorePaint);

        //Открываем и отрисовываем канвас
        holder.unlockCanvasAndPost(c);
    }
}
