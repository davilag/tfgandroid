package es.davilag.passtochrome;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class ElegirUserActivity extends ActionBarActivity {
    private static void clearNotification(Context c)
    {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearNotification(getApplicationContext());
        setContentView(R.layout.activity_elegir_user);
        LinearLayout ll = (LinearLayout) findViewById(R.id.radioUsersContainer);
        Bundle extras = getIntent().getExtras();
        String[] users = extras.getStringArray(Globals.INTENT_USERS_DOM);
        String dom = extras.getString(Globals.INTENT_DOM);
        String reqId = extras.getString(Globals.INTENT_REQ_ID);

        if(users!=null&&dom!=null) {
            TextView tv = new TextView(this);
            tv.setText("Elige un usuario para el dominio " + dom + ":");
            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(RadioGroup.VERTICAL);
            for(int i = 0; i<users.length;i++){
                RadioButton button = new RadioButton(this);
                Log.v(Globals.TAG,"El usuario ahora es: "+users[i]);
                Log.v(Globals.TAG,"Voy por el numero:"+i);
                button.setText(users[i]);
                button.setId(i);
                rg.addView(button);
            }
            LinearLayout llChild = new LinearLayout(this);
            Button bResponder = new Button(this);
            bResponder.setText("Responder");
            bResponder.setOnClickListener(new BotonResponder(getApplicationContext(), rg, users,reqId));
            ll.addView(tv);
            ll.addView(rg);
            llChild.addView(bResponder);
            ll.addView(llChild);
        }else{
            finish();
        }
    }

    private class BotonResponder implements View.OnClickListener{
        private RadioGroup rg;
        private Context c;
        private String[] users;
        private String reqId;
        public BotonResponder(Context c, RadioGroup rg, String[] users, String reqId){
            this.rg = rg;
            this.c = c;
            this.users = users;
            this.reqId = reqId;
        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(c,ResponseService.class);
            i.putExtra(Globals.INTENT_USER,users[rg.getCheckedRadioButtonId()]);
            i.putExtra(Globals.INTENT_REQ_ID,this.reqId);
            startService(i);
            finish();
        }
    }
}
