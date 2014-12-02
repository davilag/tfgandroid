package es.davilag.passtochrome;

/**
 * Created by davilag on 25/09/14.
 */
public class Globals {
    /*
    Constantes globales para acceder a las preferencias de la aplicacion
     */

    public static final String GCM_PREFS = "GCM prefs";
    public static final String TAG = "PTC";
    public static final String GCM_SENDER_ID = "877721156858";

    //Constantes para acceder a las preferencias de GCM
    public static final String REG_ID = "GCM RegId";
    public static final String REGISTERED ="GCM Registered";
    public static final String APP_VERSION ="GCM Version app";
    public static final String MAIL = "mail";
    public static final String MSG_REG_ID = "reg_id";
    public static final String REQUEST_SET = "requests_set";
    public static final String URLS_SAVED = "urls_saved";

    //Constantes para los campos de los mensajes con el servidor GCM
    public static final String MSG_ACTION="action";
    public static final String MSG_ROLE="role";
    public static final String MSG_MAIL="mail";
    public static final String MSG_DOMAIN = "dominio";
    public static final String MSG_PASSWD = "password";
    public static final String MSG_REQ_ID = "req_id";
    public static final String MSG_SERVER_KEY="serverKey";
    public static final String MSG_USER = "usuario";


    //Constantes para las acciones de GCM
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_CONTAINER = "container";
    public static final String ACTION_REGISTERED = "registered";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_RESPONSE = "response";
    public static final String ACTION_CLEARNOTIF = "clear_notification";
    public static final String ACTION_GET_URLS = "get_urls";

    //Constantes para eventos broadcast
    public static final String REFRESH_CONTENT = "es.davidavila.passtochrome.refreshContent";
    public static final String RESP_REQ = "es.davidavila.passtochrome.responseRequest";
    public static final String NOT_RESP_REQ ="es.davidavila.passtochrome.notResponseRequest";
    public static final String ACTION_BROAD = "es.davidavila.actionBroadcastRefresh";

    //Direccion del servidor, puede cambiar con la entrada de un mensaje.
    private static final String SERVER_IP = "193.147.49.34";
    public static final String SERVER_DIR = "http://"+SERVER_IP+":8080";

    //Constantes para pasar parametros por los intents
    public static final String INTENT_REQ_ID = "reqId_INTENT";
    public static final String INTENT_DOM = "dom_INTENT";
}
