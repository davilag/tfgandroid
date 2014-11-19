package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
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
    private void sendResponseMessage(Bundle data){
        new AsyncTask<Bundle,Void,String>(){

            @Override
            protected String doInBackground(Bundle... params) {
                try{
                    Bundle databg = params[0];
                    SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                    int msgId = prefs.getInt(Globals.MESSAGE_ID,0);
                    String id = Integer.toString(++msgId);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(Globals.MESSAGE_ID,Integer.parseInt(id));
                    editor.commit();
                    gcm.send(Globals.GCM_SENDER_ID+"@gcm.googleapis.com",id,Globals.GCM_TIME_TO_LIVE,databg);
                    return "Mensaje enviado";
                }catch (IOException e){
                    e.printStackTrace();
                    return "Error";
                }
            }

            protected void onPostExecute(String msg){
                Log.e(Globals.TAG,"Mensaje de respuesta enviado: "+msg);
                clearNotification();
            }
        }.execute(data);
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
    /*
    Bundle data = new Bundle();
    data.putString(Globals.MSG_PASSWD,pass);
    data.putString(Globals.MSG_MAIL, mail);
    data.putString(Globals.MSG_DOMAIN, dominio);
    data.putString(Globals.MSG_ACTION,Globals.ACTION_RESPONSE);
    */
        try {
            String regID = prefs.getString(Globals.REG_ID,"");
            ServerMessage.sendResponseMessage(mail,dominio,pass,regID,key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearNotification();
        Intent i = new Intent(Globals.REFRESH_CONTENT);
        i.putExtra(Globals.ACTION_BROAD,"");
        sendBroadcast(i);
    }
}
