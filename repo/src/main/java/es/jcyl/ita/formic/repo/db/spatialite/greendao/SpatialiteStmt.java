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

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.database.DatabaseStatement;

import jsqlite.Exception;
import jsqlite.Stmt;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SpatialiteStmt implements DatabaseStatement {

    private final Stmt delegate;

    public SpatialiteStmt(Stmt delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute() {
        try {
            this.delegate.step();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public long simpleQueryForLong() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void bindNull(int i) {
        try {
            this.delegate.bind(i);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public long executeInsert() {
        this.execute();
        return -1;
    }

    @Override
    public void bindString(int i, String s) {
        try {
            this.delegate.bind(i, s);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void bindBlob(int i, byte[] bytes) {
        try {
            this.delegate.bind(i, bytes);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void bindLong(int i, long l) {
        try {
            this.delegate.bind(i, l);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void clearBindings() {
        try {
            this.delegate.reset();
            this.delegate.clear_bindings();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void bindDouble(int i, double v) {
        try {
            this.delegate.bind(i, v);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void close() {
        try {
            this.delegate.close();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Object getRawStatement() {
        return delegate;
    }
}
