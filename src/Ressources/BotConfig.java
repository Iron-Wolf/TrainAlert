package Ressources;

/**
 * Bot ressources
 */
public class BotConfig {
    public static final String BOT_NAME = "TrainAlert";
    public static final String BOT_USERNAME = "trainalert_bot";

    // TODO : replace token
    public static final String BOT_TOKEN = "<bot token>";
    public static final String SNCF_USER = "<user token>";
    public static final String SNCF_PASSWD = "<passwd token>";

    // API's url (will change behavior later...)
    public static final String SNCF_URL_CP = "http://api.transilien.com/gare/87381079/depart/87384008/";
    public static final String SNCF_URL_PC = "http://api.transilien.com/gare/87384008/depart/87381079/";

    public static final String[] RATP_URLS = {"http://api-ratp.pierre-grimaud.fr/v2/traffic/metros/3?format=xml",
                                            "http://api-ratp.pierre-grimaud.fr/v2/traffic/metros/4?format=xml",
                                            "http://api-ratp.pierre-grimaud.fr/v2/traffic/metros/12?format=xml"};
}
