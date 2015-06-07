package es.davilag.passtochrome.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.davilag.passtochrome.Globals;

/**
 * Created by davilag on 10/12/14.
 */
public class Security {
    private String passPhrase;
    private String serverKey;
    private String passKey;
    private Context c;

    public Security(Context c, String passPhrase) {
        this.passPhrase = passPhrase;
        this.c = c;
        String[] generatedKeys = generateKeys();
        serverKey = generatedKeys[0];
        passKey = generatedKeys[1];
    }

    public String getServerKey() {
        return serverKey;
    }

    public void almacenaServerKey(){
        //TO-DO: Almacenar la verdadera serverKey en un fichero privado.
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.SERVER_KEY,this.serverKey);
        editor.commit();
    }
    private String[] generateKeys(){
        String[] generated = GaloisCounterMode.getKeys(this.passPhrase);
        return generated;
    }
    public static String getServerKey(Context c){
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Globals.SERVER_KEY,null);
    }

    public String cipherUserPass(String user, String pass, String iv){
        UserPass userPass= new UserPass(user, pass);
        ObjectWriter om = new ObjectMapper().writer();
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        String mail = prefs.getString(Globals.MAIL, "");
        try {
            String userPassPlain = om.writeValueAsString(userPass);
            String userPassCipher = GaloisCounterMode.GCMEncrypt(this.passKey,iv,userPassPlain,mail);
            return userPassCipher;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean correctPassPhrase(){
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        String serverKeyAlm = prefs.getString(Globals.SERVER_KEY,null);
        return serverKeyAlm.equals(serverKey);
    }
}
