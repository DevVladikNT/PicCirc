package com.vladiknt;

import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Scanner;

public class Main {

    private static String source = "Source/src.png"; // Дефолтный путь к изображению
    private static int ACCURACY = 10; // Чувствительность алгоритма

    // Меню приложения
    public static void main(String[] args) {
        int choice;
        boolean end = true;
        Scanner sc = new Scanner(System.in);
        while (end) {
            System.out.println("\n1 - Обработать фотографию." +
                    "\n2 - Сменить местоположение исходной фотографии." +
                    "\n3 - Сменить чувствительность алгоритма." +
                    "\n4 - Помощь." +
                    "\n0 - Выйти.");
            System.out.print("Введите пункт: ");
            while (true) {
                try {
                    choice = Integer.parseInt(sc.nextLine());
                    break;
                } catch (Exception e) {
                    System.out.println("Только цифры!");
                }
            }
            switch(choice) {
                case 1:
                    render();
                    break;
                case 2:
                    System.out.print("Введите новый путь к фотографии: ");
                    source = sc.nextLine();
                    break;
                case 3:
                    checkLight();
                    System.out.println("Советуем устанавливать чувствительность в диапазоне [6;30]." +
                            "\nЧем выше значение - тем меньше деталей будет на конечном изображении.");
                    System.out.print("Введите новое значение: ");
                    while (true) {
                        try {
                            ACCURACY = Integer.parseInt(sc.nextLine());
                            break;
                        } catch (Exception e) {
                            System.out.println("Только числа!");
                        }
                    }
                    break;
                case 4:
                    System.out.println("Изображение должно быть в формате .png" +
                            "\nТекущая чувствительность алгоритма = " + ACCURACY +
                            "\nТекущий путь к исходному изображению \"" + source + "\"");
                    break;
                case 0:
                    end = false;
                    break;
                default:
                    System.out.println("Такого варианта в меню нет.");
            }
        }
    }

    // Данный метод оценивает уровень яркости изображения
    private static void checkLight() {
        try {
            File file = new File(source);
            BufferedImage image = ImageIO.read(file);

            // Измеряет яркость изображения (чтобы успешнее анализировать работу алгоритма)
            double res = 0;
            int grey;
            int counter = 0;
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    grey = (int)(new Color(image.getRGB(i, j)).getRed() * 0.299 + new Color(image.getRGB(i, j)).getGreen() * 0.587 + new Color(image.getRGB(i, j)).getBlue() * 0.114);
                    res = (res * counter + grey) / ++counter;
                }
            }
            System.out.println("Яркость изображения " + (int)(res * 100 / 255) + "%");
        } catch (Exception e) {
            System.out.println("Не удалось считать изображение для определения уровня яркости.");
        }
    }

    // Данный метод обрабатывает изображение
    private static void render() {
        File file;
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage cur = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        try {
            file = new File(source);
            image = ImageIO.read(file);
            cur = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("Error while reading picture.");
        }
        Color black = new Color(0, 0, 0);

        // Blur (блюрит фотку чтобы уменьшить зернистость)
        Color currentColor;
        Color col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12;
        for (int i = 2; i < image.getWidth() - 2; i++) {
            for (int j = 2; j < image.getHeight() - 2; j++) {
                col1 = new Color(image.getRGB(i - 1, j));
                col2 = new Color(image.getRGB(i, j - 1));
                col3 = new Color(image.getRGB(i + 1, j));
                col4 = new Color(image.getRGB(i, j + 1));

                col5 = new Color(image.getRGB(i + 1, j + 1));
                col6 = new Color(image.getRGB(i - 1, j + 1));
                col7 = new Color(image.getRGB(i + 1, j - 1));
                col8 = new Color(image.getRGB(i - 1, j - 1));

                col9 = new Color(image.getRGB(i, j + 2));
                col10 = new Color(image.getRGB(i + 2, j));
                col11 = new Color(image.getRGB(i, j - 2));
                col12 = new Color(image.getRGB(i - 2, j));
                currentColor = new Color((col1.getRed() + col2.getRed() + col3.getRed() + col4.getRed() + col5.getRed() + col6.getRed() + col7.getRed() + col8.getRed() + col9.getRed() + col10.getRed() + col11.getRed() + col12.getRed())/12,
                        (col1.getGreen() + col2.getGreen() + col3.getGreen() + col4.getGreen() + col5.getGreen() + col6.getGreen() + col7.getGreen() + col8.getGreen() + col9.getGreen() + col10.getGreen() + col11.getGreen() + col12.getGreen())/12,
                        (col1.getBlue() + col2.getBlue() + col3.getBlue() + col4.getBlue() + col5.getBlue() + col6.getBlue() + col7.getBlue() + col8.getBlue() + col9.getBlue() + col10.getBlue() + col11.getBlue() + col12.getBlue())/12);
                cur.setRGB(i, j, currentColor.getRGB());
            }
        }

        // Горизонтальный алгоритм
        for (int i = 0; i < image.getWidth() - 2; i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (Math.abs(new Color(image.getRGB(i, j)).getRed() - new Color(image.getRGB(i + 2, j)).getRed()) > ACCURACY ||
                        Math.abs(new Color(image.getRGB(i, j)).getGreen() - new Color(image.getRGB(i + 2, j)).getGreen()) > ACCURACY ||
                        Math.abs(new Color(image.getRGB(i, j)).getBlue() - new Color(image.getRGB(i + 2, j)).getBlue()) > ACCURACY) {
                    cur.setRGB(i + 1, j, black.getRGB());
                }
            }
        }
        // Вертикальный алгоритм
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight() - 2; j++) {
                if (Math.abs(new Color(image.getRGB(i, j)).getRed() - new Color(image.getRGB(i, j + 2)).getRed()) > ACCURACY ||
                        Math.abs(new Color(image.getRGB(i, j)).getGreen() - new Color(image.getRGB(i, j + 2)).getGreen()) > ACCURACY ||
                        Math.abs(new Color(image.getRGB(i, j)).getBlue() - new Color(image.getRGB(i, j + 2)).getBlue()) > ACCURACY) {
                    cur.setRGB(i, j + 1, black.getRGB());
                }
            }
        }
        // Перекрашивает неконтурные иксели в белый
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (cur.getRGB(i, j) != black.getRGB()) {
                    cur.setRGB(i, j, new Color(255, 255, 255).getRGB());
                }
            }
        }

        // Попытка убрать шумы (убирает очевидные отдельно стоящие пиксели)
        int k;
        for (int i = 1; i < image.getWidth() - 1; i++) {
            for (int j = 1; j < image.getHeight() - 1; j++) {
                k = 0;
                if (cur.getRGB(i, j) == black.getRGB()) {
                    for (int a = (i - 1); a <= i + 1; a++) {
                        for (int b = (j - 1); b <= j + 1; b++) {
                            if (a == i && b == j)
                                continue;
                            if (cur.getRGB(a, b) == black.getRGB())
                                k++;
                        }
                    }
                    if (k < 3)
                        cur.setRGB(i, j, new Color(255, 255, 255).getRGB());
                }
            }
        }

        try {
            File output = new File("out.png");
            ImageIO.write(cur, "png", output);
        } catch (Exception e) {
            System.out.println("Error while saving picture.");
        }
    }

}
