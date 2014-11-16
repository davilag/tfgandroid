package es.davilag.passtochrome.requests_content;

import android.app.Fragment;
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
import java.util.Set;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;

/**
 * Created by davilag on 5/11/14.
 */
public class RequestsFragment extends Fragment {
    private static RecyclerCustomAdapter RVadapter;
    private static RecyclerView rv;
    private static RequestsFragment fragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,null);
        Log.e(Globals.TAG,"Entra en el framgent");

        if(keys!=null && keys.size()>0){
            View rootView = inflater.inflate(R.layout.fragment_content_requests, container, false);
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
            return  rootView;
        }else{
            View rootView = inflater.inflate(R.layout.fragment_empty,container,false);
            TextView tv = (TextView) rootView.findViewById(R.id.textEmpty);
            Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
            tv.setTypeface(tf);
            return  rootView;
        }

    }

    public static void update(){

    }
}
