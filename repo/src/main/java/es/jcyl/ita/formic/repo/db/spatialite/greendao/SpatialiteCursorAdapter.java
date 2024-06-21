package es.jcyl.ita.formic.repo.db.spatialite.greendao;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import org.greenrobot.greendao.DaoException;

import es.jcyl.ita.formic.repo.db.sqlite.sql.SQLBuilder;
import jsqlite.Constants;
import jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;

/**
 * Adapter to provide cursor-based query funcionality based on a spatialite Stmt.
 */
public class SpatialiteCursorAdapter implements Cursor {

    public static final String ERROR_MSG_COLUMN_ACCESS = "Error occurred while trying to access column index ";

    private final Stmt stmt;
    // the jsqlite api doesn't support count on cursor, adicional cursor will be used to get number of results
    private final Stmt countStmt;
    private int position = 0;
    private boolean closed = false;
    private int count = -1;


    public SpatialiteCursorAdapter(Database db, String query, String[] params) {
        try {
            String countQuery;
            if (isPragma(query)) {
                countQuery = query;
            } else {
                countQuery = SQLBuilder.countQuery(query);
            }
            this.countStmt = db.prepare(countQuery);
            this.stmt = db.prepare(query);
            int i = 1;
            if (params != null) {
                for (String p : params) {
                    this.stmt.bind(i, p);
                    this.countStmt.bind(i, p);
                    i++;
                }
            }
        } catch (Exception e) {
            throw new DaoException("Error occurred while trying to open statement against Spatialite database. " +
                    "SQL: " + query + " Params: " + params, e);
        }
    }

    private boolean isPragma(String query) {
        return query.toLowerCase().contains("pragma");
    }

    @Override
    public int getCount() {
        // execute the query with a count outside
        if (count == -1) {
            try {
                this.countStmt.step();
                this.count = countStmt.column_int(0);
            } catch (Exception e) {
                throw new DaoException("Error occurred while trying to move cursor to next position.", e);
            }
        }
        return count;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public boolean move(int offset) {
        boolean hasNext = true;
        for (int i = this.position; i < offset; i++) {
            hasNext = moveToNext();
            if (!hasNext) {
                return false;
            }
        }
        this.position += offset;
        return true;
    }

    @Override
    public boolean moveToPosition(int position) {
        try {
            this.stmt.reset();
            return move(position);
        } catch (Exception e) {
            throw new DaoException("Error while trying to move current cursor.", e);
        }
    }

    @Override
    public boolean moveToFirst() {
        try {
            if (position > 0) {
                stmt.reset();
            }
            this.position = 0;
            return this.stmt.step();
        } catch (Exception e) {
            throw new DaoException("Error occurred while trying to move current cursor to first position.", e);
        }
    }

    @Override
    public boolean moveToLast() {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public boolean moveToNext() {
        try {
            return this.stmt.step();
        } catch (Exception e) {
            throw new DaoException("Error occurred while trying to move cursor to next position.", e);
        }
    }

    @Override
    public boolean moveToPrevious() {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public boolean isFirst() {
        return position == 0;
    }

    @Override
    public boolean isLast() {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public boolean isBeforeFirst() {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public boolean isAfterLast() {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public int getColumnIndex(String columnName) {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public String getColumnName(int columnIndex) {
        return getColumnNames()[columnIndex];
    }

    @Override
    public String[] getColumnNames() {
        try {
            String[] cols = new String[getColumnCount()];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = stmt.column_origin_name(i);
            }
            return cols;
        } catch (Exception e) {
            throw new DaoException("Error occurred while trying to get column name by index.", e);
        }
    }

    @Override
    public int getColumnCount() {
        try {
            return stmt.column_count();
        } catch (Exception e) {
            throw new DaoException("Error occurred while trying to get number of columns from Statement.", e);
        }
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        try {
            return stmt.column_bytes(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public String getString(int columnIndex) {
        try {
            return stmt.column_string(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        try {
            Integer value = stmt.column_int(columnIndex);
            return value.shortValue();
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public int getInt(int columnIndex) {
        try {
            return stmt.column_int(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public long getLong(int columnIndex) {
        try {
            return stmt.column_long(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public float getFloat(int columnIndex) {
        try {
            Double value = stmt.column_double(columnIndex);
            return value.floatValue();
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public double getDouble(int columnIndex) {
        try {
            return stmt.column_double(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public int getType(int columnIndex) {
        try {
            return stmt.column_type(columnIndex);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public boolean isNull(int columnIndex) {
        try {
            return (getType(columnIndex) == Constants.SQLITE_NULL || stmt.column(columnIndex) == null);
        } catch (Exception e) {
            throw new DaoException(ERROR_MSG_COLUMN_ACCESS + columnIndex, e);
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {
        try {
            this.stmt.close();
            this.closed = true;
        } catch (Exception e) {
            throw new DaoException("Error ocurred while trying to close current cursor.", e);
        }

    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    public void setExtras(Bundle extras) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }


    /**
     * wraper to select the proper binding method depending on the value class
     *
     * @param stmt
     * @param i
     * @param value
     */
    private void bindParam(Stmt stmt, int i, Object value) {
        // TODO: a converter has to be applied here


    }
}
