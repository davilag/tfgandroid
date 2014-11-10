package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private void sendRequestNotificacion(String domain){
        Intent responseIntent = new Intent(this, ResponseService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        responseIntent.setAction(Globals.RESP_REQ);
        PendingIntent piRes = PendingIntent.getService(this,0,responseIntent,0);

        Intent noResponseIntent = new Intent (this,CancelService.class);
        noResponseIntent.setAction(Globals.NOT_RESP_REQ);
        PendingIntent piNotRes = PendingIntent.getService(this, 0, noResponseIntent,0);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono_notif)
                .setContentTitle("Peticion Pass to Chrome")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Peticion de contraseña para: " + domain))
                .setContentText("Peticion de contraseña para: " + domain)
                .setTicker("Peticion de contraseña para: " + domain)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .addAction(R.drawable.ic_reply_white_24dp,"Enviar",piRes)
                .addAction(R.drawable.ic_clear_white_24dp,"Cancelar",piNotRes);
        mNotificationManager.notify(NOTIFICATION_ID,builder.build());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Globals.TAG, "Llega algo");
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
                    if(action.equals(Globals.ACTION_REGISTERED)){
                        Log.e(Globals.TAG,"Registro completo.");
                        String email = bundle.getString(Globals.MSG_MAIL,"");
                        setRegistered(email);
                    }else if(action.equals(Globals.ACTION_REQUEST)){
                        Log.v(Globals.TAG,"Llega una peticion");
                        String domain = bundle.getString(Globals.MSG_DOMAIN);
                        String reqId = bundle.getString(Globals.MSG_REQ_ID);
                        if(domain!=null) {
                            sendRequestNotificacion(domain);
                            SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            Set<String> reqSet = prefs.getStringSet(Globals.REQUEST_SET,null);
                            if(reqSet==null){
                                Log.v(Globals.TAG,"El set de requests es null");
                                reqSet = new LinkedHashSet<String>();
                            }
                            reqSet.add(reqId);
                            Iterator<String> iterator = reqSet.iterator();
                            editor.putString(reqId,domain);
                            editor.putString(Globals.DOMAIN, domain);
                            editor.putStringSet(Globals.REQUEST_SET,reqSet);
                            editor.commit();
                            Log.v(Globals.TAG,"El set que voy a guardar es:");
                            while(iterator.hasNext()){
                                String key = iterator.next();
                                Log.v(Globals.TAG,key+": "+prefs.getString(key,""));
                            }
                            Intent i = new Intent(Globals.REFRESH_CONTENT);
                            sendBroadcast(i);
                        }
                    }else if(action.equals(Globals.ACTION_CLEARNOTIF)){
                        Log.v(Globals.TAG,"Ha llegado un mensaje para borrar la notificacion");
                        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String reqId = bundle.getString(Globals.MSG_REQ_ID);
                        Set<String> reqSet = prefs.getStringSet(Globals.REQUEST_SET,null);
                        if(reqSet!=null){
                            reqSet.remove(reqId);
                            editor.putStringSet(Globals.REQUEST_SET,reqSet);
                            editor.remove(reqId);
                        }
                        editor.commit();
                        clearNotification();
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
