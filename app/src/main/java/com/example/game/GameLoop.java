package com.example.game;

//Класс представляет собой игровой цикл, который запкускается много раз в секунду
public class GameLoop implements Runnable {
    public Thread gameThread;
    private GamePanel gamePanel;
    //Метод создает поток и получает ссылку на окно игры
    public GameLoop(GamePanel gamePanel){
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    //Меотод, определяющий логику цикла
    @Override
    public void run() {
        long lastFPScheck = System.currentTimeMillis();
        int FPS = 0;

        long lastDelta = System.nanoTime();
        long nanoSec = 1_000_000_000;
        while (true){

            //Рассчет delta. delta нужна для компенсации задержек игры в случае, если количество кадров в секунду (кол-во вызовов run) уменьшается
            long nowDelta = System.nanoTime();
            double timeSinceLastDelta = nowDelta - lastDelta;
            double delta = timeSinceLastDelta / nanoSec;

            //render отрисовывает игру, а update обновляет позиции объектов и игру в целом
            gamePanel.render();
            gamePanel.update(delta);

            //Даем игровому потоку поспать чутка)))
            try {
                gameThread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lastDelta = nowDelta;

            FPS++;

            long now = System.currentTimeMillis();
            //Каждую секунду (1000 миллисекунд) выводим FPS для анализа
            if (now - lastFPScheck >= 1000){
                System.out.println("FPS: " + FPS);
                gamePanel.FPS(FPS);
                FPS = 0;
                lastFPScheck += 1000;
            }

        }
    }
    //Запускаем цикл (начинаем поток)
    public void startGameLoop(){
        gameThread.start();
    }
}
