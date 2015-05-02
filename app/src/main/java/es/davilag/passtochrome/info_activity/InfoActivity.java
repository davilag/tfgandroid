package es.davilag.passtochrome.info_activity;

import android.app.Activity;
import android.app.AlertDialog;
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


public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent i = getIntent();
        TextView tv = (TextView) findViewById(R.id.id_dom_info);

        tv.setText(i.getStringExtra(Globals.INTENT_DOM));
        invalidateOptionsMenu();
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "Roboto-Thin.ttf");
        tv.setTypeface(tf);
        TextView tvUser = (TextView) findViewById(R.id.id_info_user);
        tvUser.setText(i.getStringExtra(Globals.INTENT_USER));
        tvUser.setTypeface(tf);
        Button buttonEditar = (Button) findViewById(R.id.button_editar);
        buttonEditar.setOnClickListener(new PulsoEditar(this,i.getStringExtra(Globals.INTENT_DOM),i.getStringExtra(Globals.INTENT_USER)));

        Button buttonEliminar = (Button) findViewById(R.id.button_eliminar);
        buttonEliminar.setOnClickListener(new PulsoEliminar(this,i.getStringExtra(Globals.INTENT_DOM),i.getStringExtra(Globals.INTENT_USER)));
    }

    private static class PulsoEditar implements View.OnClickListener{
        final Activity activity;
        private String dominio;
        private String usuario;
        public PulsoEditar (Activity activity, String dominio, String usuario){
            this.activity = activity;
            this.dominio = dominio;
            this.usuario = usuario;
        }
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_editar,null));

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editDom = (EditText)((AlertDialog)dialog).findViewById(R.id.pass_dom);
                    Toast.makeText(activity,editDom.getText(),Toast.LENGTH_SHORT).show();
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
        private String dominio;
        private String usuario;
        public PulsoEliminar (Activity activity, String dominio, String usuario){
            this.activity = activity;
            this.dominio = dominio;
            this.usuario = usuario;
        }
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_elimimar,null));

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
