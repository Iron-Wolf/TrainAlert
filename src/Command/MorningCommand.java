package Command;

import Ressources.BotConfig;
import org.apache.commons.codec.binary.Base64;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Command used for morning departure
 */
public class MorningCommand extends BotCommand {

    private static final String LOGTAG = "MORNING-COMMAND";

    public MorningCommand () {
        super("Matin", "Horaires Colombes > Paris");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = chat.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = user.getFirstName() + " " + user.getLastName();
        }

        StringBuilder messageBuilder = new StringBuilder("Etat du réseau : \n");

        try {
            InputStream content = getSNCFData(BotConfig.SNCF_URL_CP, BotConfig.SNCF_USER, BotConfig.SNCF_PASSWD);
            String message = getMessageJLine(content);

            if (message.isEmpty())
                messageBuilder.append("Aucun problèmes");
            else
                messageBuilder.append(message);

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

    /**
     * Get the XML data from a given URL (SNCF API)
     * @param urlString String of the url API
     * @param userName login
     * @param password password
     * @return Return full XML data
     * @throws IOException
     */
    private InputStream getSNCFData(String urlString, String userName, String password) throws IOException{
        URL url = new URL (urlString);
        Base64 b = new Base64();
        String encoding = b.encodeAsString((userName + ":" + password).getBytes());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setRequestProperty  ("Authorization", "Basic " + encoding);
        /*BufferedReader in = new BufferedReader (new InputStreamReader(content));
          String line;
          while ((line = in.readLine()) != null) {
            System.out.println(line);
          }*/
        return connection.getInputStream();
    }

    /**
     * Get the problem of an SNCF train, from an XML InputStream.<br/>
     * The InputStream must come from the SNCF real  time API.
     * @param content InputStream containing XML data
     * @return The String containing message
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private String getMessageJLine(InputStream content) throws ParserConfigurationException,SAXException,IOException{
        String message = "";

        // use BuilderFactory to parse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // create a new document from input stream
        Document doc = builder.parse(content);
        Element element = doc.getDocumentElement();
        NodeList nodes = element.getChildNodes();


        for (int i = 0; i < nodes.getLength(); i++) {
            // loop only TRAIN node
            if (nodes.item(i).getNodeName().equals("train")) {
                /*
                 0 : date
                 2 : num
                 4 : miss
                 6 : term
                 8 : etat (null if train OK)
                */
                try {
                    String etatNode = nodes.item(i).getChildNodes().item(8).getNodeName();
                    String etatText = nodes.item(i).getChildNodes().item(8).getTextContent();
                    String dateText = nodes.item(i).getChildNodes().item(0).getTextContent();
                    message += "Ligne J (" + dateText + ") : " + etatText + "\n";
                }catch (Exception e){}
            }
        }
        return message;
    }
}
