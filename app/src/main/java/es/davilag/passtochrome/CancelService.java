package es.davilag.passtochrome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

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
        String key = intent.getStringExtra(Globals.INTENT_REQ_ID);
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,null);
        if(key==null){
            if(keys!=null&&keys.size()==1){
                Iterator<String> it = keys.iterator();
                if(it.hasNext())
                    key = it.next();
            }
        }
        if(key!=null){
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            keys.remove(key);
            editor.putStringSet(Globals.REQUEST_SET,keys);
            editor.commit();
            Intent i = new Intent(Globals.REFRESH_CONTENT);
            i.putExtra(Globals.ACTION_BROAD,"");
            sendBroadcast(i);
        }
    }
}
