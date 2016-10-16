package main;

import command.*;
import ressources.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * Main class containing bot behavior
 */
public class TrainAlertHandler extends TelegramLongPollingCommandBot {

    public static final String LOGTAG = "HANDLER";

    public TrainAlertHandler() {
        register(new MorningCommand());
        register(new EveningCommand());
        register(new StartCommand());
        register(new SubwayCommand());
        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);
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
    public void processNonCommandUpdate(Update update) {
        //check if the update has a message
        if (update.hasMessage()) {
            Message message = update.getMessage();

            //check if the message has text
            if (message.hasText()) {
                //create an object that contains the information to send back the message
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId().toString()); //who should get the message
                echoMessage.setText("Commande inconnue : " + message.getText());

                try {
                    sendMessage(echoMessage);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }


}
