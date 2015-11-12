package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.FilaContenedor;
import es.davilag.passtochrome.database.Request;
import es.davilag.passtochrome.http.ServerMessage;
import es.davilag.passtochrome.security.GaloisCounterMode;
import es.davilag.passtochrome.security.Security;

/**
 * Servicio que se ejecuta cuando llega un mensaje de GCM para analizarlo.
 */
public class GcmIntentService extends IntentService {
    /**
     * Servicio que arranca cuando llega un mensaje de GCM a la aplicacion.
     *
     */
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    /*
    Metodo para ver si estoy registrado en el servicio de PTC.
     */
    private boolean amIRegistered(){
        SharedPreferences gcmPrefs = getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        return gcmPrefs.getBoolean(Globals.REGISTERED,false);
    }

    private boolean correctServerKey(String serverKey){
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String serverKeyAlm = prefs.getString(Globals.SERVER_KEY,"");
        return serverKey.equals(serverKeyAlm);
    }
    /*
    Deja la aplicacion en este movil como registrada, guardando
    el email con el que se ha registrado el usuario y no dejando
    que el usuario se vuelva a registrar en un futuro.
     */
    private void setRegistered(String email){
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Globals.REGISTERED,true);
        Log.e(Globals.TAG,"Guardo el email en SharedPreferences que es "+email);
        editor.putString(Globals.MAIL,email);
        editor.commit();
    }

    private void sendRequestActionNotification(String domain, String reqId){
        String[] users = BaseDatosWrapper.getUsers(getApplicationContext(),domain);
        Intent responseIntent;
        PendingIntent piRes;
        if(users.length>1){
            responseIntent = new Intent(this,ElegirUserActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);;
            responseIntent.putExtra(Globals.INTENT_DOM,domain);
            responseIntent.putExtra(Globals.INTENT_USERS_DOM,users);
            responseIntent.putExtra(Globals.INTENT_REQ_ID,reqId);
            piRes = PendingIntent.getActivity(this,0,responseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            responseIntent= new Intent(this, ResponseService.class);
            responseIntent.putExtra(Globals.INTENT_REQ_ID,reqId);
            responseIntent.setAction(reqId); //Hay que poner para cada vez que salga una notificacion una accion diferente o reciclará el primer intent que se ejecute.
            piRes = PendingIntent.getService(this,0,responseIntent,0);
        }

        Intent noResponseIntent = new Intent (this,CancelService.class);
        noResponseIntent.putExtra(Globals.INTENT_REQ_ID,reqId);
        noResponseIntent.setAction(reqId);
        PendingIntent piNotRes = PendingIntent.getService(this, 0, noResponseIntent,0);

        Intent requestsIntent = new Intent(this, ToolbarActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        requestsIntent.putExtra(Globals.INTENT_CONTENT,getResources().getString(R.string.requests_title));
        PendingIntent piRequests = PendingIntent.getActivity(this, 0, requestsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono_notif)
                .setContentTitle("Pass to Chrome")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Peticion de contraseña para: " + domain))
                .setContentText("Peticion de contraseña para: " + domain)
                .setTicker("Peticion de contraseña para: " + domain)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .addAction(R.drawable.ic_reply_white_24dp,"Enviar",piRes)
                .addAction(R.drawable.ic_clear_white_24dp, "Cancelar", piNotRes).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(piRequests);
        mNotificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    private String generateNotificationBigMessage(String [] domains){
        String msg = "";
        for(String s: domains){
            msg+="Peticion pendiente de: "+s+"\n";
        }
        return msg;
    }

    private void sendRequestNotification(String[] domains) {
        Intent requestsIntent = new Intent(this, ToolbarActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);;
        PendingIntent piRequests = PendingIntent.getActivity(this, 0, requestsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        requestsIntent.putExtra(Globals.INTENT_CONTENT,getResources().getString(R.string.requests_title));

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono_notif)
                .setContentTitle("Pass To Chrome")
                .setContentText("Varias peticiones pendientes.")
                .setTicker("Varias peticiones pendientes.")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(piRequests)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(generateNotificationBigMessage(domains)));
        mNotificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    private void handleRequest(Message message, final String reqId){
        Log.v(Globals.TAG,"Llega una peticion");
        String domain = (String)message.value(Globals.MSG_DOMAIN);
        Log.v(Globals.TAG, "El dominio de entrada es: "+domain);
        Log.v(Globals.TAG,"El reqId es: "+reqId);
        Long nonce = new Long((int)message.value(Globals.MSG_NONCE));
        if(domain!=null) {
            String[] users = BaseDatosWrapper.getUsers(getApplicationContext(),domain);//En un principio no se indica que usuario voy a coger.
            if(users!=null && users.length>0){
                new AsyncTask<String,Void,String[]>(){

                    @Override
                    protected String[] doInBackground(String... params) {
                        BaseDatosWrapper.insertRequest(getApplicationContext(),new Request(params[0],params[1],params[2]));
                        ArrayList<Request> requests = BaseDatosWrapper.getRequests(getApplicationContext());
                        String[] domains = new String[requests.size()];
                        int i = 0;
                        for(Request r: requests){
                            domains[i] = r.getDom();
                            i++;
                        }
                        return domains;
                    }
                    @Override
                    protected void onPostExecute(String[] doms)
                    {
                        if(doms.length>1){
                            sendRequestNotification(doms);
                        }else{
                            Log.v(Globals.TAG,"El reqId que le voy a pasar a la notif es: "+reqId);
                            sendRequestActionNotification(doms[0],reqId);
                        }
                        Intent intent = new Intent(Globals.REFRESH_CONTENT);
                        sendBroadcast(intent);
                    }
                }.execute(reqId,domain,nonce.toString());
            }else{
                //No tengo ningun usuario del dominio de la peticion.
                Log.v(Globals.TAG,"No tengo ningun usuario con el dominio qe me piden");
                SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                try {
                   ServerMessage.sendResponseMessage(getApplicationContext(), domain, "", "", reqId,++nonce,Globals.MSG_STATE_NO_PASSWD);
                    Log.v(Globals.TAG,"No tengo el usuario que me piden");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void handleClearNotification(String aad){
        Log.v(Globals.TAG,"Ha llegado un mensaje para borrar la notificacion");
        BaseDatosWrapper.removeRequest(getApplicationContext(),aad);
        Intent i = new Intent(Globals.REFRESH_CONTENT);
        sendBroadcast(i);
        clearNotification();
    }

    private void handleAddPass(Message bundle,String aad){
        Log.v(Globals.TAG,"Ha llegado un mensaje para añadir una contraseña.");
        String username = (String)bundle.value(Globals.MSG_USER);
        String pass = (String)bundle.value(Globals.MSG_PAYLOAD);
        String dom =(String) bundle.value(Globals.MSG_DOMAIN);
        String reqId = aad;
        String iv = (String)bundle.value(Globals.MSG_IV);
        Long nonce = new Long((int)bundle.value(Globals.MSG_NONCE));
        nonce ++;
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String mail = prefs.getString(Globals.MAIL,"");
        Boolean added = BaseDatosWrapper.insertPass(getApplicationContext(),new FilaContenedor(username,pass,dom,iv));
        Log.v(Globals.TAG,"Se ha insetado ya en la bbdd");
        new AsyncTask<Object,Void,Void>(){

            @Override
            protected Void doInBackground(Object... params) {
                try {
                    Log.v(Globals.TAG,"Se va a enviar el mensaje de respuesta al añadir.");
                    ServerMessage.sendSavedPassResponse(getApplicationContext(),(String)params[0],(Boolean)params[1],(String)params[2],(Long)params[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(reqId, added, mail, nonce);
    }

    private boolean correctTimestamp(long timestamp){
        return (new Date().getTime()-timestamp)<Globals.TIMEOUT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if(!extras.isEmpty()){
            Bundle bundle = intent.getExtras();
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                if(bundle!=null){
                    String aad = bundle.getString(Globals.MSG_AAD);
                    String iv = bundle.getString(Globals.MSG_IV);
                    if(aad!=null && iv!=null && amIRegistered()) {
                        String payloadCipher = bundle.getString(Globals.MSG_PAYLOAD);
                        String serverKey = Security.getServerKey(getApplicationContext());
                        try {
                            String payloadPlain = GaloisCounterMode.GCMDecrypt(serverKey, iv, payloadCipher, aad);
                            ObjectMapper om = new ObjectMapper();
                            Message message = om.readValue(payloadPlain, Message.class);
                            String action = (String) message.value(Globals.MSG_ACTION);
                            long timestamp = (long)message.value(Globals.MSG_TS);
                            if (correctServerKey(serverKey)&&correctTimestamp(timestamp)) {
                                if (action.equals(Globals.ACTION_REQUEST)) {
                                    handleRequest(message, aad);
                                } else if (action.equals(Globals.ACTION_CLEARNOTIF)) {
                                    if(aad.equals(message.value(Globals.MSG_REQ_ID))){
                                        handleClearNotification(aad);
                                    }

                                } else if (action.equals(Globals.ACTION_ADD_PASS)) {
                                    handleAddPass(message,aad);
                                }
                            } else {
                                Log.e(Globals.TAG, "La serverKey no es correcta");
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchProviderException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        Log.v(Globals.TAG,"Llega aqui");
    }
    private void clearNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }
}
