package es.jcyl.ita.frmdrd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.VisibleForTesting;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TestDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mytest.db";
    private static final int DATABASE_VERSION = 1;
    private String createSQL = "create  table tmp_table (name TEXT )";

    public TestDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public TestDatabase(Context context, String createSQL) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.createSQL = createSQL;
    }

    @VisibleForTesting
    TestDatabase(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override public void onCreate(SQLiteDatabase db){
        db.rawQuery(this.createSQL, null);
    }
    @Override public void onUpgrade(SQLiteDatabase db, int a, int b){}
}