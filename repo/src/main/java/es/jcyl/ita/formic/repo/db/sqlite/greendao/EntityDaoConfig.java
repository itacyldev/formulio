package es.jcyl.ita.formic.repo.db.sqlite.greendao;
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

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.internal.TableStatements;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.CursorPropertyBinder;
import es.jcyl.ita.formic.repo.CursorPropertyReader;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

/**
 * Extends greenDao configuration class to allow creating configuration from Entity metadata.
 */
public class EntityDaoConfig extends DaoConfig {

    private String entityType;
    private DBTableEntitySource source;
    private EntityMeta meta;

    protected CursorPropertyBinder propertyBinder;
    protected CursorPropertyReader propertyReader;
    protected TableStatementProvider stmtProvider;


    public EntityDaoConfig(EntityMeta meta, DBTableEntitySource source) {
        // TODO: apply genetic type configuration to allow each safe type-checks
        this.db = source.getDb();
        this.meta = meta;
        this.source = source;
        this.entityType = meta.getName();
        this.tablename = source.getTableName();

        // create greenDAO properties from entity's metadata
        this.properties = createProperties(meta);
        allColumns = new String[properties.length];

        List<String> pkColumnList = new ArrayList<String>();
        List<String> nonPkColumnList = new ArrayList<String>();
        Property lastPkProperty = null;
        for (int i = 0; i < properties.length; i++) {
            Property property = properties[i];
            String name = property.columnName;
            allColumns[i] = name;
            if (property.primaryKey) {
                pkColumnList.add(name);
                lastPkProperty = property;
            } else {
                nonPkColumnList.add(name);
            }
        }
        String[] nonPkColumnsArray = new String[nonPkColumnList.size()];
        nonPkColumns = nonPkColumnList.toArray(nonPkColumnsArray);
        String[] pkColumnsArray = new String[pkColumnList.size()];
        pkColumns = pkColumnList.toArray(pkColumnsArray);

        pkProperty = pkColumns.length == 1 ? lastPkProperty : null;
        // TODO: the statements
        statements = new TableStatements(db, tablename, allColumns, pkColumns);

        if (pkProperty != null) {
            Class<?> type = pkProperty.type;
            keyIsNumeric = type.equals(Long.TYPE) || type.equals(Long.class) || type.equals(Integer.TYPE)
                    || type.equals(Integer.class) || type.equals(Short.TYPE) || type.equals(Short.class)
                    || type.equals(Byte.TYPE) || type.equals(Byte.class);
        } else {
            keyIsNumeric = false;
        }
        // default implementations
        propertyBinder = new CursorPropertyBinder();
        propertyReader = new CursorPropertyReader();
    }

    private Property[] createProperties(EntityMeta<DBPropertyType> meta) {
        String columnName;
        Property[] props = new Property[meta.getProperties().length];
        int i = 0;
        for (DBPropertyType prop : meta.getProperties()) {
            props[i] = new Property(0, prop.getType(), prop.getName(),
                    prop.isPrimaryKey(), prop.getColumnName());
            i++;
        }
        return props;
    }

    public DBTableEntitySource getSource() {
        return source;
    }

    public EntityMeta getMeta() {
        return meta;
    }

    public CursorPropertyBinder getPropertyBinder() {
        return propertyBinder;
    }

    public CursorPropertyReader getPropertyReader() {
        return propertyReader;
    }

    public void setPropertyBinder(CursorPropertyBinder propertyBinder) {
        this.propertyBinder = propertyBinder;
    }

    public void setPropertyReader(CursorPropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public void setTableStatementsProvider(TableStatementProvider provider) {
        this.stmtProvider = provider;
        this.stmtProvider.setDB(this.db);
        this.stmtProvider.setEntityMeta(this.meta);
        this.statements = this.stmtProvider.build();
    }

}
