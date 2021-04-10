package es.jcyl.ita.formic.repo.builders;
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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.db.builders.RawSQLiteRepoBuilder;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.RawSQLiteRepository;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.sql.TableSQLBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Class with helper methods to create and populate SQLite databases using different builders
 * to reduce boilerplate code.
 */

public class DevDbBuilder {

    // databuilders and factories
    public static SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    // resulting objects
    private DaoMaster daoMaster;
    private EntityMeta meta;
    private DBTableEntitySource source;

    // builder properties
    private Database db;
    private int numEntities = 10;
    private Map<String, Object[]> limitedValues;
    private EditableRepository repo;

    public DevDbBuilder withNumEntities(int num) {
        this.numEntities = num;
        return this;
    }

    /**
     * Limits the possible values that the given field can have when creating random data.
     *
     * @param prop
     * @param values
     * @return
     */
    public DevDbBuilder withPropertyValues(String prop, Object[] values) {
        if (limitedValues == null) {
            limitedValues = new HashMap<String, Object[]>();
        }
        limitedValues.put(prop, values);
        return this;
    }

    public DevDbBuilder withMeta(EntityMeta meta) {
        this.meta = meta;
        return this;
    }

    public void build(Context ctx) {
        build(ctx, RandomUtils.randomString(10));
    }

    public void build(Context ctx, String name) {
        Database db = createDevSQLiteDb(ctx, name);
        build(db);
    }

    /**
     * Builds a poppulated database
     *
     * @param db
     */
    public void build(Database db) {
        this.db = db;
        this.daoMaster = new DaoMaster(db);
        if (this.meta == null) {
            this.meta = createRandomMeta(RandomUtils.randomString(10), this.limitedValues);
        }
        // create table using meta information
        DevDbBuilder.createTable(this.getDb(), this.meta);

        // create entity source using database and tableName
        this.source = DevDbBuilder.createEntitySource("db", this.meta.getName(), "xx", db);

        // create repository
        this.repo = getSQLiteRepository(source, this.meta);

        // create and insert test data
        List<Entity> lstEntities = buildEntities(this.meta, this.numEntities, this.limitedValues);
        for (Entity e : lstEntities) {
            e.setId(null); // set id to null to make sure entities are inserted
            this.repo.save(e);
        }
    }

    public SQLiteRepository getSQLiteRepository() {
        return getSQLiteRepository(source, this.meta);
    }

    public SQLiteRepository getSQLiteRepository(DBTableEntitySource source, EntityMeta meta) {
        return getSQLiteRepository(source, meta, null);
    }


    public SQLiteRepository getSQLiteRepository(DBTableEntitySource source, EntityMeta meta,
                                                es.jcyl.ita.formic.core.context.Context dataContext) {
        // Get a builder and create repository instance
        RepositoryFactory.getInstance().setContext(dataContext); //TODO: replace with dep-injection
        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(source);
        EntityDaoConfig conf = new EntityDaoConfig(meta, source);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();

        return (SQLiteRepository) RepositoryFactory.getInstance().getEditableRepo(source.getEntityTypeId());
    }


    public RawSQLiteRepository getRawSQLiteRepoBuilder(NativeSQLEntitySource source,
                                                       es.jcyl.ita.formic.core.context.Context dataContext) {
        RepositoryFactory.getInstance().setContext(dataContext); //TODO: replace with dep-injection
        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(source);
        builder.withProperty(RawSQLiteRepoBuilder.DB_ENTITY_SOURCE, source);
        builder.build();

        return (RawSQLiteRepository) RepositoryFactory.getInstance().getRepo(source.getEntityTypeId());
    }

    /////////////////////////////////////////////////
    // Accessors for built-in objects
    /////////////////////////////////////////////////
    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public EntityMeta getMeta() {
        return meta;
    }

    public DBTableEntitySource getSource() {
        return source;
    }

    public Database getDb() {
        return db;
    }

    public EditableRepository getRepo() {
        return repo;
    }

    ///////////////////////////////////////////////
    //// Helper functions
    ///////////////////////////////////////////////


    public static EntityMeta createRandomMeta() {
        return createRandomMeta(RandomUtils.randomString(10), null);
    }

    public static EntityMeta createRandomMeta(String name) {
        return createRandomMeta(name, null);
    }

    public static EntityMeta createRandomMeta(String name, Map<String, Object[]> limitedValues) {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        metaBuilder.withBasicTypes(true).withRandomData().withEntityName(name);
        if (limitedValues != null) {
            // add properties with expected data types
            for (Map.Entry<String, Object[]> entry : limitedValues.entrySet()) {
                metaBuilder.addProperty(entry.getKey(), entry.getValue()[0].getClass());
            }
        }
        return metaBuilder.build();
    }

    /**
     * Set default converters for each property of the given map
     *
     * @param meta
     * @return
     */
    public static void setDefaultConverters(EntityMeta<DBPropertyType> meta) {
        SQLitePropertyConverter conv;
        DBPropertyType[] props = meta.getProperties();
        for (int i = 0; i < props.length; i++) {
            conv = convFactory.getDefaultConverter(props[i].getType());
            DBPropertyType newP = new DBPropertyType.DBPropertyTypeBuilder(props[i]).withConverter(conv).build();
            props[i] = newP;
        }
    }

    public static Map<String, String> createDefaultMapper(EntityMeta meta) {
        Map<String, String> mapper = new HashMap<String, String>();
        for (PropertyType p : meta.getProperties()) {
            mapper.put(p.getName(), p.getName()); // Property name as columnName
        }
        return mapper;
    }


    public static DBTableEntitySource createEntitySource(String sourceId, String entityType, String path, Database db) {
        Source dbSource = new Source(sourceId, path, db);
        EntitySourceBuilder sourceBuilder = EntitySourceFactory.getInstance()
                .getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        sourceBuilder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, dbSource);
        sourceBuilder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, entityType);
        sourceBuilder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, entityType);
        return (DBTableEntitySource) sourceBuilder.build();
    }

    public static NativeSQLEntitySource createNativeSQLEntitySource(String sourceId, String entityType, String path, Database db, String query) {
        Source dbSource = new Source(sourceId, path, db);
        EntitySourceBuilder sourceBuilder = EntitySourceFactory.getInstance()
                .getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE_CURSOR);
        sourceBuilder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.SOURCE, dbSource);
        sourceBuilder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.ENTITY_TYPE_ID, entityType);
        sourceBuilder.withProperty(NativeSQLEntitySource.NativeSQLEntitySourceBuilder.QUERY, query);
        return (NativeSQLEntitySource) sourceBuilder.build();
    }

    public static EntityMeta createMetaFromSource(DBTableEntitySource source) {
        SQLiteMetaModeler modeler = new SQLiteMetaModeler();
        EntityMeta meta = modeler.readFromSource(source);
        DevDbBuilder.setDefaultConverters(meta);
        return meta;
    }

    public static EditableRepository createSQLiteRepository(EntitySource eSource) {
        MetaModeler metaModeler = new SQLiteMetaModeler();
        EntityMeta meta = metaModeler.readFromSource(eSource);
        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(eSource);
        EntityDaoConfig conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();
        return RepositoryFactory.getInstance().getEditableRepo(eSource.getEntityTypeId());
    }


    public static Entity buildEntity(EntityMeta meta) {
        return buildEntities(meta, 1, null).get(0);
    }

    public static List<Entity> buildEntities(EntityMeta meta, int numEntities) {
        return buildEntities(meta, numEntities, null);
    }
    public static List<Entity> buildEntitiesRandomMeta(int numEntities) {
        EntityMeta meta = createRandomMeta();
        return buildEntities(meta, numEntities, null);
    }

    /**
     * Creates an entity list using the entityBuilder class.
     *
     * @param meta:          Entity metadata with property data type descriptions.
     * @param numEntities:   Number of entities to create
     * @param limitedValues: restricted values to use when populating fields. The maps contains per
     *                       each property to limit and array with the values to use during the
     *                       populating process.
     * @return: list of entities populated with random data.
     */
    public static List<Entity> buildEntities(EntityMeta meta, int numEntities,
                                             Map<String, Object[]> limitedValues) {
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);

        List<Entity> lst = new ArrayList<Entity>();
        for (int i = 0; i < numEntities; i++) {
            entityBuilder.withRandomData();
            if (limitedValues != null) {
                // limit property values
                for (Map.Entry<String, Object[]> entry : limitedValues.entrySet()) {
                    entityBuilder.withLimitedValues(entry.getKey(), entry.getValue());
                }
            }
            Entity entity = entityBuilder.build();
            lst.add(entity);
        }
        return lst;
    }


    public static Database createDevSQLiteDb(Context ctx, String dbName) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, dbName, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        return new StandardDatabase(db);
    }

    ///////////////////////////////////////////////
    //// SQL Helper functions
    ///////////////////////////////////////////////

    public static void createTable(Database db, EntityMeta meta) {
        String script = TableSQLBuilder.createTableScript(meta, true);
        db.execSQL(script);
    }

    public static long countRecords(Database db, String tableName) {
        String query = "select count() from " + tableName;
        Cursor cursor = db.rawQuery(query, null);
        boolean available = cursor.moveToFirst();
        long count = cursor.getLong(0);
        return count;
    }

}
