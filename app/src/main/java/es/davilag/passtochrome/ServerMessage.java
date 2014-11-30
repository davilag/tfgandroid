package es.davilag.passtochrome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by davilag on 22/10/14.
 */
public class ServerMessage {

    private static void setRegistered(String email,Context context){
        SharedPreferences prefs = context.getSharedPreferences(Globals.GCM_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Globals.REGISTERED,true);
        Log.e(Globals.TAG,"Guardo el email en SharedPreferences que es "+email);
        editor.putString(Globals.MAIL,email);
        editor.commit();
        Intent i = new Intent(Globals.REFRESH_CONTENT);
        context.sendBroadcast(i);
    }
    public static boolean  sendRegisterMessage(String mail,String regId, Context context) throws Exception {
        URL obj = new URL(Globals.SERVER_DIR+"/PTC/register");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        con.setDoInput(true);
        ObjectMapper om = new ObjectMapper();
        Message m = new Message();
        m.addData(Globals.MSG_ACTION,Globals.ACTION_REGISTER);
        m.addData(Globals.MSG_MAIL,mail);
        m.addData(Globals.MSG_REG_ID,regId);
        m.addData(Globals.MSG_ROLE,Globals.ACTION_CONTAINER);
        m.addData(Globals.MSG_SERVER_KEY,"1234");
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        om.writeValue(dos,m);
        dos.flush();
        dos.close();
        int responseCode = con.getResponseCode();
        Log.v(Globals.TAG,"El codigo de respuesta de el mensaje de registro es: "+responseCode);
        if(responseCode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Boolean registrado = Boolean.parseBoolean(response.toString());
            setRegistered(mail,context);
            return registrado;
        }
        return false;
    }

    public static boolean sendResponseMessage(String mail, String dominio, String pass, String regId,String reqId) throws Exception{
        URL obj = new URL(Globals.SERVER_DIR+"/PTC/response");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        ObjectMapper om = new ObjectMapper();
        Message m = new Message();
        m.addData(Globals.MSG_ACTION,Globals.ACTION_REQUEST);
        m.addData(Globals.MSG_MAIL,mail);
        m.addData(Globals.MSG_DOMAIN,dominio);
        m.addData(Globals.MSG_PASSWD,pass);
        m.addData(Globals.MSG_REG_ID,regId);
        m.addData(Globals.MSG_REQ_ID,reqId);
        m.addData(Globals.MSG_USER,dominio+"user");
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        om.writeValue(dos,m);
        dos.flush();
        dos.close();
        int responseCode = con.getResponseCode();
        Log.v(Globals.TAG,"El codigo de respuesta de el mensaje de registro es: "+responseCode);
        if(responseCode == 200){
            /*
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Boolean registrado = Boolean.parseBoolean(response.toString());
            setRegistered(mail,context);
            */
            Log.v(Globals.TAG,"Mensaje de respuesta enviado con éxtio.");
            return true;
        }
        Log.e(Globals.TAG,"Mensaje de respuesta fallido");
        return false;
    }
}
