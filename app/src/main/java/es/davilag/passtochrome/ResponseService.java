package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.Request;
import es.davilag.passtochrome.http.ServerMessage;

/**
 * Created by davilag on 01/10/14.
 */
public class ResponseService extends IntentService{
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
        final SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        final String reqId = intent.getStringExtra(Globals.INTENT_REQ_ID);
        if(reqId!=null) {
            final Request req = BaseDatosWrapper.getAndRemoveRequestDomain(getApplicationContext(),reqId);
            String user = intent.getStringExtra(Globals.INTENT_USER);
            /*
            Hilo para enviar el mensaje de respuesta.
             */
            if(req!=null){
                new AsyncTask<Object, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Object... params) {
                        Request req = (Request)params[0];
                        String user;
                        if(params[1]==null){
                            String[] users = BaseDatosWrapper.getUsers(getApplicationContext(),req.getDom());
                            user = users[0];
                        }else{
                            user = (String)params[1];
                        }
                        String[] ivPass = BaseDatosWrapper.getPass(getApplicationContext(),req.getDom(),user); //posicion 0 iv posicion 1 pass y user cifrados
                        String mail = prefs.getString(Globals.MAIL, "");
                        Long nonce = new Long(req.getNonce());
                        nonce ++;
                        String ivPassAlm = ivPass[0];
                        String pass = ivPass[1];
                        Log.e(Globals.TAG, "Voy a responder");
                        Log.v(Globals.TAG,"dominio: '"+params[0]+"'");
                        Log.v(Globals.TAG,"pass: "+pass);
                        Log.v(Globals.TAG,"mail: "+mail);
                        Log.v(Globals.TAG,"Llega una peticion");
                        try {
                            return ServerMessage.sendResponseMessage(getApplicationContext(), req.getDom(), pass, ivPassAlm, reqId,nonce,Globals.MSG_STATE_OK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean resultado) {
                        if (resultado) {
                            clearNotification();
                            Intent i = new Intent(Globals.REFRESH_CONTENT);
                            i.putExtra(Globals.ACTION_BROAD, "");
                            sendBroadcast(i);
                        }
                    }
                }.execute(req,user);
            }
        }
    }
}
