package ressources;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class used to make asynchronous call to SNCF or RATP API
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
        // this method contain nothing because we don't use it :p
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        StringBuilder messageBuilder = new StringBuilder(Emoji.ALARM_CLOCK + " Alerte automatique : \n");
        String message;

        try {
            // SNCF transilien
            String fullSNCFUrl = String.format(BotConfig.SNCF_URL, BotConfig.COLOMBES, BotConfig.ST_LAZARD);
            InputStream content = APIWorker.getInstance().getXMLData(fullSNCFUrl, BotConfig.SNCF_USER, BotConfig.SNCF_PASSWD);
            message = "Départ Colombes -> " + APIWorker.getInstance().getMessageJLine(content);

            fullSNCFUrl = String.format(BotConfig.SNCF_URL, BotConfig.ST_LAZARD, BotConfig.COLOMBES);
            content = APIWorker.getInstance().getXMLData(fullSNCFUrl, BotConfig.SNCF_USER, BotConfig.SNCF_PASSWD);
            message += "Départ St Lazard -> " + APIWorker.getInstance().getMessageJLine(content);

            // RATP subway
            for (Integer subwayLine : BotConfig.SUBWAY_LIST) {
                String fullRATPUrl = String.format(BotConfig.RATP_SUBWAY_URL, subwayLine.toString());
                InputStream ratpContent =  APIWorker.getInstance().getXMLData(fullRATPUrl);
                message +=  APIWorker.getInstance().getMessageSubway(ratpContent, false);
            }

            // send message only if something is returned from the APIWorker
            if (!message.isEmpty()) {
                messageBuilder.append(message);
                sendAlerts(messageBuilder);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    private void sendAlerts(StringBuilder message) {
        // send message
        try {
            SendMessage answer = ReplyMessage.getSendMessage(BotConfig.MY_CHAT_ID, message.toString());
            sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG + "-sendAlerts", e);
        }
    }
}
