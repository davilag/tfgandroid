package es.davilag.passtochrome.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class GaloisCounterMode {

    public static String GCMEncrypt(String key,String iv,String input,String aad)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
        //GENERAR SALIDA
        byte[] keyBytes = Base64.decode(key,Base64.DEFAULT);
        byte[] ivBytes = Base64.decode(iv,Base64.DEFAULT);
        byte[] aadBytes = aad.getBytes("UTF-8");
        Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        cipherEncrypt.updateAAD(aadBytes);
        byte[] encrypted = cipherEncrypt.doFinal(input.getBytes("UTF-8"));
        //FIN GENERAR SALIDA
        return Base64.encodeToString(encrypted,Base64.DEFAULT).trim();

    }

    public static String GCMDecrypt(String key,String iv,String input,String aad)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
        byte[] keyBytes = Base64.decode(key,Base64.DEFAULT);
        byte[] ivBytes = Base64.decode(iv,Base64.DEFAULT);
        byte[] aadBytes = aad.getBytes("UTF-8");
        Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        cipherDecrypt.updateAAD(aadBytes);
        byte[] decryptedBytes = Base64.decode(input,Base64.DEFAULT);
        byte[] original = cipherDecrypt.doFinal(decryptedBytes);
        return new String(original,"UTF-8");
    }

    public static long getNonce(){
        return (long) (Math.floor(Math.random()*999999999)+0);
    }

    public static String getIv(){
        String retVal = "";
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for(int i = 0 ; i< 16;i++){
            retVal += possible.charAt((int)Math.floor(Math.random()*possible.length()));
        }
        try {
            return Base64.encodeToString(retVal.getBytes("UTF-8"),Base64.DEFAULT).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String[] getKeys(String pass){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes("UTF-8"));
            byte[] resumen = md.digest();
            return new String[] {Base64.encodeToString(Arrays.copyOfRange(resumen, 0, resumen.length / 2),Base64.DEFAULT).trim(),
                    Base64.encodeToString(Arrays.copyOfRange(resumen,resumen.length/2,resumen.length ),Base64.DEFAULT).trim()};
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
