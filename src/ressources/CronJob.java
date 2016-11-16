package ressources;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * Class used to make asynchrone call to SNCF or RATP API
 */
public class CronJob extends TelegramLongPollingBot implements Job {

    private static final String LOGTAG = "MONITORING";

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

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        sendAlerts();
    }

    private void sendAlerts() {
        // send message
        try {
            SendMessage answer = ReplyMessage.getSendMessage(BotConfig.MY_CHAT_ID, "scheduled test message");
            sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG + "-sendAlerts", e);
        }
    }
}
