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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Particular command used to retrieve status of any subway line
 */
public class SubwayCommand extends BotCommand{
    private static final String LOGTAG = "SUBWAY-COMMAND";

    public SubwayCommand() {
        super("Metro", "Info sur n'importe quel métro");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder messageBuilder = new StringBuilder("");
        String message = "";

        if (arguments.length <= 0)
        {
            message = "Il faut au moins 1 paramètre";
        }

        try {
            // loop on each argument
            for (String subwayLine : arguments) {
                // get message if the argument is a valid subway line
                try {
                    if (Integer.parseInt(subwayLine) > 0 && Integer.parseInt(subwayLine) < 15) {
                        String fullRATPUrl = String.format(BotConfig.RATP_URLS, subwayLine);
                        InputStream ratpContent = APIWorker.getInstance().getXMLData(fullRATPUrl);
                        message += APIWorker.getInstance().getMessageSubway(ratpContent, true);
                    }
                    else
                    {
                        message += "metro " + Integer.parseInt(subwayLine) + " inconnue\n";
                    }
                } catch (NumberFormatException e){
                    message += "metro <b>" + subwayLine + "</b> incorrecte\n";
                }
            }

            messageBuilder.append(message);

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
