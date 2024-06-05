# Flappy bird
 
Данный проект является игрой, подобной игре Flappy bird, написанной на языке программирования Java в среде Android studio. В проекте были реализованы следующие принципы классической игры:
    
* "Птица", прыгающая по нажатию экрана в высоту, а затем падающая с ускорением вниз
* Трубы, двигающиеся в сторону птицы и представляющие собой препятствие, при столкновении с которыми игра начинается с начала
* Счетчик очков, отображающий количество преодоленных труб и сбрасывающийся при проигрыше
* Текстовое поле со значением рекорда, которое обновляется каждый раз при проигрыше игры в случаае его преодоления
* Сохранение рекорда в текстовый файл и его считывание из файла при каждом запуске игры

Программный код проекта разбит на три java-класса:

* MainActivity - первый класс, который получает информацию об экране пользователя, а также включает "полноэкранный" режим, скрывая панель навигации, кнопки и т.д.
* GamePanel - главный класс, отвечающий за отрисовку всех объектов игры, а также за логику: прыжки птицы, движение труб, сохранение счета и т.п. Также этот класс меняет размер окна игры в зависимости от размера экрана пользователя.
* GameLoop - класс, отвечающий за "игровой поток" - вечный цикл, который запускается определенное количество раз в секунду (60 кадров в секунду). Благодаря ему объекты могут плавно двигаться засчет постоянного вызова методов движения, которые каждый раз чуть-чуть сдвигают нужные игровые объекты. В случае, если ресурсов устройства не хватает для игры в 60 кадров, в классе предусмотренна величина (delta), которая записывает задержки игры в миллисекундах и помогает справлятся с "просадками" кадров

Также в коде представленны коментарии по поводу функциональности тех или иных блоков кода. Файлы java-классов можно найти по пути - app/src/main/java/com/example/game
