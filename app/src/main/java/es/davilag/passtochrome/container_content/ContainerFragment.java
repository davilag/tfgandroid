package es.davilag.passtochrome.container_content;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;

/**
 * Created by davilag on 1/12/14.
 */
public class ContainerFragment extends Fragment {
    private static TextView tv;
    private static View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.e(Globals.TAG, "Entra en el framgent de contenedor");
        rootView = inflater.inflate(R.layout.fragment_content_container, container, false);
        tv = (TextView) rootView.findViewById(R.id.textEmpty);
        return  rootView;
    }
}
