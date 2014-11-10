package es.davilag.passtochrome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends Activity {

    /*
    Globales de MainActivity
     */
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    AtomicInteger msgId = new AtomicInteger();
    private EditText editMail;
    private Button botonReg;
    private EditText editMsg;
    private Button botonSendMsg;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(Globals.TAG,"Ha llegado un mensaje al receiver.");
            setRegisterDisabled();
        }
    };

    private boolean amIRegistered(){
        SharedPreferences gcmPrefs = getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        return gcmPrefs.getBoolean(Globals.REGISTERED,false);
    }

    private static int getAppVersion(Context context)
    {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("No se pudo obtener la version del paquete");
        }
    }

    private String getRegid(Context context){
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String regId = prefs.getString(Globals.REG_ID,"");
        if(regId == null || regId.equalsIgnoreCase("")){
            Log.v(Globals.TAG,"RegId no encontrado.");
            return  "";
        }

        int registeredVersion = prefs.getInt(Globals.APP_VERSION,Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if(registeredVersion!=currentVersion){
            Log.v(Globals.TAG, "App actualizada");
            return "";
        }
        return regId;
    }
    private void storeRegId(Context context, String regId){
        final SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.REG_ID,regId);
        editor.putInt(Globals.APP_VERSION,appVersion);
        editor.commit();

    }


    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void sendRegisterMessage(String mail){
        new AsyncTask<String,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(String... params) {
                try{
                    /*
                    Bundle data = new Bundle();
                    data.putString(Globals.MSG_ACTION,Globals.ACTION_REGISTER);
                    data.putString(Globals.MSG_MAIL,params[0]);
                    data.putString(Globals.MSG_ROLE,Globals.ACTION_CONTAINER);
                    SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                    msgId.set(prefs.getInt(Globals.MESSAGE_ID,0));
                    String id = Integer.toString(msgId.incrementAndGet());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(Globals.MESSAGE_ID,Integer.parseInt(id));
                    editor.commit();
                    gcm.send(Globals.GCM_SENDER_ID+"@gcm.googleapis.com",id,Globals.GCM_TIME_TO_LIVE,data);
                    return "Mensaje enviado";
                    */
                    SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
                    String regId = prefs.getString(Globals.REG_ID,"");
                    if(regId!="")
                        return ServerMessage.sendRegisterMessage(params[0],regId,context);
                    return false;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

            protected void onPostExecute(Boolean msg){
                if(!msg){
                    Log.e(Globals.TAG,"Ha habido un problema al registrarse");
                }else{
                    Log.v(Globals.TAG,"Mensaje de registro enviado con exito.");
                }
            }
        }.execute(mail);
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(Globals.REFRESH_CONTENT));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegid(context);
        botonReg = (Button) findViewById(R.id.buttonReg);
        botonSendMsg = (Button) findViewById(R.id.buttonSendMsg);
        editMail = (EditText) findViewById(R.id.editMail);
        editMsg = (EditText) findViewById(R.id.editMsg);
        botonReg.setEnabled(!amIRegistered());
        if(checkPlayServices()){
            regid = getRegid(context);
            if(!amIRegistered()){
                registerInBackground();
            }
        }
        botonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegisterMessage(editMail.getText().toString());
            }
        });
    }

    private void registerInBackground(){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                Log.v(Globals.TAG,"Entra en register in background");
                String msg = "";
                try{
                    if(gcm==null){
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Globals.GCM_SENDER_ID);
                    Log.e(Globals.TAG,"RegId = "+regid);
                    storeRegId(context,regid);
                    msg = "Aparato registrado: "+regid;
                }catch (IOException e){
                    msg = "Error: "+e.getMessage();
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg)
            {

            }
        }.execute(null,null,null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRegisterDisabled(){
        if(botonReg!=null){
            botonReg.setEnabled(false);
        }
        Toast.makeText(this,"Registrado con exito",Toast.LENGTH_SHORT).show();
    }
}
