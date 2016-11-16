package command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.xml.sax.SAXException;
import ressources.APIWorker;
import ressources.BotConfig;
import ressources.Emoji;
import ressources.ReplyMessage;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Particular command used to retrieve status of any RER line
 */
public class RersCommand extends BotCommand {
    private static final String LOGTAG = "SUBWAY-COMMAND";
    private static final String[] RERS_LINE = {"a","b"};

    public RersCommand() {
        super("Rer", " [Ligne] - Info sur les RER A et B");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageBuilder = new StringBuilder("");
        String message = "";

        try {
            if (arguments.length <= 0) {
                // return all rer that contain a message
                for (String rerLine : RERS_LINE) {
                    String fullRATPUrl = String.format(BotConfig.RATP_RER_URL, rerLine);
                    InputStream ratpContent =  APIWorker.getInstance().getXMLData(fullRATPUrl);
                    message +=  APIWorker.getInstance().getMessageSubway(ratpContent, false);
                }
                message = (message.isEmpty()) ? "Aucun problème " + Emoji.WHITE_HEAVY_CHECK_MARK : message ;
            }

            // loop on each argument
            for (String rerLine : arguments) {
                // get message if the argument is a valid RER line
                try {
                    if (stringContainsItemFromList(rerLine.toLowerCase(), RERS_LINE)) {
                        String fullRATPUrl = String.format(BotConfig.RATP_RER_URL, rerLine);
                        InputStream ratpContent = APIWorker.getInstance().getXMLData(fullRATPUrl);
                        message += APIWorker.getInstance().getMessageSubway(ratpContent, true);
                    } else {
                        message += "rer <b>" + Integer.parseInt(rerLine) + "</b> inconnue\n";
                    }
                } catch (NumberFormatException e) {
                    message += "rer <b>" + rerLine + "</b> incorrecte\n";
                }
            }

            messageBuilder.append(message);

            // send message
            SendMessage answer = ReplyMessage.getSendMessage(chat.getId(), messageBuilder.toString());
            absSender.sendMessage(answer);

        } catch (ParserConfigurationException e) {
            BotLogger.error(LOGTAG, e);
        } catch (SAXException e) {
            BotLogger.error(LOGTAG, e);
        } catch (IOException e) {
            BotLogger.error(LOGTAG, e);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }

    }

    public static boolean stringContainsItemFromList(String inputString, String[] items) {
        for(int i =0; i < items.length; i++) {
            if(inputString.contains(items[i]))
                return true;
        }
        return false;
    }

}