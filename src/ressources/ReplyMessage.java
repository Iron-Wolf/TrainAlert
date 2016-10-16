package ressources;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralize all messages. Allow better consistency in the code.<br/>
 * The <i>send</i> operation has to be made by the caller.
 */
public class ReplyMessage {

    public static SendMessage getSendMessage(Long chatId, String message){
        return customMessage(chatId, message, null);
    }

    public static SendMessage getSendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard){
        return customMessage(chatId, message, replyKeyboard);
    }

    private static SendMessage customMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
        // create an object that contains the information to send back the message
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setChatId(chatId.toString());// who should get the message
        sendMessage.setText(message);

        if (replyKeyboard != null)
            sendMessage.setReplyMarkup(replyKeyboard);

        return sendMessage;
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("/matin");
        firstRow.add("/soir");
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("/help");

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
