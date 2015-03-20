package es.davilag.passtochrome.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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

    private  byte[] xor(byte[] b1, byte[] b2){
        if(b1.length>b2.length){
            b2 = concatenar(b2, new byte[b1.length-b2.length]);
        }else if(b1.length<b2.length){
            b1 = concatenar(b1, new byte[b2.length-b1.length]);
        }
        byte[] b3 = new byte[b1.length];
        int i = 0;
        for(byte b: b1){
            b3[i] = (byte) (b ^ b2[i++]);
        }
        return b3;
    }
    private  byte[] intToList(int number, int listSize){
        ByteBuffer b = ByteBuffer.allocate(listSize);
        b.putInt(number);
        return b.array();
    }

    private  int listToInt(byte[] in){
        ByteBuffer wrapped = ByteBuffer.wrap(in);
        int out = wrapped.getInt();
        return out;
    }
    private  byte[] multGF2(byte[]bytex, byte[]bytey){
        int x = listToInt(bytex);
        int y = listToInt(bytey);

        int z = 0;
        while((y & ((1<<128)-1))!=0){
            if((y & (1<<127))!=0){
                z ^= x;
            }
            y <<= 1;
            if((x & 1)!=0){
                x = (x>>1)^(0xe1<<120);
            }else{
                x >>=1;
            }
        }
        return intToList(z, 16);
    }
    private  byte[] xorMultH(byte[] p, byte[] q, byte[] hkey){
        return multGF2(hkey, xor(p,q));
    }

    private  byte[] concatenar(byte[] b1, byte[] b2){
        byte[] byteReturn = new byte[b1.length+b2.length];
        for(int i = 0; i<b1.length; i++){
            byteReturn[i] = b1[i];
        }
        for(int i = b1.length; i<byteReturn.length;i++){
            byteReturn[i] = b2[i-b1.length];
        }
        return byteReturn;
    }
    private  byte[] subArray(byte[] arrayIn,int indexInit, int indexFin){
        if (indexFin >= arrayIn.length){
            indexFin = arrayIn.length-1;
        }
        byte[] arrayOut = new byte[indexFin-indexInit+1];
        for(int i = indexInit; i<indexFin+1;i++){
            arrayOut[i-indexInit] = arrayIn[i];
        }
        return arrayOut;
    }
    private  byte[] GHASH(byte[] hkey,byte[] aad, byte[] ctext){
        byte[] x = new byte[16];
        byte[] auxAad = new byte[(16-aad.length%16)%16];
        byte[] aadP = concatenar(aad, auxAad);
        byte[] ctexAux = new byte[(16-ctext.length%16)%16];
        byte[] ctextP = concatenar(ctext, ctexAux);
        for(int i = 0; i<aadP.length;i+=16){
            byte[] aux = subArray(aadP, i, i+16);
            x = xorMultH(x, aux, hkey);
        }
        for(int i = 0; i<ctexAux.length;i+=16){
            byte[] aux = subArray(ctextP, i, i+16);
            x = xorMultH(x, aux, hkey);
        }
        return xorMultH(x, concatenar(aad, ctext), hkey);
    }

    public  String[] GCMEncrypt(String key,String iv,String input,String aad) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        //GENERAR SALIDA
        byte[] keyBytes = Base64.decode(key,Base64.DEFAULT);
        byte[] ivBytes = Base64.decode(iv,Base64.DEFAULT);
        byte[] aadBytes = Base64.decode(aad,Base64.DEFAULT);
        Cipher cipher1 = Cipher.getInstance("AES/GCM/NoPadding");
        cipher1.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
        byte[]h = cipher1.doFinal(new byte[16]);
        byte[] y0=null;
        if(ivBytes.length==12){
            byte[] aux = new byte[4];
            aux[0] = 0x01;
            y0= concatenar(ivBytes, aux);
        }else{
            y0=GHASH(h, new byte[0], ivBytes);
        }

        byte[] g = cipher1.doFinal(y0);
        Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        byte[] encrypted = cipherEncrypt.doFinal(input.getBytes("UTF-8"));
        //FIN GENERAR SALIDA
        byte[] tag = xor(GHASH(h, aadBytes, encrypted), g);
        return new String[]{Base64.encodeToString(encrypted,Base64.DEFAULT),Base64.encodeToString(tag,Base64.DEFAULT)};

    }

    public  String[] GCMDecrypt(String key,String iv,String input,String aad) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        Cipher cipher1 = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        byte[] ivBytes = Base64.decode(iv, Base64.DEFAULT);
        byte[] aadBytes = Base64.decode(aad, Base64.DEFAULT);
        cipher1.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
        byte[]h = cipher1.doFinal(new byte[16]);
        byte[] y0=null;
        if(ivBytes.length==12){
            byte[] aux = new byte[4];
            aux[3] = 0x01;
            y0= concatenar(ivBytes, aux);
        }else{
            y0=GHASH(h, new byte[0], ivBytes);
        }

        byte[] g = cipher1.doFinal(y0);
        Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        byte[] decryptedBytes = Base64.decode(input, Base64.DEFAULT);
        byte[] original = cipherDecrypt.doFinal(decryptedBytes);
        byte[] tag = xor(GHASH(h, aadBytes, decryptedBytes),g);
        return new String[] {new String(original,"UTF-8"),Base64.encodeToString(tag, Base64.DEFAULT)};
    }
}
