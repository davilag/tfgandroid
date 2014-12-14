package es.davilag.passtochrome.container_content;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.davilag.passtochrome.AddContainerActivity;
import es.davilag.passtochrome.ContentFragment;
import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;
import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.FilaContenedor;

/**
 * Created by davilag on 1/12/14.
 */
public class ContainerFragment extends ContentFragment {
    private static TextView tv;
    private static View rootView;
    private static RecyclerView rv;
    private static RecyclerContainerAdapter RVadapter;
    private static Activity activity;
    private static ArrayList<FilaContenedor> contenido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.e(Globals.TAG, "Entra en el framgent de contenedor");
        activity = this.getActivity();
        rootView = inflater.inflate(R.layout.fragment_content_container, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.recycler_view_container);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        new AsyncTask<Void,Void,ArrayList<FilaContenedor>>(){

            @Override
            protected ArrayList<FilaContenedor> doInBackground(Void... params) {
                contenido = BaseDatosWrapper.getContenedor(rootView.getContext());
                RVadapter = new RecyclerContainerAdapter(contenido);
                rv.setAdapter(RVadapter);
                tv = (TextView) rootView.findViewById(R.id.textEmpty);
                return  contenido;
            }
            @Override
            protected void onPostExecute(ArrayList<FilaContenedor> contenido)
            {
                if(contenido.size()>0){
                    tv.setText("");
                }else{
                    tv.setText(getResources().getString(R.string.cont_empty));
                    tv.bringToFront();
                    Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
                    tv.setTypeface(tf);
                }
            }
        }.execute();
        setHasOptionsMenu(true);
        return  rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.container,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.addContainer:
                Intent i = new Intent(getActivity(), AddContainerActivity.class);
                startActivity(i);
                return true;
            default:
                return false;
        }
    }
    public static void update(){
        if(rv!=null){
            rv.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
            rv.setLayoutManager(mLayoutManager);
            ArrayList<FilaContenedor> contenido = BaseDatosWrapper.getContenedor(rootView.getContext());
            Log.e(Globals.TAG,"Voy a coger las requests de la base de datos");
            RVadapter = new RecyclerContainerAdapter(contenido);
            rv.setAdapter(RVadapter);
            tv = (TextView) rootView.findViewById(R.id.textEmpty);
            if(contenido.size()>0){
                tv.setText("");
            }else{
                tv.setText(activity.getResources().getString(R.string.cont_empty));
                tv.bringToFront();
                Typeface tf = Typeface.createFromAsset(rootView.getContext().getAssets(), "Roboto-Thin.ttf");
                tv.setTypeface(tf);

            }
        }
    }
}
