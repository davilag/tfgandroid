package es.davilag.passtochrome.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

    private static final String SQL_CREATE_ENTRIES_CONTAINER =
            "CREATE TABLE "+PTCDbContract.ContainerTable.TABLE_NAME+" ("+
                    PTCDbContract.ContainerTable.COLUMN_NAME_DOM+TEXT_TYPE+COMMA_SEP+
                    PTCDbContract.ContainerTable.COLUMN_NAME_USER+TEXT_TYPE+COMMA_SEP+
                    PTCDbContract.ContainerTable.COLUMN_NAME_PASS+TEXT_TYPE+COMMA_SEP+
                    "PRIMARY KEY ("+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+COMMA_SEP+
                    PTCDbContract.ContainerTable.COLUMN_NAME_USER+"))";

    private static final String SQL_DELETE_ENTRIES_REQUESTS =
            "DROP TABLE IF EXISTS "+ PTCDbContract.RequestTable.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_CONTAINER =
            "DROP TABLE IF EXISTS "+PTCDbContract.ContainerTable.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "PTCTest.db";

    public PTCDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_REQUESTS);
        db.execSQL(SQL_CREATE_ENTRIES_CONTAINER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_REQUESTS);
        db.execSQL(SQL_DELETE_ENTRIES_CONTAINER);
        onCreate(db);
    }

    public void doReset(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_ENTRIES_REQUESTS);
        db.execSQL(SQL_DELETE_ENTRIES_CONTAINER);
        onCreate(db);
    }

    /*
    *
    *
    * OPERACIONES CON LAS REQUESTS
    *
    *
    */
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
                String reqId = c.getString(0);
                String dom = c.getString(1);

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
    /*
    *
    *
    * FIN OPERACIONES CON LAS REQUESTS
    *
    *
    */
    /*
    *
    *
    * OPERACIONES CON EL CONTENEDOR
    *
    *
    */
    public void addPassContainer(SQLiteDatabase db, FilaContenedor fila) throws SQLiteConstraintException {
        Log.v(Globals.TAG,"Voy a ejecutar: INSERT INTO "+PTCDbContract.ContainerTable.TABLE_NAME+"("+
                PTCDbContract.ContainerTable.COLUMN_NAME_DOM+COMMA_SEP+
                PTCDbContract.ContainerTable.COLUMN_NAME_USER+COMMA_SEP+
                PTCDbContract.ContainerTable.COLUMN_NAME_PASS+")"+
                "VALUES("+COMILLA+fila.getDom()+COMILLA+COMMA_SEP+
                COMILLA+fila.getUsuario()+COMILLA+COMMA_SEP+
                COMILLA+fila.getPass()+COMILLA+");");
        db.execSQL("INSERT INTO "+PTCDbContract.ContainerTable.TABLE_NAME+"("+
                    PTCDbContract.ContainerTable.COLUMN_NAME_DOM+COMMA_SEP+
                    PTCDbContract.ContainerTable.COLUMN_NAME_USER+COMMA_SEP+
                    PTCDbContract.ContainerTable.COLUMN_NAME_PASS+")"+
                    "VALUES("+COMILLA+fila.getDom()+COMILLA+COMMA_SEP+
                              COMILLA+fila.getUsuario()+COMILLA+COMMA_SEP+
                              COMILLA+fila.getPass()+COMILLA+");");
    }

    public void delPassContainer(SQLiteDatabase db, String domName, String userName){
        db.execSQL("DELETE FROM "+PTCDbContract.ContainerTable.TABLE_NAME+"WHERE "+
                    PTCDbContract.ContainerTable.COLUMN_NAME_DOM+"="+COMILLA+domName+COMILLA+"AND"+
                    PTCDbContract.ContainerTable.COLUMN_NAME_USER+"="+COMILLA+userName+COMILLA+";");
    }
    public String getPassContainer(SQLiteDatabase db, String dom, String userName){
        String pass = null;
        Cursor c = db.rawQuery("SELECT "+PTCDbContract.ContainerTable.COLUMN_NAME_PASS+COMMA_SEP+
                PTCDbContract.ContainerTable.COLUMN_NAME_USER+" FROM "+PTCDbContract.ContainerTable.TABLE_NAME+" WHERE "+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+
                            " = "+COMILLA+dom+COMILLA, new String[]{});
        if(c!=null){
            if(c.getCount()>0){
                if(userName.equals("")){
                    return "";
                }else{
                    while(c.moveToNext()){
                        String user = c.getString(1);
                        pass = c.getString(0);
                        Log.v(Globals.TAG,"El usuario que he cogido es: "+user+" La contraseña es: "+pass);
                        if(user.equals(userName))
                            return pass;
                    }
                }
            }
        }
        return pass;
    }

    public void modPassContainer(SQLiteDatabase db, String dom, String userName, String newPass){
        Log.v(Globals.TAG,"Voy a ejecutar: UPDATE "+PTCDbContract.ContainerTable.TABLE_NAME+" SET "+PTCDbContract.ContainerTable.COLUMN_NAME_PASS+"="+COMILLA+newPass+COMILLA+
                "WHERE "+PTCDbContract.ContainerTable.COLUMN_NAME_USER+"="+COMILLA+userName+COMILLA+
                " AND "+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+"="+COMILLA+dom+COMILLA+");");
        db.execSQL("UPDATE "+PTCDbContract.ContainerTable.TABLE_NAME+" SET "+PTCDbContract.ContainerTable.COLUMN_NAME_PASS+"="+COMILLA+newPass+COMILLA+
                    "WHERE "+PTCDbContract.ContainerTable.COLUMN_NAME_USER+"="+COMILLA+userName+COMILLA+
                    " AND "+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+"="+COMILLA+dom+COMILLA+");");
    }

    public ArrayList<FilaContenedor> getListaContenedor(SQLiteDatabase db){
        ArrayList<FilaContenedor> filas = new ArrayList<FilaContenedor>();
        Cursor c =  db.rawQuery("SELECT "+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+COMMA_SEP+PTCDbContract.ContainerTable.COLUMN_NAME_USER+" FROM "+PTCDbContract.ContainerTable.TABLE_NAME,new String[]{});
        if(c!=null){
            while(c.moveToNext()){
                String dom = c.getString(0);
                String user = c.getString(1);
                if(dom!=null && user!=null){
                    filas.add(new FilaContenedor(user,null,dom));//Para los mostrar por pantalla los dominios guardados no tenemos que sacar las contraseñas.
                }
            }
        }
        return filas;
    }

    public String[] getUsers(SQLiteDatabase db, String dom){
        String[] users = null;
        Cursor c = db.rawQuery("SELECT "+PTCDbContract.ContainerTable.COLUMN_NAME_USER+" FROM "+PTCDbContract.ContainerTable.TABLE_NAME+" WHERE "+PTCDbContract.ContainerTable.COLUMN_NAME_DOM+"="+COMILLA+dom+COMILLA, new String[]{});
        if(c!=null){
            users = new String[c.getCount()];
            int i = 0;
            while(c.moveToNext()){
                users[i] = c.getString(0);
                i++;
            }
        }
        return users;

    }
   /*
    *
    *
    * FIN OPERACIONES CON LAS REQUESTS
    *
    *
    */


}
