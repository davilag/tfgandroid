package es.davilag.passtochrome.drawer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import es.davilag.passtochrome.LoginActivity;
import es.davilag.passtochrome.R;
import es.davilag.passtochrome.ToolbarActivity;
import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.http.ServerMessage;

/**
 * Created by davilag on 29/10/14.
 */
public class DrawerFragment extends Fragment {
    private static ListView lv;

    private void logOut(final Context c){
        new AsyncTask<Context,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Context... params) {
                try {
                    return ServerMessage.sendLogoutMessage(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            protected void onPostExecute(Boolean logout)
            {
                if(logout) {
                    SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.commit();
                    BaseDatosWrapper bdw = new BaseDatosWrapper();
                    bdw.deleteDataBase(c);
                    Intent i = new Intent(c, LoginActivity.class);
                    c.startActivity(i);
                }else{
                    Toast.makeText(c,"Problema al hacer logout",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(c);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        lv = (ListView)rootView.findViewById(R.id.list_drawer);
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
                String titulo;
                ToolbarActivity.setContent("");
                if(view==null){
                    titulo = getResources().getString((int)id);
                    ToolbarActivity.closeAndSetTitle(titulo);
                }else{
                    TextView tv = (TextView)view.findViewById(R.id.drawer_itemName);
                    titulo = tv.getText().toString();
                    ToolbarActivity.closeAndSetTitle(titulo);
                }
                if(titulo.equals(getResources().getString(R.string.logout))){
                    Log.v(Globals.TAG,"He pulsado que quiero salir de la sesion");
                    logOut(c);
                }
                for(int i = 0; i<lv.getChildCount();i++){
                    Log.e(Globals.TAG,"Se mete en el for");
                    if(i!=position){
                        lv.getChildAt(i).setActivated(false);
                    }
                }
            }
        });
        ArrayList<DrawerItem> list = new ArrayList<DrawerItem>();
        list.add(new DrawerItem(getResources().getString(R.string.container_title),R.drawable.ic_sd_card_grey600_24dp));
        list.add(new DrawerItem(getResources().getString(R.string.requests_title),R.drawable.ic_inbox_grey600_24dp));
        list.add(new DrawerItem("Ajustes",R.drawable.ic_settings_black_24dp));
        list.add(new DrawerItem(getResources().getString(R.string.logout),R.drawable.ic_settings_black_24dp));
        ListDrawerAdapter adapter = new ListDrawerAdapter(c,R.layout.drawer_row,list);
        lv.setAdapter(adapter);

        Log.v(Globals.TAG,"Ha llegado al final de drawe y el lv es: "+lv);
        return rootView;
    }

    public static void performDrawerClick(int index, int title){

        Log.v(Globals.TAG,"Lv en perform es:"+lv);
        Log.e(Globals.TAG,"LV tiene :"+lv.getChildCount()+" hijos");

        /*
        for(int i = 0; i<lv.getChildCount();i++){
            lv.getChildAt(i).setActivated(false);
        }
        filaSeleccionada.setActivated(true);
        TextView tv = (TextView) filaSeleccionada.findViewById(R.id.drawer_itemName);
        ToolbarActivity.closeAndSetTitle(tv.getText());
        */
        lv.performItemClick(null,index,title);
    }
}
