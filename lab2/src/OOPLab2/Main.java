package OOPLab2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(new Locale("ru", "RU"));

        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            System.out.println("    ООП ЛР-2\nАвтор: Н.А. Чехонадских\n");

            while (true) {
                System.out.print("Введите путь к файлу, либо 0 для выхода из программы: ");
                String filename = parseFilename(scanner.nextLine().trim());

                if (filename.equals("0")) {
                    break;
                }

                Path filePath = Paths.get(filename);
                if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                    Dataset dataset = Dataset.createDataset(filename);

                    if (dataset != null) {
                        try {
                            long[] times = new long[4];

                            times[0] = System.currentTimeMillis();
                            dataset.input(filePath);
                            times[1] = System.currentTimeMillis();

                            dataset.printDups();
                            times[2] = System.currentTimeMillis();

                            dataset.countHouse();
                            times[3] = System.currentTimeMillis();

                            System.out.println("Время выполнения:");
                            System.out.printf("   ввода данных - %d мс\n", times[1] - times[0]);
                            System.out.printf("   печати дубликатов - %d мс\n", times[2] - times[1]);
                            System.out.printf("   поиска и печати домов одной этажности в городах - %d мс\n", times[3] - times[2]);
                            System.out.printf("   (ИТОГО) всей программы - %d мс\n\n", times[3] - times[0]);

                        } catch (IOException e) {
                            System.out.println("Ошибка при чтении файла: " + e.getMessage() + "\n");
                        }
                    } else {
                        System.out.println("Неподдерживаемый формат файла. Используйте .CSV или .XML!\n");
                    }
                } else {
                    System.out.println("Был введён недопустимый путь к файлу.\n");
                }
            }
        }
    }

    private static String parseFilename(String input) {
        if (input.startsWith("\"")) {
            int endQuote = input.indexOf("\"", 1);
            return endQuote != -1 ? input.substring(1, endQuote) : input.substring(1);
        }
        return input;
    }
}