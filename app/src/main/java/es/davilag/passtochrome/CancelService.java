package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.davilag.passtochrome.database.BaseDatosWrapper;

/**
 * Created by davilag on 01/10/14.
 */
public class CancelService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    public CancelService() {
        super("CancelService");
    }
    private void clearNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Globals.TAG,"He pulsado que no quiero responder a la peticion");
        clearNotification();
        String reqId = intent.getStringExtra(Globals.INTENT_REQ_ID);
        if(reqId!=null){
            BaseDatosWrapper.removeRequest(getApplicationContext(),reqId);
            Intent i = new Intent(Globals.REFRESH_CONTENT);
            i.putExtra(Globals.ACTION_BROAD,"");
            sendBroadcast(i);
        }
    }
}
