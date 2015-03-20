package es.davilag.passtochrome.database;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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

    public static String getAndRemoveRequestDomain(Context c, String reqId){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        String dom = helper.getRequestDom(db, reqId);
        helper.removeRequest(db,reqId);
        db.close();
        return dom;
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

    public static String getPass(Context c, String dom, String user){
        PTCDbHelper helper= new PTCDbHelper(c);
        SQLiteDatabase db = helper.getReadableDatabase();
        String pass = helper.getPassContainer(db,dom,user);
        db.close();
        return  pass;
    }

    public static boolean changePass(Context c, String dom, String user, String oldPass, String newPass){
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getWritableDatabase();
        String oldPassGuardada = helper.getPassContainer(db,dom,user);
        if(oldPassGuardada.equals(oldPass)){
            helper.modPassContainer(db,dom,user,newPass);
            db.close();
            return true;
        }
        db.close();
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
