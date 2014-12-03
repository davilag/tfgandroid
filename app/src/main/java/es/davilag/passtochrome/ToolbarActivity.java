package es.davilag.passtochrome;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import es.davilag.passtochrome.container_content.ContainerFragment;
import es.davilag.passtochrome.drawer.DrawerFragment;
import es.davilag.passtochrome.requests_content.RequestsFragment;


public class ToolbarActivity extends ActionBarActivity {


    private ActionBarDrawerToggle toggle;
    private static Toolbar toolbar;
    private static DrawerLayout drawerLayout;
    private static Activity activity;
    private static String toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = Globals.TITLE_CONTENEDOR;
        toolbar.setTitle(toolbarTitle);
        toolbar.setBackgroundColor(getResources().getColor(R.color.orange));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            public void onDrawerOpened(View drawerView){
                toolbar.setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu();
            }
            public void onDrawerClosed(View drawerView){
                toolbar.setTitle(toolbarTitle);
                invalidateOptionsMenu();
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        Fragment drawer = new DrawerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.navigation_drawer_fragment,drawer);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
        setContent(Globals.TITLE_CONTENEDOR);
        DrawerFragment.performDrawerClick(0);
        Log.v(Globals.TAG,"Ha llegado al final de onCreate");
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

    public static void openDrawer(){
        if(drawerLayout!=null&&toolbar!=null&&activity!=null){
            drawerLayout.closeDrawer(Gravity.LEFT);
            toolbar.setTitle(activity.getResources().getString(R.string.app_name));
            activity.invalidateOptionsMenu();
        }
    }

    public static void closeAndSetTitle(CharSequence title){
        Log.v(Globals.TAG,"Voy a poner: "+title);
        if(drawerLayout!=null&&toolbar!=null&&activity!=null){
            toolbarTitle = title.toString();
            if(drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            toolbar.setTitle(title);
            activity.invalidateOptionsMenu();
        }
    }

    public static void setContent(String fragmentName){
        if(activity!=null){
            Fragment content = null;
            if(Globals.TITLE_CONTENEDOR.equals(fragmentName)) {
                content = new ContainerFragment();
            }else if(Globals.TITLE_PETICIONES.equals(fragmentName)){
                content = new RequestsFragment();
            }
            if(content!=null){
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.replace(R.id.content_fragment, content);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }
}
