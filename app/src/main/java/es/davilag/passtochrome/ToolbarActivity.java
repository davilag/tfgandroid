package es.davilag.passtochrome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


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
        FragmentManager fm = getFragmentManager();
        DrawerFragment  drawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        fm.beginTransaction().replace(R.id.navigation_drawer_fragment,drawerFragment);
        FragmentManager fm2 = getFragmentManager();
        RequestsFragment requestsFragment = (RequestsFragment) getFragmentManager().findFragmentById(R.id.content_fragment);
        fm2.beginTransaction().replace(R.id.content_fragment,requestsFragment);
=======
        
        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.orange));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            public void onDrawerClosed(View view) {
                toolbar.setTitle("Cerrado");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                toolbar.setTitle("Abierto");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);
>>>>>>> 578512e0ed3106c87ef97cabc5c46a5b096c5de1
        */
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //updateUI();
            Log.v(Globals.TAG, "Ha llegado algo al recevier de broadcast");
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
    private void updateUI() {
        final FragmentTransaction fm2 = getSupportFragmentManager().beginTransaction();
        Fragment fr =  getSupportFragmentManager().findFragmentById(R.id.content_fragment);
        fm2.detach(fr);
        fm2.attach(fr);
        fm2.commit();

    }
}
