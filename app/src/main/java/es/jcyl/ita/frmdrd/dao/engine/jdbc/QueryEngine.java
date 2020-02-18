package es.jcyl.ita.frmdrd.dao.engine.jdbc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.dao.collection.PaginatedList;
import es.jcyl.ita.frmdrd.dao.sources.DBSourceDescriptor;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 * @since 15/10/2019.
 */

public class QueryEngine {


    public PaginatedList<Map<String, Object>> retrieve(DBSourceDescriptor source, String query, Context context) {
        PaginatedList<Map<String, Object>> lst = new PaginatedList<Map<String, Object>>();

        SQLiteDatabase db = SQLiteDatabase.openDatabase(source.getPath(), null, 0);


        // TODO: Android api bug, binding parameters have to be forced to String. Create new implementation
        //  See:
        // https://stackoverflow.com/questions/41092903/use-blob-field-as-query-parameter-in-sqlite/47908283#47908283
        // SQLiteProgram
        String[] parameters = getParamsFromContext(context);

        Cursor cursor = db.rawQuery(query, parameters);

        Map row = null;
        while (cursor.moveToNext()) {
            row = cursorToMap(cursor);
            lst.add(row);
        }

        return lst;
    }

    public Map cursorToMap(Cursor cursor) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            if (cursor.getType(i) == Cursor.FIELD_TYPE_BLOB) {
                map.put(columns[i], cursor.getBlob(i));
            } else if (cursor.getType(i) == Cursor.FIELD_TYPE_NULL) {
//                map.put(columns[i], null);
            } else if (cursor.getType(i) == Cursor.FIELD_TYPE_FLOAT) {
                map.put(columns[i], cursor.getFloat(i));
            } else if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                map.put(columns[i], cursor.getInt(i));
            } else {
                map.put(columns[i], cursor.getString(i));
            }
        }
        return map;
    }


    private String[] getParamsFromContext(Context context) {
        // TODO: parse named query and obtain the expected values from the context
        return null;
    }

    /**
     * insert/update/delete query execution
     *
     * @param source
     * @param query
     * @param context
     */
    public void execute(DBSourceDescriptor source, String query, Context context) {

    }

    class RowMapper {


    }

}
