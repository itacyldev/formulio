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

import android.database.Cursor;
import android.database.SQLException;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import es.jcyl.ita.formic.repo.RepositoryException;
import jsqlite.Exception;
import jsqlite.Stmt;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Adapter to access to a sqlite-spatialite database using GreenDAO ORM.
 */
public class SpatialiteDataBase implements Database {

    private final jsqlite.Database db;
    private final String path;

    public SpatialiteDataBase(String path, jsqlite.Database db) {
        this.db = db;
        this.path = path;
        try {
            db.open(path, jsqlite.Constants.SQLITE_OPEN_READWRITE);
        } catch (Throwable e) {
            throw new RepositoryException("An error occurred while trying to open Spatialite database: " + path, e);
        }
    }

    public Stmt prepare(String sql) {
        try {
            return this.db.prepare(sql);
        } catch (Exception e) {
            throw new DaoException("Error encounter while trying to prepare statement.", e);
        }
    }

    @Override
    public Cursor rawQuery(String query, String[] params) {
        return new SpatialiteCursorAdapter(this.db, query, params);
    }

    @Override
    public void execSQL(String sql) throws SQLException {
        try {
            this.db.exec(sql, null);
        } catch (Exception e) {
            throw new DaoException("Error encounter while trying to execute SQL: " + sql, e);
        }
    }

    @Override
    public void beginTransaction() {
        // nothing to do yet

    }

    @Override
    public void endTransaction() {
        // nothing to do yet
    }

    @Override
    public boolean inTransaction() {
        return false;
    }

    @Override
    public void setTransactionSuccessful() {

    }

    @Override
    public void execSQL(String s, Object[] objects) throws SQLException {

    }

    @Override
    public DatabaseStatement compileStatement(String sql) {
        try {
            return new SpatialiteStmt(this.db.prepare(sql));
        } catch (Exception e) {
            throw new DaoException("Error encounter while trying to compile statement: " + sql, e);
        }
    }

    @Override
    public boolean isDbLockedByCurrentThread() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public Object getRawDatabase() {
        return null;
    }

    public void open(String filePath, int mode) {
        try {
            this.db.open(filePath, mode);
        } catch (Exception e) {
            throw new DaoException("Error while trying to open database " + filePath, e);
        }
    }

    public String getPath() {
        return path;
    }
}
