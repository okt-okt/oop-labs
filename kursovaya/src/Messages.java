import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Messages{
    private TelegramLongPollingBot bot;

    public Messages(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendHiMessage(Long chatID, String userName) throws TelegramApiException {
        String text = "Привет, " + (userName != null ? userName : "Друг") + "!\n\n" +
                "Я твой персональный поисковик для рецептов!\n\n" +
                "Со мной ты можешь:\n" +
                "🔎 Искать рецепты по ингредиентам\n" +
                "📝 Вести свой список рецептов\n" +
                "⭐ Выбирать, что приготовить\n" +
                "\nНачнём по твоей команде!";
        sendMessage(chatID,text,getNavigationKeyboard());
    }

    public <T> void sendMessage(Long chatID, String text, T fun) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(text);
        message.setParseMode("");
        message.setReplyMarkup((ReplyKeyboard) fun);
        bot.execute(message);
    }

    public void editMessageKeyboard(Long chatId, Integer messageId, String text,
                                    InlineKeyboardMarkup newKeyboard) throws TelegramApiException {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setReplyMarkup(newKeyboard);
        bot.execute(editMessage);
    }

    public ReplyKeyboardMarkup getNavigationKeyboard(){
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> list = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Поиск рецептов"));
        row1.add(new KeyboardButton("Избранные"));

        list.add(row1);
        keyboard.setKeyboard(list);
        return keyboard;
    }

    public InlineKeyboardMarkup getInlineKeyboard(String[][] click){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        for (String[] row : click){
            List<InlineKeyboardButton> current_row = new ArrayList<>();
            for (int i = 0; i < row.length; i+=2){
                if (i+1 < row.length){
                    String buttonText = row[i];
                    String buttonCall = row[i+1];
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(buttonText);
                    button.setCallbackData(buttonCall);
                    current_row.add(button);
                }
            }
            if (!current_row.isEmpty()) {
                list.add(current_row);
            }
        }
        keyboard.setKeyboard(list);
        return keyboard;
    }
}
