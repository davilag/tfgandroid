package es.davilag.passtochrome;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by davilag on 10/12/14.
 */
public class Security {
    private String passPhrase;
    private String serverKey;
    private String userKey;
    private Context c;

    public Security(Context c, String passPhrase) {
        this.passPhrase = passPhrase;
        this.c = c;
        String[] generatedKeys = generateKeys();
        serverKey = generatedKeys[0];
        userKey = generatedKeys[1];
    }

    public String getServerKey() {
        return serverKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void almacenaServerKey(){
        //TO-DO: Almacenar la verdadera serverKey en un fichero privado.
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.SERVER_KEY,this.serverKey);
        editor.commit();
    }
    private String[] generateKeys(){
        String[] generated = new String[2];
        generated[0] = passPhrase;
        generated[1] = passPhrase;
        return generated;
    }
}
