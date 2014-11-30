package es.davilag.passtochrome.requests_content;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import es.davilag.passtochrome.GcmIntentService;
import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;

/**
 * Created by davilag on 5/11/14.
 */
public class RequestsFragment extends Fragment {
    private static RecyclerCustomAdapter RVadapter;
    private static RecyclerView rv;
    private static RequestsFragment fragment;
    private static TextView tv;
    private static View rootView;
    private static Activity activity;

    private static void clearNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        activity = this.getActivity();
        clearNotification();
        SharedPreferences prefs = activity.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,new LinkedHashSet<String>());
        Log.e(Globals.TAG,"Entra en el framgent");
        rootView = inflater.inflate(R.layout.fragment_content_requests, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        ArrayList<RequestItem> contenido = new ArrayList<RequestItem>();
        Log.e(Globals.TAG,"Es distinto de null");
        Log.e(Globals.TAG,"El tamaño es: "+keys.size());
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String key = it.next();
            String domain = prefs.getString(key,null);
            if(domain!=null){
                contenido.add(new RequestItem(domain,key));
            }
        }
        RVadapter = new RecyclerCustomAdapter(getActivity(),contenido);
        rv.setAdapter(RVadapter);
        tv = (TextView) rootView.findViewById(R.id.textEmpty);
        if(keys!=null && keys.size()>0){
            tv.setText("");
        }else{
            tv.setText(getResources().getString(R.string.reqs_empty));
            tv.bringToFront();
            Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
            tv.setTypeface(tf);

        }
        return  rootView;
    }

    public static void update(){
        SharedPreferences prefs = activity.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,null);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(mLayoutManager);
        ArrayList<RequestItem> contenido = new ArrayList<RequestItem>();
        Log.e(Globals.TAG,"Es distinto de null");
        Log.e(Globals.TAG,"El tamaño es: "+keys.size());
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String key = it.next();
            String domain = prefs.getString(key,null);
            if(domain!=null){
                contenido.add(new RequestItem(domain,key));
            }
        }
        RVadapter = new RecyclerCustomAdapter(activity,contenido);
        rv.setAdapter(RVadapter);
        if(keys!=null && keys.size()>0){
            tv.setText("");
        }else{
            tv.setText(activity.getResources().getString(R.string.reqs_empty));
            tv.bringToFront();
            Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
            tv.setTypeface(tf);

        }
        clearNotification();
    }
}
