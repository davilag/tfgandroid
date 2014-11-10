package es.davilag.passtochrome.requests_content;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;

/**
 * Created by davilag on 5/11/14.
 */
public class RequestsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        ArrayList<RequestItem> list = new ArrayList<RequestItem>();
        Set<String> keys = prefs.getStringSet(Globals.REQUEST_SET,null);
        Log.e(Globals.TAG,"Entra en el framgent");
        if(keys!=null && keys.size()!=0){
            LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_content_requests, container, false);
            ListView lv = (ListView)rootView.findViewById(R.id.list_requests);
            final Context c = rootView.getContext();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(c, "He tocado algo", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(Globals.TAG,"Es distinto de null");
            Log.e(Globals.TAG,"El tamaño es: "+keys.size());
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
                String key = it.next();
                String domain = prefs.getString(key,null);
                if(domain!=null){
                    list.add(new RequestItem(domain,key));
                }
            }
            ListRequestsAdapter adapter = new ListRequestsAdapter(c,R.layout.content_request_row,list);
            lv.setAdapter(adapter);
            return rootView;
        }else {
            RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_empty, container, false);
            TextView tv = (TextView) rootView.findViewById(R.id.textEmpty);
            Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
            tv.setTypeface(tf);
            return rootView;
        }

    }
}
