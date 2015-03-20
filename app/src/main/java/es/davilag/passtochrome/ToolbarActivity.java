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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.davilag.passtochrome.container_content.ContainerFragment;
import es.davilag.passtochrome.drawer.DrawerFragment;
import es.davilag.passtochrome.requests_content.RequestsFragment;
import es.davilag.passtochrome.security.GaloisCounterMode;


public class ToolbarActivity extends ActionBarActivity {


    private ActionBarDrawerToggle toggle;
    private static Toolbar toolbar;
    private static DrawerLayout drawerLayout;
    private static Activity activity;
    private static String toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        activity = this;
        setContentView(R.layout.activity_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        String ventana = null;
        if(extras!=null){
            ventana= extras.getString(Globals.INTENT_CONTENT);
            Log.e(Globals.TAG,"extras no es null");
        }
        if(ventana==null) {
            Log.e(Globals.TAG,"Ventana es null");
            toolbarTitle = getResources().getString(R.string.container_title);
        }else{
            toolbarTitle = ventana;
        }
        Log.v(Globals.TAG,"Ventana sale siendo: "+toolbarTitle);
        toolbar.setTitle(toolbarTitle);
        toolbar.setBackgroundColor(getResources().getColor(R.color.orange));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.darkOrange));
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            public void onDrawerOpened(View drawerView){
                toolbar.setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu();
            }
            public void onDrawerClosed(View drawerView){
                toolbar.setTitle(toolbarTitle);
                setContent(toolbarTitle);
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
        if(toolbarTitle.equals(getResources().getString(R.string.container_title))) {
            DrawerFragment.performDrawerClick(0,R.string.container_title);
        }else if(toolbarTitle.equals(getResources().getString(R.string.requests_title))){
            DrawerFragment.performDrawerClick(1,R.string.requests_title);
        }
        setContent(toolbarTitle);
        Log.v(Globals.TAG,"Ha llegado al final de onCreate");
        Log.v(Globals.TAG,"CIFRADO2:");
        String key = "MTIzNDU2Nzg5MDk4NzY1NA==";
        String iv = "NzYzNDI1MTA5ODQ2MzgyNQ==";
        String input = null;
        try {
            input = Base64.encodeToString("Prueba".getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String aad = "12457423556432ghdf";
        GaloisCounterMode gcm = new GaloisCounterMode();
        try {
            String[] encrypt = gcm.GCMEncrypt(key, iv, input, aad);
            String[] decrypt = gcm.GCMDecrypt(key, iv, encrypt[0], aad);
            String decode = new String(Base64.decode(decrypt[0], Base64.DEFAULT),"UTF-8");
            System.out.println("Salida encrypt: " + encrypt[0]);
            System.out.println("Salida decrypt: "+decrypt[0]);
            System.out.println("Salida: " + decode);
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchProviderException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
            if(activity.getResources().getString(R.string.container_title).equals(fragmentName)) {
                content = new ContainerFragment();
                ContainerFragment.update();
            }else if(activity.getResources().getString(R.string.requests_title).equals(fragmentName)){
                content = new RequestsFragment();
                RequestsFragment.update();
            }else if("".equals(fragmentName)){
                content = new BlankFragment();
            }
            if(content!=null){
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                ft.replace(R.id.content_fragment, content);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
            toolbarTitle = fragmentName;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        ContainerFragment.update();
        RequestsFragment.update();
    }
}
