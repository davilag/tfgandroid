package es.davilag.passtochrome.http;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.Message;
import es.davilag.passtochrome.R;
import es.davilag.passtochrome.security.GaloisCounterMode;
import es.davilag.passtochrome.security.Security;

/**
 * Created by davilag on 22/10/14.
 */
public class ServerMessage {
    public static class NullHostNameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }
    }
    private static String getServerKey(Context c){
        SharedPreferences prefs = c.getSharedPreferences(Globals.GCM_PREFS,Context.MODE_PRIVATE);
        return prefs.getString(Globals.SERVER_KEY,"");
    }

    private static InputStream getClientCertFile(Context c) throws Exception{
        AssetManager assetManager = c.getAssets();
        return assetManager.open(c.getResources().getString(R.string.client_cert_file_name));
    }
    private static String readCaCert(Context c) throws Exception {
        AssetManager assetManager = c.getAssets();
        InputStream inputStream = assetManager.open(c.getResources().getString(R.string.server_cert_asset_name));
        return IOUtil.readFully(inputStream);
    }
    private static SSLContext generateSSLContext(Context c) throws Exception{
        InputStream clientCertFile = getClientCertFile(c);
        String clientCertificatePasswd = c.getResources().getString(R.string.client_cert_password);
        String caCertificate = readCaCert(c);
        return SSLContextFactory.getInstance().makeContext(clientCertFile, clientCertificatePasswd, caCertificate);
    }
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
    public static boolean  sendRegisterMessage(String mail,String regId,String serverKey, Context context) throws Exception {
        SSLContext sslContext = generateSSLContext(context);
        URL obj = new URL(Globals.SERVER_DIR_SEC+"/PTC/register");
        HttpURLConnection con = null;
        ((HttpsURLConnection)con).setDefaultHostnameVerifier(new NullHostNameVerifier());
        con = (HttpURLConnection) obj.openConnection();
        if(con instanceof HttpsURLConnection) {
            ((HttpsURLConnection)con).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        con.setDoInput(true);
        ObjectMapper om = new ObjectMapper();
        Message m = new Message();
        m.addData(Globals.MSG_MAIL,mail);
        m.addData(Globals.MSG_REG_ID,regId);
        m.addData(Globals.MSG_ROLE,Globals.ACTION_CONTAINER);
        m.addData(Globals.MSG_SERVER_KEY,serverKey);
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

    public static boolean sendResponseMessage(Context c, String dominio, String pass, String ivPass,String reqId, Long nonce,String estado) throws Exception{
        SSLContext sslContext = generateSSLContext(c);
        URL obj = new URL(Globals.SERVER_DIR+"/PTC/response");
        HttpURLConnection con = null;
        ((HttpsURLConnection)con).setDefaultHostnameVerifier(new NullHostNameVerifier());
        con = (HttpURLConnection) obj.openConnection();
        if(con instanceof HttpsURLConnection) {
            ((HttpsURLConnection)con).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        Message messagePayload = new Message();
        messagePayload.addData(Globals.MSG_DOMAIN, dominio);
        messagePayload.addData(Globals.MSG_NONCE,nonce);
        messagePayload.addData(Globals.MSG_STATE,estado);
        messagePayload.addData(Globals.MSG_TS, System.currentTimeMillis());
        messagePayload.addData(Globals.MSG_PASSWD, pass);
        messagePayload.addData(Globals.MSG_IV,ivPass);
        ObjectWriter ow = new ObjectMapper().writer();
        String payloadPlain = ow.writeValueAsString(messagePayload);
        Log.v(Globals.TAG,"El payload de respuesta en plano es: "+payloadPlain);
        String serverKey = Security.getServerKey(c);
        String iv = GaloisCounterMode.getIv();
        String payloadCipher = GaloisCounterMode.GCMEncrypt(serverKey,iv,payloadPlain,reqId);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        ObjectMapper om = new ObjectMapper();
        Message m = new Message();
        m.addData(Globals.MSG_IV,iv);
        m.addData(Globals.MSG_AAD,reqId);
        m.addData(Globals.MSG_PAYLOAD,payloadCipher);
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

    public static boolean sendSavedPassResponse(Context c, String reqId, Boolean saved, String mail,Long nonce) throws Exception{
        SSLContext sslContext = generateSSLContext(c);
        URL obj = new URL(Globals.SERVER_DIR+"/PTC/savedres");
        HttpURLConnection con = null;
        ((HttpsURLConnection)con).setDefaultHostnameVerifier(new NullHostNameVerifier());
        con = (HttpURLConnection) obj.openConnection();
        if(con instanceof HttpsURLConnection) {
            ((HttpsURLConnection)con).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        Message messagePayload = new Message();
        if(saved){
            messagePayload.addData(Globals.MSG_STATE,Globals.MSG_STATE_OK);
        }else{
            messagePayload.addData(Globals.MSG_STATE,Globals.MSG_STATE_FAIL);
        }
        messagePayload.addData(Globals.MSG_NONCE,nonce);
        messagePayload.addData(Globals.MSG_TS, System.currentTimeMillis());
        ObjectWriter ow = new ObjectMapper().writer();
        String payloadPlain = ow.writeValueAsString(messagePayload);
        Log.v(Globals.TAG, "El payload de respuesta en plano es: " + payloadPlain);
        String serverKey = Security.getServerKey(c);
        String iv = GaloisCounterMode.getIv();
        String payloadCipher = GaloisCounterMode.GCMEncrypt(serverKey,iv,payloadPlain,reqId);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        ObjectMapper om = new ObjectMapper();
        Message m = new Message();
        m.addData(Globals.MSG_IV,iv);
        m.addData(Globals.MSG_AAD,reqId);
        m.addData(Globals.MSG_PAYLOAD,payloadCipher);
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

    public static boolean sendLogoutMessage(Context c, String serverKey,String regId,String mail) throws Exception {
        SSLContext sslContext = generateSSLContext(c);
        URL obj = new URL(Globals.SERVER_DIR+"/PTC/logout");
        HttpURLConnection con = null;
        ((HttpsURLConnection)con).setDefaultHostnameVerifier(new NullHostNameVerifier());
        con = (HttpURLConnection) obj.openConnection();
        if(con instanceof HttpsURLConnection) {
            ((HttpsURLConnection)con).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        Log.v(Globals.TAG,"Voy a enviar el mensaje de logout");
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        con.setDoInput(true);
        ObjectMapper om = new ObjectMapper();
        Message payload = new Message();
        payload.addData(Globals.MSG_REG_ID,regId);
        payload.addData(Globals.MSG_TS,System.currentTimeMillis());
        String iv = GaloisCounterMode.getIv();
        ObjectWriter ow = om.writer();
        String payloadPlain = ow.writeValueAsString(payload);
        String aad = mail;
        String payloadCipher = GaloisCounterMode.GCMEncrypt(serverKey,iv,payloadPlain,aad);
        Message m = new Message();
        m.addData(Globals.MSG_IV,iv);
        m.addData(Globals.MSG_AAD,aad);
        m.addData(Globals.MSG_PAYLOAD,payloadCipher);
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        om.writeValue(dos,m);
        dos.flush();
        dos.close();
        int responseCode = con.getResponseCode();
        if(responseCode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Boolean registrado = Boolean.parseBoolean(response.toString());
            return registrado;
        }
        return false;
    }
}
