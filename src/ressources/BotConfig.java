package ressources;

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
    public static final String SNCF_URL = "http://api.transilien.com/gare/%s/depart/%s/";
    public static final Integer COLOMBES = 87381079;
    public static final Integer ST_LAZARD = 87384008;

    public static final String RATP_SUBWAY_URL = "http://api-ratp.pierre-grimaud.fr/v2/traffic/metros/%s?format=xml";
    public static final Integer[] SUBWAY_LIST = {3, 4, 12};

    public static final String RATP_RER_URL = "http://api-ratp.pierre-grimaud.fr/v2/traffic/rers/%s?format=xml";
}
