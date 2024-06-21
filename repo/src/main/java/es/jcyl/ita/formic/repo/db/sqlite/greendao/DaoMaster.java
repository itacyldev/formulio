package es.jcyl.ita.formic.repo.db.sqlite.greendao;
/*
 * Copyright 2011-2020 the original author or authors.
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DaoMaster {
    public static final int DEFAULT_SCHEMA_VERSION = 1;

    protected final Database db;
    protected final int schemaVersion;
    protected final Map<String, EntityDaoConfig> daoConfigMap;

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        this(db, DEFAULT_SCHEMA_VERSION);
    }

    public DaoMaster(Database db, int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;
        daoConfigMap = new HashMap<String, EntityDaoConfig>();
    }

    public void register(String entityType, EntityDaoConfig config) {
        this.daoConfigMap.put(entityType, config);
    }

    public boolean isRegistered(String entityType){
        return this.daoConfigMap.containsKey(entityType);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public void createAllTables(boolean ifNotExists) {
        createAllTables(this.db, ifNotExists);
    }

    public void createAllTables(Database db, boolean ifNotExists) {
        DaoSession session = new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
        String entityType = null;
        EntityDao dao = null;
        for (Map.Entry<String, EntityDaoConfig> entry : this.daoConfigMap.entrySet()) {
            entityType = entry.getKey();
            dao = session.getEntityDao(entityType);
            dao.createTable(db, ifNotExists);
        }
    }
    public void dropAllTables(boolean ifExists) {
        dropAllTables(this.db, ifExists);
    }

    public void dropAllTables(Database db, boolean ifExists) {
        DaoSession session = new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
        String entityType = null;
        EntityDao dao = null;
        for (Map.Entry<String, EntityDaoConfig> entry : this.daoConfigMap.entrySet()) {
            entityType = entry.getKey();
            dao = session.getEntityDao(entityType);
            dao.dropTable(db, ifExists);
        }
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, DEFAULT_SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, DEFAULT_SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + DEFAULT_SCHEMA_VERSION);
//            createAllTables(db, false);
        }

    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            onCreate(db);
        }
    }

}
