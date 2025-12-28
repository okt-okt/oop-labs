package OOPLab2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Класс House (Дом)
class House {
    int floor;
    int repetitions = 0;

    House(int floor) {
        this.floor = floor;
    }
}

// Класс City (Город)
class City {
    // Key=address, value=House
    Map<String, House> houses = new HashMap<>();
    int[] houseStats = new int[5];

    City() {}

    void addHouse(String address, int floor) {
        houseStats[floor - 1]++;

        House house = houses.get(address);
        if (house == null) {
            house = new House(floor);
            houses.put(address, house);
        }
        house.repetitions++;
    }
}

// Абстрактный класс Dataset
abstract class Dataset {
    // Key=city name, value=City
    protected Map<String, City> cities = new HashMap<>();

    Dataset() {
        // В Java HashMap автоматически управляет нагрузкой
    }

    public abstract void input(Path filePath) throws IOException;

    // Метод для вставки данных
    public void insertData(String city, String address, int floor) {
        City c = cities.get(city);
        if (c == null) {
            c = new City();
            cities.put(city, c);
        }
        c.addHouse(address, floor);
    }

    // Метод для печати дубликатов
    public void printDups() {
        boolean first = true;

        for (Map.Entry<String, City> cityEntry : cities.entrySet()) {
            City city = cityEntry.getValue();

            for (Map.Entry<String, House> houseEntry : city.houses.entrySet()) {
                House house = houseEntry.getValue();

                if (house.repetitions > 1) {
                    if (first) {
                        System.out.println("Во время обработки файла обнаружены следующие дублирующиеся записи:");
                        first = false;
                    }
                    System.out.printf("%d x [%s, %s, %d этажей]\n",
                            house.repetitions, cityEntry.getKey(), houseEntry.getKey(), house.floor);
                }
            }
        }

        if (first) {
            System.out.println("Дубликатов не было обнаружено.\n");
        } else {
            System.out.println(); // Добавляем пустую строку после списка дубликатов
        }
    }

    // Метод для подсчета домов по этажности
    public void countHouse() {
        for (Map.Entry<String, City> cityEntry : cities.entrySet()) {
            System.out.printf("В городе \"%s\":%n", cityEntry.getKey());

            City city = cityEntry.getValue();
            for (int i = 0; i < 5; i++) {
                if (city.houseStats[i] > 0) {
                    System.out.printf("   %d-этажных домов: %d%n", (i + 1), city.houseStats[i]);
                }
            }
            System.out.println();
        }
    }

    public void clear() {
        cities.clear();
    }

    public static Dataset createDataset(String filename) {
        if (filename.toLowerCase().endsWith(".csv")) {
            return new DatasetCSV();
        } else if (filename.toLowerCase().endsWith(".xml")) {
            return new DatasetXML();
        }
        return null;
    }
}

class DatasetCSV extends Dataset {
    @Override
    public void input(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    try {
                        String city = parts[0].substring(1, parts[0].length() - 1);
                        String address = parts[1].substring(1, parts[1].length() - 1) + ", " + parts[2];
                        int floor = Integer.parseInt(parts[3]);

                        if (floor >= 1 && floor <= 5) {
                            insertData(city, address, floor);
                        }
                    } catch (NumberFormatException e) {}
                }
            }
        }
    }
}

// Конкретная реализация для XML файлов
class DatasetXML extends Dataset {
    @Override
    public void input(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = extractQuotedStrings(line).toArray(new String[0]);
                if (parts.length == 4) {
                    try {
                        String city = parts[0];
                        String address = parts[1] + ", " + parts[2];
                        int floor = Integer.parseInt(parts[3]);

                        if (floor >= 1 && floor <= 5) {
                            insertData(city, address, floor);
                        }
                    } catch (NumberFormatException e) {}
                }
            }
        }
    }

    private static List<String> extractQuotedStrings(String text) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }
}

