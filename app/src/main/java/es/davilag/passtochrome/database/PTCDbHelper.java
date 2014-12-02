package es.davilag.passtochrome.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import es.davilag.passtochrome.Globals;

/**
 * Created by davilag on 29/11/14.
 */
public class PTCDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String COMILLA = "\"";
    private static final String SQL_CREATE_ENTRIES_REQUESTS =
            "CREATE TABLE "+ PTCDbContract.RequestTable.TABLE_NAME+" (" +
                    PTCDbContract.RequestTable.COLUMN_NAME_ID+TEXT_TYPE+" PRIMARY KEY, "+
                    PTCDbContract.RequestTable.COLUMN_NAME_DOM+TEXT_TYPE+
                    ")";
    private static final String SQL_DELETE_ENTRIES_REQUESTS =
            "DROP TABLE IF EXISTS "+ PTCDbContract.RequestTable.TABLE_NAME;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PTCTest.db";

    public PTCDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_REQUESTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_REQUESTS);
        onCreate(db);
    }

    public void doReset(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_ENTRIES_REQUESTS);
        onCreate(db);
    }

    public void addRequest(SQLiteDatabase db, Request r){
        Log.v(Globals.TAG,"He ejecutado en la bd: INSERT INTO "+ PTCDbContract.RequestTable.TABLE_NAME+"("+
                PTCDbContract.RequestTable.COLUMN_NAME_ID+COMMA_SEP+
                PTCDbContract.RequestTable.COLUMN_NAME_DOM+")"+
                "VALUES("+COMILLA+r.getReqId()+COMILLA+COMMA_SEP+COMILLA+r.getDom()+COMILLA+");");
        db.execSQL("INSERT INTO "+ PTCDbContract.RequestTable.TABLE_NAME+"("+
                        PTCDbContract.RequestTable.COLUMN_NAME_ID+COMMA_SEP+
                        PTCDbContract.RequestTable.COLUMN_NAME_DOM+")"+
                        "VALUES("+COMILLA+r.getReqId()+COMILLA+COMMA_SEP+COMILLA+r.getDom()+COMILLA+");");
    }

    public ArrayList<Request> getRequests(SQLiteDatabase db){
        ArrayList<Request> requests = new ArrayList<Request>();
        Cursor c = db.rawQuery("SELECT * FROM "+ PTCDbContract.RequestTable.TABLE_NAME,new String[] {});
        Log.v(Globals.TAG,"He ejecutado la siguiente rawQuery: SELECT * FROM "+ PTCDbContract.RequestTable.TABLE_NAME);
        if(c!=null){
            while(c.moveToNext()){
                String reqId = null;
                String dom = null;
                for (int i = 0; i<c.getColumnCount();i++){
                    switch (i){
                        case 0:
                            //ReqID
                            reqId = c.getString(i);
                            break;
                        case 1:
                            //Dom
                            dom = c.getString(i);
                            break;
                        default:
                            break;
                    }

                }
                if(reqId!=null&&dom!=null){
                    requests.add(new Request(reqId,dom));
                }
            }
        }
        return requests;
    }

    public String getRequestDom(SQLiteDatabase db, String reqId){
        String dom = null;
        Log.v(Globals.TAG,"Voy a ejecutar: SELECT "+ PTCDbContract.RequestTable.COLUMN_NAME_DOM +
                                                 " FROM "+PTCDbContract.RequestTable.TABLE_NAME +
                                                 " WHERE "+ PTCDbContract.RequestTable.COLUMN_NAME_ID+"="+COMILLA+reqId+COMILLA);
        Cursor c = db.rawQuery("SELECT "+ PTCDbContract.RequestTable.COLUMN_NAME_DOM+
                                " FROM "+PTCDbContract.RequestTable.TABLE_NAME+
                                " WHERE "+ PTCDbContract.RequestTable.COLUMN_NAME_ID+"="+COMILLA+reqId+COMILLA,new String[]{});
        if(c!=null){
            if(c.moveToNext()){
                dom = c.getString(0);
            }
        }
        Log.v(Globals.TAG,"El dominio es: "+dom);
        return dom;
    }

    public void removeRequest(SQLiteDatabase db, String reqId){
        Log.v(Globals.TAG,"Voy a ejecutar:DELETE FROM "+ PTCDbContract.RequestTable.TABLE_NAME+" WHERE "+ PTCDbContract.RequestTable.COLUMN_NAME_ID+"="+COMILLA+reqId+COMILLA+";");
        db.execSQL("DELETE FROM "+ PTCDbContract.RequestTable.TABLE_NAME+" WHERE "+ PTCDbContract.RequestTable.COLUMN_NAME_ID+"="+COMILLA+reqId+COMILLA+";");
    }
}
