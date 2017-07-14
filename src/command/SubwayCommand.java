package command;

import ressources.APIWorker;
import ressources.BotConfig;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import ressources.Emoji;
import ressources.ReplyMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Particular command used to retrieve status of any subway line
 */
public class SubwayCommand extends BotCommand{
    private static final String LOGTAG = "SUBWAY-COMMAND";
    private static final Integer[] SUBWAY_LINE = {1,14} ;

    public SubwayCommand() {
        super("Metro", " [Ligne] - Info sur les métros");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageBuilder = new StringBuilder("");
        String message = "";

        try {
            if (arguments.length <= 0)
            {
                // return all Subway that contain a message
                for (int subwayLine = SUBWAY_LINE[0]; subwayLine <= SUBWAY_LINE[SUBWAY_LINE.length - 1]; subwayLine++) {
                    String fullRATPUrl = String.format(BotConfig.RATP_SUBWAY_URL, subwayLine);
                    InputStream ratpContent =  APIWorker.getInstance().getXMLData(fullRATPUrl);
                    message +=  APIWorker.getInstance().getMessageSubway(ratpContent, false);
                }
                message = (message.isEmpty()) ? "Aucun problème " + Emoji.WHITE_HEAVY_CHECK_MARK : message ;
            }

            // loop on each argument
            for (String subwayLine : arguments) {
                // get message if the argument is a valid subway line
                try {
                    if (Integer.parseInt(subwayLine) >= SUBWAY_LINE[0]
                            && Integer.parseInt(subwayLine) <= SUBWAY_LINE[SUBWAY_LINE.length - 1]) {
                        String fullRATPUrl = String.format(BotConfig.RATP_SUBWAY_URL, subwayLine);
                        InputStream ratpContent = APIWorker.getInstance().getXMLData(fullRATPUrl);
                        message += APIWorker.getInstance().getMessageSubway(ratpContent, true);
                    }
                    else {
                        message += "metro <b>" + Integer.parseInt(subwayLine) + "</b> inconnue\n";
                    }
                } catch (NumberFormatException e){
                    message += "metro <b>" + subwayLine + "</b> incorrecte\n";
                }
            }

            messageBuilder.append(message);

            // send message
            SendMessage answer = ReplyMessage.getSendMessage(chat.getId(), messageBuilder.toString());
            absSender.sendMessage(answer);

        } catch (TelegramApiException | IOException e) {
            BotLogger.error(LOGTAG, e);
        }

    }
}
