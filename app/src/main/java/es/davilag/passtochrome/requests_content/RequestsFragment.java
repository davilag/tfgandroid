package es.davilag.passtochrome.requests_content;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
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

import es.davilag.passtochrome.ContentFragment;
import es.davilag.passtochrome.GcmIntentService;
import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;
import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.Request;

/**
 * Created by davilag on 5/11/14.
 */
public class RequestsFragment extends ContentFragment{
    private static RecyclerRequestAdapter RVadapter;
    private static RecyclerView rv;
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
        Log.e(Globals.TAG,"Entra en el framgent de peticiones");
        rootView = inflater.inflate(R.layout.fragment_content_requests, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        ArrayList<Request> contenido = BaseDatosWrapper.getRequests(rootView.getContext());
        Log.e(Globals.TAG,"Voy a coger las requests de la base de datos");
        RVadapter = new RecyclerRequestAdapter(getActivity(),contenido);
        rv.setAdapter(RVadapter);
        tv = (TextView) rootView.findViewById(R.id.textEmpty);
        if(contenido.size()>0){
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
        if(rv!=null){
            rv.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
            rv.setLayoutManager(mLayoutManager);
            ArrayList<Request> contenido = BaseDatosWrapper.getRequests(rootView.getContext());
            Log.e(Globals.TAG,"Voy a coger las requests de la base de datos");
            for(Request r : contenido){
                Log.v(Globals.TAG,"ID: "+r.getReqId()+ " Dominio: "+r.getDom());
            }
            RVadapter = new RecyclerRequestAdapter(activity,contenido);
            rv.setAdapter(RVadapter);
            tv = (TextView) rootView.findViewById(R.id.textEmpty);
            if(contenido.size()>0){
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
}
