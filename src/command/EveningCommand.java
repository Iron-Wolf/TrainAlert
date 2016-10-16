package command;

import ressources.APIWorker;
import ressources.BotConfig;
import ressources.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * command used for evening departure
 */
public class EveningCommand extends BotCommand {

    private static final String LOGTAG = "EVENING-COMMAND";

    public EveningCommand () {
        super("Soir", "Horaires Paris > Colombes");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageBuilder = new StringBuilder("Etat du réseau : ");
        String message;

        try {
            // SNCF transilien
            String fullSNCFUrl = String.format(BotConfig.SNCF_URL, BotConfig.ST_LAZARD, BotConfig.COLOMBES);
            InputStream content = APIWorker.getInstance().getXMLData(fullSNCFUrl, BotConfig.SNCF_USER, BotConfig.SNCF_PASSWD);
            message =  APIWorker.getInstance().getMessageJLine(content);

            // RATP subway
            for (Integer subwayLine : BotConfig.SUBWAY_LIST) {
                String fullRATPUrl = String.format(BotConfig.RATP_URLS, subwayLine.toString());
                InputStream ratpContent =  APIWorker.getInstance().getXMLData(fullRATPUrl);
                message +=  APIWorker.getInstance().getMessageSubway(ratpContent, false);
            }

            // message header
            if (message.isEmpty())
                messageBuilder.append(Emoji.WHITE_HEAVY_CHECK_MARK + "\nAucun problèmes");
            else
                messageBuilder.append(Emoji.WARNING_SIGN + "\n" + message);

            // send message
            SendMessage answer = new SendMessage();
            answer.setChatId(chat.getId().toString());
            answer.enableHtml(true);
            answer.setText(messageBuilder.toString());
            absSender.sendMessage(answer);


        } catch (ParserConfigurationException e) {
            BotLogger.error(LOGTAG, e);
        } catch (SAXException e) {
            BotLogger.error(LOGTAG, e);
        } catch (IOException e){
            BotLogger.error(LOGTAG, e);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}