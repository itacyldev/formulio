package es.jcyl.ita.frmdrd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BasicDBTest {

    @Test
    public void myfirstTest(){

//        TestDatabase db = new TestDatabase(ApplicationProvider.getApplicationContext());
//        System.out.println(db.getDatabaseName());

        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase("src/test/resourceS/ribera.sqlite", null);
        System.out.println(sqDb.getPath());

        Cursor c= sqDb.rawQuery("select count(1) as numero from subparcelas", null);
        int numparcelas= -1;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                numparcelas= c.getInt(0);
            } while(c.moveToNext());
        }
        System.out.println(String.format("Número de parcelas: %s", numparcelas));
    }

    /**
     * Make sure there will be no problema access
     */
    public void testParallelApiDBAccess(){

    }

}