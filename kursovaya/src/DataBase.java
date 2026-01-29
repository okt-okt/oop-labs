    import java.io.*;
    import java.util.*;

    public class DataBase {

        private Map<Long, List<String>> data = new HashMap<>();
        private String filename = "C:\\Developments\\database.txt";

        public DataBase() {
            loadFromFile();
        }

        private void loadFromFile() {
            File file = new File(filename);
            if (!file.exists()) {
                data = new HashMap<>();
                return;
            }

            data.clear();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    Scanner scanner = new Scanner(line);
                    Long chatId = scanner.nextLong();
                    String s = scanner.nextLine();
                    List<String> recipes = Arrays.stream(s.split(" ")).toList();
                    if (chatId != null) data.put(chatId, recipes);
                }
            } catch (IOException e) {
                System.out.println("Ошибка загрузки: " + e.getMessage());
            }
        }


        private void saveToFile() {
            try (PrintWriter writer = new PrintWriter(filename)) {
                for (Long chatId : data.keySet()) {
                    if (data.get(chatId).isEmpty()) continue;

                    String s = String.join(" ", data.get(chatId));
                    writer.println(chatId.toString() + " " + s);
                }
            } catch (IOException e) {
                System.out.println("Ошибка сохранения: " + e.getMessage());
            }
        }

        public void add(Long chatId, String recipeId, String title) {
            List<String> recipes = new ArrayList<>(getRecipes(chatId));
            if (!recipes.contains(recipeId)) {
                recipes.add(recipeId);
                recipes.add(title.replaceAll(" ", "_"));
                data.put(chatId, recipes);
                saveToFile();
            }
        }

        public List<String> getRecipes(Long chatId) {
            List<String> recipes = data.get(chatId);
            if (recipes == null) {
                return new ArrayList<>();
            }
            return recipes;
        }

        public void delete(Long chatId) {
            data.remove(chatId);
            saveToFile();
        }

        public void remove(Long chatId, String recipe) {
            List<String> recipes = new ArrayList<>(getRecipes(chatId));
            int pos = recipes.indexOf(recipe);
            if (pos != -1) {
                recipes.remove(pos);
                recipes.remove(pos);
                data.put(chatId, recipes);
                saveToFile();
            }
        }
    }
