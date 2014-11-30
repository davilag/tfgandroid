package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by davilag on 01/10/14.
 */
public class ResponseService extends IntentService{
    GoogleCloudMessaging gcm;
    /**
     * Servicio que arranca cuando se pulsa el boton de responder en la notificacion.
     *
     */
    public ResponseService() {
        super("ResponseService");
    }
    private void clearNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Globals.TAG,"He pulsado que quiero responder a la peticion.");
        gcm = GoogleCloudMessaging.getInstance(this);
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,null);
        String key = intent.getStringExtra(Globals.INTENT_REQ_ID);
        if(key==null) {

            if(keys!=null) {
                if (keys.size() == 1) {
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        key = it.next();
                        Log.v(Globals.TAG, key);
                    }
                }
            }
        }
        String dominio = null;
        if(key!=null)
            dominio = prefs.getString(key,"");
        Log.v(Globals.TAG,"El dominio es:"+dominio);
        if(dominio != null && !dominio.equalsIgnoreCase("")){
            if(keys!=null) {
                SharedPreferences.Editor editor = prefs.edit();
                keys.remove(key);
                editor.remove(key);
                editor.putStringSet(Globals.REQUEST_SET, keys);
                editor.commit();
                Log.v(Globals.TAG, "El dominio al que voy a responder es: " + dominio);
            }
        }
        String pass = dominio+"password";
        String mail = prefs.getString(Globals.MAIL,"");
        String regID = prefs.getString(Globals.REG_ID,"");
        /*
        Hilo para enviar el mensaje de respuesta.
         */
        new AsyncTask<String, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return ServerMessage.sendResponseMessage(params[0],params[1],params[2],params[3],params[4]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean resultado){
                if(resultado){
                    clearNotification();
                    Intent i = new Intent(Globals.REFRESH_CONTENT);
                    i.putExtra(Globals.ACTION_BROAD,"");
                    sendBroadcast(i);
                }
            }
        }.execute(mail,dominio,pass,regID,key);
    }
}
