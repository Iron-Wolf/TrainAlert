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
import ressources.ReplyMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * command used for evening departure
 */
public class EveningCommand extends BotCommand {

    private static final String LOGTAG = "EVENING-COMMAND";

    public EveningCommand () {
        super("Soir", " - Horaires Paris > Colombes");
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
                String fullRATPUrl = String.format(BotConfig.RATP_SUBWAY_URL, subwayLine.toString());
                InputStream ratpContent =  APIWorker.getInstance().getXMLData(fullRATPUrl);
                message +=  APIWorker.getInstance().getMessageSubway(ratpContent, false);
            }

            // message header
            messageBuilder.append((message.isEmpty()) ?
                    Emoji.WHITE_HEAVY_CHECK_MARK + "\nAucun problèmes" :
                    Emoji.WARNING_SIGN + "\n" + message);

            // send message
            SendMessage answer = ReplyMessage.getSendMessage(chat.getId(), messageBuilder.toString());
            absSender.sendMessage(answer);

        } catch (TelegramApiException | IOException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}