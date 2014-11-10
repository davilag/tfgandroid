package es.davilag.passtochrome.drawer;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;

/**
 * Created by davilag on 29/10/14.
 */
public class DrawerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ListView lv = (ListView)rootView.findViewById(R.id.list_drawer);
        final Context c = rootView.getContext();
        SharedPreferences prefs = this.getActivity().getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String mail = prefs.getString(Globals.MAIL,null);
        if(mail!=null){
            TextView tvMail = (TextView)rootView.findViewById(R.id.drawer_email);
            tvMail.setText(mail);
            Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "Roboto-Regular.ttf");
            tvMail.setTypeface(tf);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(c,"He tocado algo",Toast.LENGTH_SHORT).show();
            }
        });
        ArrayList<DrawerItem> list = new ArrayList<DrawerItem>();
        list.add(new DrawerItem("Ajustes",R.drawable.ic_settings_black_24dp));
        ListDrawerAdapter adapter = new ListDrawerAdapter(c,R.layout.drawer_row,list);
        lv.setAdapter(adapter);
        return rootView;

    }

}
