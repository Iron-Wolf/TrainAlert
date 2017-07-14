package ressources;

import org.apache.commons.codec.binary.Base64;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Handle all necesary stuff related to API call,
 * such as retrieve and parse XML data
 */
public class APIWorker {

    private static APIWorker INSTANCE = new APIWorker();

    private APIWorker(){}

    public static APIWorker getInstance() {
        return INSTANCE;
    }

    /**
     * Get the stream data from a given URL
     * @param urlString URL of the remote ressource
     * @return {@link java.io.InputStream InputStream} of the remote ressource
     * @throws IOException If problem occur on connection
     */
    public InputStream getXMLData(String urlString) throws IOException{
        return getData(urlString, null, null);
    }

    /**
     * Get the stream data from the URL, with authentication
     * @param urlString URL of the remote ressource
     * @param userName login
     * @param password password
     * @return {@link java.io.InputStream InputStream} of the remote ressource
     * @throws IOException If problem occur on connection
     */
    public InputStream getXMLData(String urlString, String userName, String password) throws IOException{
        return getData(urlString, userName, password);
    }

    /**
     * main.Main method to download XML data
     * @param urlString Remote ressource
     * @param userName Needed for authentication
     * @param password Needed for authentication
     * @return {@link java.io.InputStream InputStream} of the remote ressource
     * @throws IOException If problem occur on connection
     */
    private InputStream getData(String urlString, String userName, String password) throws IOException {
        URL url = new URL (urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        if (userName!=null || password!=null) {
            Base64 b = new Base64();
            String encoding = b.encodeAsString((userName + ":" + password).getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encoding);
        }
        return connection.getInputStream();
    }

    /**
     * Get the problem of an SNCF train, from an XML InputStream.<br/>
     * The InputStream must come from the SNCF real time API.
     * @param content {@link java.io.InputStream InputStream} containing XML data
     * @return The String containing message
     * @throws IOException If problem occur on connection
     */
    public String getMessageJLine(InputStream content) throws IOException {
        String message = "";

        // use SAXBuilder to parse XML
        SAXBuilder builder = new SAXBuilder();

        try {
            // create a new document from input stream
            Document doc = builder.build(content);
            Element root = doc.getRootElement();
            List trains = root.getChildren();
            Iterator i = trains.iterator();
            while (i.hasNext()) {
                Element train = (Element) i.next();

                // loop on each element and retrieve the node if he exists
                List<Element> elements = train.getChildren();
                for (Element element : elements) {
                    if (element.getName().equals("etat")){
                        String etatText = train.getChild("etat").getText();
                        String dateText = train.getChild("date").getText();
                        message += "<b>Ligne J</b> (" + dateText + ") : " + etatText + "\n";
                    }
                }
            }
        } catch (JDOMException e) {
            message += "Oups " + Emoji.FROWNING_FACE + "\n";
        }
        return message;
    }

    /**
     * Get the alert of a subway, from the given XML InputStream.<br/>
     * The InputStream must come from the RATP API.
     * @param content {@link java.io.InputStream InputStream} containing XML data
     * @param allInfo <b>true</b> return the message.
     *                <b>false</b> return message if subway has trouble
     * @return Message to the user
     * @throws IOException If problem occur on connection
     */
    public String getMessageSubway(InputStream content, boolean allInfo) throws IOException {
        String message = "";

        // use SAXBuilder to parse XML
        SAXBuilder builder = new SAXBuilder();

        try {
            // create a new document from input stream
            Document doc = builder.build(content);
            Element root = doc.getRootElement();
            String lineText = root.getChild("result").getChild("line").getText();
            String slugText = root.getChild("result").getChild("slug").getText();
            String messageText = root.getChild("result").getChild("message").getText();

            // check the flag and get the message if TRUE
            if (allInfo) {
                message += "<b>Ligne " + lineText + " :</b> " + messageText + "\n";
            }
            // get the message if SLUG is not normal
            else if (!slugText.contains("normal")) {
                message += "<b>Ligne " + lineText + "</b> : " + messageText + "\n";
            }
        } catch (JDOMException e) {
            message += "Oups " + Emoji.FROWNING_FACE + "\n";
        }

        return message;
    }
}
