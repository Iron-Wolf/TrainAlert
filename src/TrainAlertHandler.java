import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * Main class containing bot behavior
 */
public class TrainAlertHandler extends TelegramLongPollingBot {

    public TrainAlertHandler() {

    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //check if the update has a message
        if(update.hasMessage()){
            Message message = update.getMessage();

            //check if the message has text. it could contain a location ( message.hasLocation() )
            if(message.hasText()){
                //create an object that contains the information to send back the message
                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message
                sendMessageRequest.setText("Message : " + message.getText() + " \n Emoji : "+Emoji.AIRPLANE);

                try {
                    sendMessage(sendMessageRequest); // send the message
                } catch (TelegramApiException e) {
                    //do some error handling
                }
            }
        }
    }
}
