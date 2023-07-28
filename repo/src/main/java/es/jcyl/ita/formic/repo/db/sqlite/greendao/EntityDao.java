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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.SqlUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.format.ValueFormatter;
import es.jcyl.ita.formic.core.format.ValueFormatterFactory;
import es.jcyl.ita.formic.repo.CursorPropertyBinder;
import es.jcyl.ita.formic.repo.CursorPropertyReader;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.converter.ConverterUtils;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.meta.KeyGeneratorStrategy;
import es.jcyl.ita.formic.repo.db.meta.MaxRowIdKeyGenerator;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteDBValue;
import es.jcyl.ita.formic.repo.db.sqlite.sql.TableSQLBuilder;
import es.jcyl.ita.formic.repo.el.JexlEntityUtils;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityDao extends AbstractDao<Entity, Object> implements TableScript {

    private Context context;
    private CursorPropertyReader propertyReader;
    private CursorPropertyBinder propertyBinder;

    private static MaxRowIdKeyGenerator rowIdKeyGenerator = new MaxRowIdKeyGenerator();

    public EntityDao(EntityDaoConfig daoConfig) {
        super(daoConfig);
        propertyReader = entityConfig().getPropertyReader();
        propertyBinder = entityConfig().getPropertyBinder();
    }

    public EntityDaoConfig entityConfig() {
        return (EntityDaoConfig) this.config;
    }

    @Override
    protected Object readKey(Cursor cursor, int offset) {
        Object key;
        EntityMeta<DBPropertyType> meta = this.entityConfig().getMeta();
        DBPropertyType[] props = meta.getProperties();
        // get column properties
        Integer[] idIdxs = this.entityConfig().getMeta().getIdIndexes();
        if (idIdxs == null) {
            return null;
        } else if (idIdxs.length == 1) {
            int index = idIdxs[0];
            key = propertyReader.readPropertyValue(cursor, props[index], offset + index);
        } else {
            key = readMultiColumnKey(cursor, props, idIdxs, offset);
        }
        return key;
    }

    private Object readMultiColumnKey(Cursor cursor, DBPropertyType[] props, Integer[] idIdxs, int offset) {
        Object[] values = new Object[idIdxs.length];
        int index;
        for (int i = 0; i < idIdxs.length; i++) {
            index = idIdxs[i];
            values[i] = propertyReader.readPropertyValue(cursor, props[index], offset + index);
        }
        return values;
    }

    public void save(Entity entity) {
        if (this.hasKey(entity)) {
            this.insertOrReplace(entity);
        } else {
            this.insert(entity);
        }

    }

    @Override
    protected Entity readEntity(Cursor cursor, int offset) {
        DBTableEntitySource source = this.entityConfig().getSource();
        EntityMeta meta = this.entityConfig().getMeta();
        Entity entity = new Entity(source, meta, 0);
        readEntity(cursor, entity, offset);
        return entity;
    }

    /**
     * Read entity properties from cursor and apply transformations using property converters.
     *
     * @param cursor
     * @param entity
     * @param offset
     */
    @Override
    protected void readEntity(Cursor cursor, Entity entity, int offset) {
        int i = 0;
        DBPropertyType dp;
        EntityMeta<PropertyType> meta = entity.getMetadata();
        for (PropertyType p : meta.getProperties()) {
            // read db value according to defined persistence type
            dp = (DBPropertyType) p;
            Object value;
            if (dp.isCalculatedOnSelect() && dp.isJexlExpression()) {
                // read calculated properties from context
                value = readCalculatedJexlProperty(dp);
            } else {
                value = propertyReader.readPropertyValue(cursor, dp, offset + i);
            }
            entity.set(p.getName(), value);
            i++;
        }
    }

    private Object readCalculatedJexlProperty(DBPropertyType p) {
        return JexlEntityUtils.eval(this.context, p.getExpression());
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, Entity entity) {
        stmt.clearBindings();
        Object value;
        SQLiteDBValue dbValue;
        int i = 1;
        EntityMeta<PropertyType> meta = entity.getMetadata();
        for (PropertyType p : meta.getProperties()) {
            DBPropertyType dp = (DBPropertyType) p;
            if (dp.isCalculatedOnSelect()) {
                continue; // the property value is not persisted
            }
            value = entity.get(p.getName());
            if (hasToBeCalculated(dp, value)) {
                value = calculateProperty(dp);
                entity.set(p.getName(), value);
            }
            propertyBinder.bindValue(stmt, dp, i, value);
            i++;
        }
    }

    private Object calculateProperty(DBPropertyType p) {
        // create expression and evaluate
        if (p.isJexlExpression()) {
            if (context == null) {
                throw new RepositoryException("Error while trying to evaluate Entity calculated expression:" +
                        "The context is null, make sure the RepositoryFactory has a context instance" +
                        " to se on repos during initialization.");
            }
            Object value = JexlEntityUtils.eval(context, p.getExpression());
            if (StringUtils.isNotBlank(p.getPattern())) {
                Class objectType = (value == null) ? Void.class : value.getClass();
                ValueFormatter formatter = ValueFormatterFactory.getInstance().getFormatter(objectType, p.getPattern(), p.getType());
                value = formatter.format(value, p.getPattern());
            }
            return value;
        } else {
            throw new UnsupportedOperationException("Not implemented yet!!");
        }
    }

    /**
     * Do we have to include current property in the binding for this type of statement?
     *
     * @param p
     * @return
     */
    private boolean hasToBeCalculated(DBPropertyType p, Object currentValue) {
        return p.isCalculatedOnUpdate() || (p.isCalculatedOnInsert() && currentValue == null);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Entity entity) {
        stmt.clearBindings();

        Object value;
        int i = 1;

        EntityMeta<PropertyType> meta = entity.getMetadata();
        DBPropertyType dp;
        for (PropertyType p : meta.getProperties()) {
            dp = (DBPropertyType) p;
            if (dp.isCalculatedOnSelect()) {
                continue; // the property value is not persisted
            }
            value = entity.get(p.getName());
            if (hasToBeCalculated(dp, value)) {
                value = calculateProperty(dp);
                // the calculated property might change not just the format but the type of the object.
                // The value will be converted before inserting in the bindValue method, but here with temporarily
                // disable the constraint to set the calculated value to the entity.
                entity.enablePropertyValidation(false);
                entity.set(p.getName(), value);
                entity.enablePropertyValidation(true);
            }
            propertyBinder.bindValue(stmt, dp, i, value);
            i++;
        }
    }

    /**
     * Creates a pk for the entity. If the entity already has a value in id properties, keeps that
     * value, if not, tries to use a KeyGeneratorStrategy to get a new key.
     * In case the rowId given is not -1, and the key strategy is also MAX_ROWID, it reuses the
     * given rowId value.
     *
     * @param entity
     * @param rowId
     * @return
     */
    @Override
    protected Object updateKeyAfterInsert(Entity entity, long rowId) {

        if (entity.getId() == null) {
            Object pkValue;
            // get generator to obtain entity key
            if (!entity.getMetadata().hasIdProperties()) {
                return null; // no pk defined
            }
            DBPropertyType pkProperty = (DBPropertyType) entity.getMetadata().getIdProperties()[0];
            Class pkType = pkProperty.getType();
            KeyGeneratorStrategy keyGenerator = this.entityConfig().getMeta().getKeyGenerator();
            if (keyGenerator == null) {
                // try to use given rowId or use a rowId generator
                if (rowId != -1) {
                    pkValue = ConvertUtils.convert(rowId, pkType);
                } else {
                    pkValue = rowIdKeyGenerator.getKey(this, entity, pkType);
                }
            } else {
                if (rowId != -1 && keyGenerator.getType() == KeyGeneratorStrategy.TYPE.MAXROWID) {
                    // used the already obtained key
                    pkValue = ConvertUtils.convert(rowId, pkType);
                } else {
                    pkValue = keyGenerator.getKey(this, entity, pkProperty.type);
                }
            }
            entity.setId(pkValue);
            // persist the key value to db
            updatePK(entity, rowId, pkValue);
        }
        return entity.getId();
    }

    private void updatePK(Entity entity, Long rowId, Object pkValue) {
        EntityMeta meta = entity.getMetadata();
        String sqlUpdate = SqlUtils.createSqlUpdate(meta.getName(), meta.getIdPropertiesName(), new String[]{"rowid"});
        Object[] bindingValues = new Object[]{pkValue, rowId}; //TODO: multicolumn support
        DatabaseStatement stmt = db.compileStatement(sqlUpdate);
        propertyBinder.bindValue(stmt, (DBPropertyType) meta.getIdProperties()[0], 1, pkValue);
        stmt.bindLong(2, rowId);
        stmt.execute();
    }

    @Override
    protected Object getKey(Entity entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected boolean hasKey(Entity entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        // this method is used by GreenDao's AbstrractDao to update the entity
        // Pk using the rowId returned by the PreparedStatement.
        // set to false to avoid this.
        // It is also used to control the IndentityObjectCache, if set to false, the records in
        // cache aren't update after executing UPDATE statement
        return true;
    }

    /**
     * Table drop/creation scripts
     */

    @Override
    public void createTable(Database db, boolean ifNotExists) {
        // TODO: use some kind of helpero to create SQL
        String createSQL = TableSQLBuilder.createTableScript(entityConfig().getMeta(), true);
        db.execSQL(createSQL);
    }

    @Override
    public void dropTable(Database db, boolean ifExists) {
        // TODO: use some kind of helper to create SQL
        entityConfig();
    }

    public Entity load(Object key) {
        this.assertSinglePk();
        if (key == null) {
            return null;
        } else {
            if (this.identityScope != null) {
                if (this.entityConfig().keyIsNumeric) {
                    // security convert to long the Id before accessing the entity cache
                    key = ConverterUtils.convertNumericToLong(key);
                }
                Entity entity = this.identityScope.get(key);
                if (entity != null) {
                    return entity;
                }
            }
            String sql = this.statements.getSelectByKey();
            // keys are always represented as object array
            String[] keyArray = convertKeysToString(key);
            Cursor cursor = this.db.rawQuery(sql, keyArray);
            return this.loadUniqueAndCloseCursor(cursor);
        }
    }

    /**
     * Converts primary key columns to the corresponding persistence-layer representation and
     * transform them to String class to be used as filter
     *
     * @param keys
     * @return
     */
    private String[] convertKeysToString(Object keys) {
        Object[] keysArray = null;
        if (!keys.getClass().isArray()) {
            // turn into array
            keysArray = new Object[]{keys};
        } else {
            keysArray = (Object[]) keys;
        }
        String[] convertedKeys = new String[keysArray.length];
        SQLitePropertyConverter converter;

        // idIndexes contains the position of the pk properties in the properties array
        EntityMeta<DBPropertyType> meta = this.entityConfig().getMeta();
        Integer[] idIndexes = meta.getIdIndexes();
        PropertyType[] props = meta.getProperties();
        for (int i = 0; i < idIndexes.length; i++) {
            DBPropertyType prop = (DBPropertyType) props[idIndexes[i]];
            convertedKeys[i] = prop.getConverter().toPersistence(keysArray[i]).value.toString();
        }
        return convertedKeys;
    }

    protected Entity loadUnique(Cursor cursor) {
        boolean available = cursor.moveToFirst();
        if (!available) {
            return null;
        } else {
            boolean isLast;
            try {
                isLast = cursor.isLast();
            } catch (UnsupportedOperationException e) {
                // method not supported by cursor, no fall-back solution
                isLast = true;
            }
            if (!isLast) {
                throw new DaoException("Expected unique result, but count was " + cursor.getCount());
            } else {
                return loadCurrent(cursor, 0, true);
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Property getPropertyByName(String name) {
        if (this.getProperties() == null) {
            return null;
        }
        for (Property p : this.getProperties()) {
            if (p.columnName.equals(name)) {
                return p;
            }
        }
        return null;
    }

}

