import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class RequestResponse{
    protected Messages messages;
    protected DataBase db;
    private Map<Long,String> map = new HashMap<>();
    public RequestResponse(Messages messages, DataBase db) {
        this.messages = messages;
        this.db = db;
    }

    public void getInterrupt(Update update) throws TelegramApiException, IOException, InterruptedException {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();
        ParserInf parser = new ParserInf();

        switch (message) {
            case "/start":
                messages.sendHiMessage(chatId, userName);
                return;

            case "/delete":
                db.delete(chatId);
                messages.sendMessage(chatId, "Ваши данные удалены, " + userName + "!", messages.getNavigationKeyboard());
                map.remove(chatId);
                return;

            case "Поиск рецептов":
                messages.sendMessage(chatId, "Введите поисковой запрос:", null);
                map.put(chatId, "wait_search");
                return;

            case "Избранные":
                StringBuilder result = new StringBuilder();
                StringBuilder state = new StringBuilder();
                state.append("select");
                if(db.getRecipes(chatId).isEmpty()){
                    messages.sendMessage(chatId, "Вы пока не сохраняли рецепты", messages.getNavigationKeyboard());
                    map.remove(chatId);
                }
                else {
                    result.append("Ваши избранные рецепты:");
                    int n = 1, i = 0;
                    List<String> recipes = db.getRecipes(chatId);
                    while (i < recipes.size()) {
                        if (!recipes.get(i).isEmpty()) {
                            state.append(" ").append(recipes.get(i)).append(" ").append(recipes.get(i + 1));
                            result.append("\n").append(n++).append(") ").append(recipes.get(i + 1).replaceAll("_", " "));
                            i += 2;
                        }
                        else{
                            recipes.remove(i);
                            recipes.remove(i);
                        }
                    }
                    messages.sendMessage(chatId, result.toString(), null);
                    messages.sendMessage(chatId, "Введите номер рецепта для просмотра", null);
                    map.put(chatId, state.toString());
                }
                return;
        }
        if (map.containsKey(chatId)) {
            String state = map.get(chatId);
            if (state != null) state = state.split(" ")[0];
            switch (state) {
                case "wait_search": {
                    Map<String, String> found = parser.searchRecipes(message);
                    StringBuilder msg = new StringBuilder();
                    StringBuilder st = new StringBuilder();
                    st.append("select");
                    msg.append(found != null ? "Результаты поиска:\n" : "Ничего не нашлось.");
                    int n = 1;
                    if (found != null) {
                        Object[] values = found.values().toArray();
                        Object[] keys = found.keySet().toArray();
                        for (int i = 0, t = found.keySet().size(); i < t; i++) {
                            msg.append("\n").append(n++).append(") ").append(values[i]);
                            st.append(" " + keys[i].toString() + " " + values[i].toString().replaceAll(" ", "_"));
                        }
                    }
                    map.put(chatId, st.toString());
                    messages.sendMessage(chatId, msg.toString(), null);
                    messages.sendMessage(chatId, "Отправьте номер интересующего рецепта, чтобы открыть его описание", null);
                }
                return;

                case "select": {
                    String[] recipes = map.get(chatId).split(" ");
                    Integer n = Integer.valueOf(message);
                    if (n == null | n < 1 | n > (recipes.length - 1) / 2)
                        messages.sendMessage(chatId, "Недопустимый номер рецепта!", null);
                    else {
                        messages.sendMessage(chatId, parser.getInfo(recipes[2*n-1]), messages.getInlineKeyboard(
                                db.getRecipes(chatId).contains(recipes[2*n-1]) ?
                                        new String[][]{{"Убрать из избранного", "remove_recipe"}} :
                                        new String[][]{{"Добавить в избранное", "add_recipe"}}));
                        map.put(chatId, "recipe " + recipes[2*n-1]);
                    }
                }
                return;
            }
        }

        messages.sendMessage(chatId, "Неизвестная команда", messages.getNavigationKeyboard());
        map.remove(chatId);
    }

    public void getCallBack(String callBackData, Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String messageText = update.getCallbackQuery().getMessage().getText();
        String[] state = map.get(chatId).split(" ");

        //ButtomsOut buttomsOut = new ButtomsOut(messages);
        switch (callBackData) {
            case "add_recipe":
                if (!state[0].equals("recipe") | state.length < 2) messages.sendMessage(chatId,
                        "Возникла ошибка при добавлении в избранное, попробуйте ещё раз позже!", messages.getNavigationKeyboard());
                else {
                    db.add(chatId, state[1], messageText.split("\n")[0]);
                    messages.editMessageKeyboard(chatId, messageId, messageText,
                            messages.getInlineKeyboard(new String[][]{{"Убрать из избранного", "remove_recipe"}}));
                }
                break;

            case "remove_recipe":
                if (!state[0].equals("recipe") | state.length < 2) messages.sendMessage(chatId,
                        "Возникла ошибка при удалении из избранного, попробуйте ещё раз позже!", messages.getNavigationKeyboard());
                else {
                    db.remove(chatId, state[1]);
                    messages.editMessageKeyboard(chatId, messageId, messageText,
                            messages.getInlineKeyboard(new String[][]{{"Добавить в избранное", "add_recipe"}}));
                }
                break;
        }
    }
}
