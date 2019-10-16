package es.jcyl.ita.frmdrd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(AndroidJUnit4.class)
public class BasicDBTest {


    lateinit var dbHelper: DbHelper

    @Before
    fun setup() {
        dbHelper = DbHelper(RuntimeEnvironment.application)
        dbHelper.clearDbAndRecreate()
    }

    @Test
    @Throws(Exception::class)
    fun testDbInsertion() {

        // Given
        val testStr1 = "testing"
        val testStr2 = "testing"

        // When
        dbHelper.insertText(testStr1)
        dbHelper.insertText(testStr2)

        // Then
        assertEquals(dbHelper.getAllText(), "$testStr1-$testStr2-")
    }

    @After
    fun tearDown() {
        dbHelper.clearDb()
    }

}