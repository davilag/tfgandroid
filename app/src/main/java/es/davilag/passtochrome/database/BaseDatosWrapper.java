package es.davilag.passtochrome.database;

import android.content.Context;
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
        PTCDbHelper helper = new PTCDbHelper(c);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.addRequest(db,r);
        db.close();
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
}
