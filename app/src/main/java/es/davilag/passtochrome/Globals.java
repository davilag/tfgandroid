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
    public static final String SERVER_KEY = "server_key";


    //Constantes para los campos de los mensajes con el servidor GCM
    public static final String MSG_ACTION="action";
    public static final String MSG_ROLE="role";
    public static final String MSG_MAIL="mail";
    public static final String MSG_DOMAIN = "dominio";
    public static final String MSG_PASSWD = "password";
    public static final String MSG_REQ_ID = "req_id";
    public static final String MSG_SERVER_KEY="serverKey";
    public static final String MSG_USER = "usuario";
    public static final String MSG_PAYLOAD = "payload";
    public static final String MSG_IV = "iv";
    public static final String MSG_AAD = "aad";
    public static final String MSG_NONCE = "nonce";
    public static final String MSG_TS = "ts";
    public static final String MSG_STATE = "estado";



    //Constantes para las acciones de GCM
    public static final String ACTION_CONTAINER = "container";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_CLEARNOTIF = "clear_notification";
    public static final String ACTION_ADD_PASS = "addpass";

    //Constantes para eventos broadcast
    public static final String REFRESH_CONTENT = "es.davidavila.passtochrome.refreshContent";
    public static final String ACTION_BROAD = "es.davidavila.actionBroadcastRefresh";

    //Direccion del servidor, puede cambiar con la entrada de un mensaje.
    private static final String SERVER_IP = "192.168.1.41"; //193.147.49.31 192.168.1.41  192.168.0.102
    public static final String SERVER_DIR_SEC = "https://"+SERVER_IP+":8443";
    public static final String SERVER_DIR = "http://"+SERVER_IP+":8080";

    //Estados del mensaje de respuesta
    public static final String MSG_STATE_OK = "OK";
    public static final String MSG_STATE_NO_PASSWD ="noPass";
    public static final String MSG_STATE_FAIL = "fail";

    //Constantes para pasar parametros por los intents
    public static final String INTENT_REQ_ID = "reqId_INTENT";
    public static final String INTENT_DOM = "dom_INTENT";
    public static final String INTENT_CONTENT = "contentIntent";
    public static final String INTENT_USERS_DOM = "usersDom";
    public static final String INTENT_USER = "userIntent";

    public static final long TIMEOUT = 2*60*1000;

}
