package es.davilag.passtochrome.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class GaloisCounterMode {

    public  String GCMEncrypt(String key,String iv,String input,String aad)
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
        return Base64.encodeToString(encrypted,Base64.DEFAULT);

    }

    public  String GCMDecrypt(String key,String iv,String input,String aad)
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
}
