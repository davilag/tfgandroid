package es.davilag.passtochrome.database;

import android.provider.BaseColumns;

/**
 * Created by davilag on 29/11/14.
 */
public final class PTCDbContract {
    public PTCDbContract(){}

    public static abstract class RequestTable implements BaseColumns{
        public static final String TABLE_NAME = "TB_REQUESTS";
        public static final String COLUMN_NAME_ID = "REQ_ID";
        public static final String COLUMN_NAME_DOM = "DOM_NAME";
    }

    public static abstract class ContainerTable implements BaseColumns{
        public static final String TABLE_NAME = "TB_CONTAINER";
    }
}
