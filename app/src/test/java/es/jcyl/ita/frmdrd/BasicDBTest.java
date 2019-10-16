package es.jcyl.ita.frmdrd;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BasicDBTest {

    @Test
    public void myfirstTest(){

        TestDatabase db = new TestDatabase(ApplicationProvider.getApplicationContext());
        System.out.println(db.getDatabaseName());

        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase("mynewdb.db", null);
        System.out.println(sqDb.getPath());
    }

}