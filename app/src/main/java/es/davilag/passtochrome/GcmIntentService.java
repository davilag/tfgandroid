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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.FilaContenedor;
import es.davilag.passtochrome.database.Request;
import es.davilag.passtochrome.http.ServerMessage;

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
    private static int nveces = 0;


    public GcmIntentService() {
        super("GcmIntentService");
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

    private void handleRequest(Bundle bundle){
        Log.v(Globals.TAG,"Llega una peticion");
        String domain = bundle.getString(Globals.MSG_DOMAIN);
        final String reqId = bundle.getString(Globals.MSG_REQ_ID);
        Log.v(Globals.TAG,"El reqId es: "+reqId);
        if(domain!=null) {
            String[] users = BaseDatosWrapper.getUsers(getApplicationContext(),domain);//En un principio no se indica que usuario voy a coger.
            if(users!=null && users.length>0){
                new AsyncTask<String,Void,String[]>(){

                    @Override
                    protected String[] doInBackground(String... params) {
                        BaseDatosWrapper.insertRequest(getApplicationContext(),new Request(params[0],params[1]));
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
                }.execute(reqId,domain);
            }else{
                //No tengo ningun usuario del dominio de la peticion.
                SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                String regId = prefs.getString(Globals.REG_ID,"");
                String mail = prefs.getString(Globals.MAIL,"");
                try {
                    ServerMessage.sendResponseMessage(getApplicationContext(), mail, "", domain, Globals.NO_PASSWD, regId, reqId);
                    Log.v(Globals.TAG,"No tengo el usuario que me piden");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void handleClearNotification(Bundle bundle){
        Log.v(Globals.TAG,"Ha llegado un mensaje para borrar la notificacion");
        String reqId = bundle.getString(Globals.MSG_REQ_ID);
        BaseDatosWrapper.removeRequest(getApplicationContext(),reqId);
        Intent i = new Intent(Globals.REFRESH_CONTENT);
        sendBroadcast(i);
        clearNotification();
    }

    private void handleAddPass(Bundle bundle){
        Log.v(Globals.TAG,"Ha llegado un mensaje para añadir una contraseña.");
        String username = bundle.getString(Globals.MSG_USER);
        String pass = bundle.getString(Globals.MSG_PASSWD);
        String dom = bundle.getString(Globals.MSG_DOMAIN);
        String reqId = bundle.getString(Globals.MSG_REQ_ID);
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String mail = prefs.getString(Globals.MAIL,"");
        boolean added = BaseDatosWrapper.insertPass(getApplicationContext(),new FilaContenedor(username,pass,dom));
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... params) {
                try {
                    Log.v(Globals.TAG,"Se va a enviar el mensaje de respuesta al añadir.");
                    for(String s: params){
                        Log.v(Globals.TAG,s);
                    }
                    ServerMessage.sendSavedPassResponse(getApplicationContext(),params[0],params[1],params[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(reqId, "" + added, mail);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Globals.TAG, "Llega algo: "+nveces);
        nveces++;
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if(!extras.isEmpty()){
            Bundle bundle = intent.getExtras();
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                if(bundle!=null){
                    Set<String> keys = bundle.keySet();
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        Log.e(Globals.TAG,"[" + key + "=" + bundle.get(key)+"]");
                    }
                    String action = bundle.getString(Globals.MSG_ACTION);
                    String serverKey = bundle.getString(Globals.MSG_SERVER_KEY);
                    if(correctServerKey(serverKey)) {
                        if (action.equals(Globals.ACTION_REGISTERED)) {
                            Log.e(Globals.TAG, "Registro completo.");
                            String email = bundle.getString(Globals.MSG_MAIL, "");
                            setRegistered(email);
                        } else if (action.equals(Globals.ACTION_REQUEST)) {
                            handleRequest(bundle);
                        } else if (action.equals(Globals.ACTION_CLEARNOTIF)) {
                            handleClearNotification(bundle);
                        }else if(action.equals(Globals.ACTION_ADD_PASS)){
                            handleAddPass(bundle);
                        }
                    }else{
                        Log.e(Globals.TAG,"La serverKey no es correcta");
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
