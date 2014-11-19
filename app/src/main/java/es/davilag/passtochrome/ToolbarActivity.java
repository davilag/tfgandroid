package es.davilag.passtochrome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import java.util.LinkedHashSet;
import java.util.Set;

import es.davilag.passtochrome.requests_content.RequestsFragment;


public class ToolbarActivity extends ActionBarActivity {


    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_toolbar);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.orange));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(toggle);

            /*
            Prueba para recuperar las urls guardadas;
             */
        SharedPreferences prefs = getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        Set<String> urls = prefs.getStringSet(Globals.URLS_SAVED,null);
        if(urls == null){
            urls = new LinkedHashSet<String>();
            for(int i = 0; i< 5 ; i++){
                urls.add("url"+i);
            }
        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RequestsFragment.update();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Globals.REFRESH_CONTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
