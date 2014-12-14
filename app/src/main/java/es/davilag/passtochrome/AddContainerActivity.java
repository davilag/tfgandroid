package es.davilag.passtochrome;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import es.davilag.passtochrome.database.BaseDatosWrapper;
import es.davilag.passtochrome.database.FilaContenedor;


public class AddContainerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_container);
    }

    public void finalizar(View v){
        finish();
    }

    public void add(View v){
        EditText textDom = (EditText) findViewById(R.id.textDom);
        EditText textUser = (EditText) findViewById(R.id.textUser);
        EditText textPass = (EditText) findViewById(R.id.textPass);
        final View vButton = v;

        String dom = textDom.getText().toString();
        String user = textUser.getText().toString();
        String pass = textPass.getText().toString();
        vButton.setEnabled(false);
        new AsyncTask<String,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(String... params) {
                return BaseDatosWrapper.insertPass(getApplicationContext(),new FilaContenedor(params[0],params[1],params[2]));
            }
            @Override
            protected void onPostExecute(Boolean exito)

            {
                if(!exito){
                    Toast.makeText(getApplicationContext(),"Usuario y dominio repetidos",Toast.LENGTH_SHORT).show();
                    vButton.setEnabled(true);
                }else{
                    Toast.makeText(getApplicationContext(),"Dominio guardado con exito",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }.execute(user,pass,dom);
    }
}
