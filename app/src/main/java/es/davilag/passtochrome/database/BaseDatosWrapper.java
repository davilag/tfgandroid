package es.davilag.passtochrome.database;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import es.davilag.passtochrome.security.GaloisCounterMode;
import es.davilag.passtochrome.security.Security;

/**
 * Created by davilag on 29/11/14.
 */
public class BaseDatosWrapper {
    public BaseDatosWrapper(){
    }

    public static void resetBaseDatos(Context c){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.doReset(db);
        db.close();
    }

    public static void insertRequest(Context c, Request r){
        if(r.getDom()!=null&&r.getReqId()!=null){
            PTCDbHelper helper = new PTCDbHelper(c);
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.addRequest(db,r);
            db.close();
        }
    }

    public static ArrayList<Request> getRequests(Context c){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Request> requests = helper.getRequests(db);
        db.close();
        return requests;
    }

    public static Request getAndRemoveRequestDomain(Context c, String reqId){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        Request req = helper.getRequestDom(db, reqId);
        helper.removeRequest(db,reqId);
        db.close();
        return req;
    }
    public static void removeRequest(Context c, String reqId){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        helper.removeRequest(db,reqId);
        db.close();
    }

    public static boolean insertPass(Context c, FilaContenedor fila){
        if(fila.getDom()!=null&&fila.getPass()!=null&&fila.getUsuario()!=null){
            PTCDbHelper helper = new PTCDbHelper(c);
            SQLiteDatabase db = helper.getWritableDatabase();
            try{
                helper.addPassContainer(db,fila);
                return true;
            }catch(SQLiteConstraintException e){
                return false;
            }finally {
                db.close();
            }
        }
        return false;
    }

    public static void delPass(Context c, String dom, String user){
        if(dom!=null&&user!=null){
            PTCDbHelper helper = new PTCDbHelper(c);
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.delPassContainer(db,dom,user);
            db.close();
        }
    }

    public static String[] getPass(Context c, String dom, String user){
        PTCDbHelper helper= new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] pass = helper.getPassContainer(db,dom,user);
        db.close();
        return  pass;
    }

    public static boolean changePass(Context c, String newDom, String newUser, String newPass,
                                     String oldDom, String oldUser, String passContainer){
        Security sec = new Security(c,passContainer);
        if(sec.correctPassPhrase()){
            PTCDbHelper helper = new PTCDbHelper(c);
            String iv = GaloisCounterMode.getIv();
            String newPassCipher = sec.cipherUserPass(newUser,newPass,iv);
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.modPassContainer(db,newDom,newUser,newPassCipher,oldDom,oldUser,iv);
            db.close();
            db.close();
            return true;
        }
        return false;
    }

    public static ArrayList<FilaContenedor> getContenedor(Context c){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<FilaContenedor> filas = helper.getListaContenedor(db);
        db.close();
        return filas;
    }

    public static String[] getUsers(Context c, String dom){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] users = helper.getUsers(db,dom);
        db.close();
        return users;
    }

    public void deleteDataBase(Context c){
        PTCDbHelper helper = new PTCDbHelper(c);
        helper.deleteDataBase(c);
    }
}
