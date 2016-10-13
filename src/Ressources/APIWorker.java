package Ressources;

import org.apache.commons.codec.binary.Base64;
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
 * Handle all necesary stuff related to API call,
 * such as retrieve and parse XML data
 */
public class APIWorker {

    private APIWorker(){}

    private static APIWorker INSTANCE = new APIWorker();

    public static APIWorker getInstance() {
        return INSTANCE;
    }

    /**
     * Get the stream data from a given URL
     * @param urlString URL of the remote ressource
     * @return {@link java.io.InputStream InputStream} of the remote ressource
     * @throws IOException
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
     * @throws IOException
     */
    public InputStream getXMLData(String urlString, String userName, String password) throws IOException{
        return getData(urlString, userName, password);
    }

    /**
     * Main method to download XML data
     * @param urlString Remote ressource
     * @param userName Needed for authentication
     * @param password Needed for authentication
     * @return {@link java.io.InputStream InputStream} of the remote ressource
     * @throws IOException
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
        /* // debug //
          BufferedReader in = new BufferedReader (new InputStreamReader(content));
          String line;
          while ((line = in.readLine()) != null) {
            System.out.println(line);
          }*/
        return connection.getInputStream();
    }

    /**
     * Get the problem of an SNCF train, from an XML InputStream.<br/>
     * The InputStream must come from the SNCF real time API.
     * @param content {@link java.io.InputStream InputStream} containing XML data
     * @return The String containing message
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String getMessageJLine(InputStream content) throws ParserConfigurationException,SAXException,IOException {
        String message = "";

        // use BuilderFactory to parse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // create a new document from input stream
        Document doc = builder.parse(content);
        Element element = doc.getDocumentElement();
        NodeList nodes = element.getChildNodes();


        for (int i = 0; i < nodes.getLength(); i++) {
            // loop only on TRAIN node
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

    /**
     * Get the alert of a subway, from the given XML InputStream.<br/>
     * The InputStream must come from the RATP API.
     * @param content {@link java.io.InputStream InputStream} containing XML data
     * @param allInfo <b>true</b> return the message.
     *                <b>false</b> return message if subway has trouble
     * @return Message to the user
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String getMessageSubway(InputStream content, boolean allInfo) throws ParserConfigurationException,SAXException,IOException {
        String message = "";

        // use BuilderFactory to parse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // create a new document from input stream
        Document doc = builder.parse(content);
        Element element = doc.getDocumentElement();
        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            // get result if SLUG is not normal
            if (nodes.item(i).getNodeName().equals("response")) {
                /*
                 0 : line
                 1 : slug
                 2 : title
                 3 : message
                */
                try {
                    String lineNode = nodes.item(i).getChildNodes().item(0).getTextContent();
                    String slugText = nodes.item(i).getChildNodes().item(1).getTextContent();
                    String messageText = nodes.item(i).getChildNodes().item(3).getTextContent();
                    if (allInfo) {
                        message += "metro (" + lineNode + ") : " + messageText + "\n";
                    }
                    else if (!slugText.contains("normal")) {
                        message += "metro (" + lineNode + ") : " + messageText + "\n";
                    }
                }catch (Exception e){}
            }
        }
        return message;
    }
}
