package es.jcyl.ita.jsqlite;

import android.util.Log;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    protected static final Log Log = new Log();

    @Test
    public void testSQLiteConnection() {
    }

    public boolean isConnected(String dbFilePath) throws DAOException {
        boolean output = false;

        if (layer == null) {
            return output;
        }

        jsqlite.Database db = null;
        jsqlite.Stmt stmt = null;

        try {



            final ConnectionPoolManagerImpl connectionPoolManager = ConnectionPoolManagerImpl
                    .getInstance();
            db = connectionPoolManager.getSpatialiteConnection(databasename);

            final String query = "select 1;";
            stmt = db.prepare(query);

            if (stmt.step()) {
                output = true;
            }
        } catch (final Exception e) {
            Log.warn("No se han podido leer los datos de la capa " + layer.getId());
        } finally {
            SQLiteUtils.closeStmt(stmt);
        }

        return output;
    }
}