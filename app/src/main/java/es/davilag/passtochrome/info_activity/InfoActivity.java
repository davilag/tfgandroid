package es.davilag.passtochrome.info_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.R;
import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.security.Security;


public class InfoActivity extends Activity {

    private static String dom;
    private static String user;
    private static TextView tvDom;
    private static TextView tvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent i = getIntent();
        tvDom = (TextView) findViewById(R.id.id_dom_info);
        dom = i.getStringExtra(Globals.INTENT_DOM);
        user = i.getStringExtra(Globals.INTENT_USER);
        tvDom.setText(i.getStringExtra(Globals.INTENT_DOM));
        invalidateOptionsMenu();
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        tvDom.setTypeface(tf);
        tvUser = (TextView) findViewById(R.id.id_info_user);
        tvUser.setText(i.getStringExtra(Globals.INTENT_USER));
        tvUser.setTypeface(tf);
        Button buttonEditar = (Button) findViewById(R.id.button_editar);
        buttonEditar.setOnClickListener(new PulsoEditar(this));

        Button buttonEliminar = (Button) findViewById(R.id.button_eliminar);
        buttonEliminar.setOnClickListener(new PulsoEliminar(this));
    }
    private static boolean modPass(Context c,String newDom, String newUser, String newPassDom, String oldDom,
                                String oldUser, String passContainer){
        if(BaseDatosWrapper.changePass(c,newDom,newUser,newPassDom,oldDom,oldUser,passContainer)){
            dom = newDom;
            user = newUser;
            tvDom.setText(newDom);
            tvUser.setText(newUser);
            return true;
        }else{
            return false;
        }
    }

    private static boolean deletePass(Context c, String passCont){
        Security sec = new Security(c,passCont);
        if(sec.correctPassPhrase()){
            BaseDatosWrapper.delPass(c,dom,user);
            return true;
        }
        return false;
    }

    private static class PulsoEditar implements View.OnClickListener{
        final InfoActivity activity;
        public PulsoEditar (InfoActivity activity){
            this.activity = activity;
        }
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_editar, null);
            builder.setView(dialogView);
            ((EditText)dialogView.findViewById(R.id.user_mod)).setText(activity.user);
            ((EditText)dialogView.findViewById(R.id.dom_mod)).setText(activity.dom);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editUser = (EditText)((AlertDialog)dialog).findViewById(R.id.user_mod);
                    EditText editDom = (EditText)((AlertDialog)dialog).findViewById(R.id.dom_mod);
                    EditText editPassDom = (EditText)((AlertDialog)dialog).findViewById(R.id.pass_dom);
                    EditText editPassCont = (EditText)((AlertDialog)dialog).findViewById(R.id.pass_cont);
                    if(!modPass(activity.getApplicationContext(),editDom.getText().toString(),
                            editUser.getText().toString(),editPassDom.getText().toString(),activity.dom,
                            activity.user,editPassCont.getText().toString())){
                        Toast.makeText(activity.getApplicationContext(),"No se ha podido cambiar la contraseña",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setTitle("Editar contraseña");
            builder.create().show();
        }
    }

    private static class PulsoEliminar implements View.OnClickListener{
        final Activity activity;
        public PulsoEliminar (Activity activity){
            this.activity = activity;
        }
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_elimimar,null));

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText eTPassContainer = (EditText)((AlertDialog)dialog).findViewById(R.id.pass_cont);
                    if(deletePass(activity.getApplicationContext(),eTPassContainer.getText().toString())){
                        activity.finish();
                    }else{
                        Toast.makeText(activity.getApplicationContext(),"No se ha podido borrar la contraseña",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setTitle("Eliminar contraseña");
            builder.create().show();
        }
    }
}
