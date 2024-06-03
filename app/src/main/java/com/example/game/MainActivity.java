package com.example.game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;

import android.app.Activity;
import android.content.Context;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.ImageView;


@RequiresApi(api = Build.VERSION_CODES.R)
//В классе мы получаем размеры экрана и устанавливаем полноэкранный режим
public class MainActivity extends AppCompatActivity {

    private View decorView;
    private Display display;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point actualScreenSize;
    ImageView Bird;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Получаем текущий экран и записываем его размеры
        display = getDisplay();
        display.getRealMetrics(displayMetrics);
        actualScreenSize = new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);


        super.onCreate(savedInstanceState);
        setContentView(new GamePanel(this));

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0){
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });
    }

    //Метод скрывает системную панель в случае, если фокус установлен на окне игры
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());

        }
    }
    //Метод скрывает панель навигации и устанавливает полноэкранный режим
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}