package es.jcyl.ita.formic.repo.db.meta;

import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.meta.PropertyType;

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

/**
 * PropertyType extension to support db persistence specific features.
 */
public class DBPropertyType extends PropertyType {

    public enum CALC_METHOD {CONTEXT, SQL_EXPRESSION}
    public enum CALC_MOMENT {SELECT, INSERT, UPDATE}

    protected String columnName;
    protected boolean calculated = false;
    protected CALC_METHOD calculateBy;
    protected CALC_MOMENT calculateOn;
    protected String contextExpression;
    protected String sqlExpression;
    protected SQLitePropertyConverter converter;
    protected KeyGeneratorStrategy keyGenerator;


    public DBPropertyType(String name, Class<?> type, String persistenceType, boolean primaryKey) {
        super(name, type, persistenceType, primaryKey);
    }

    public boolean isCalculatedOnSelect() {
        return this.calculated && this.calculateOn == CALC_MOMENT.SELECT;
    }

    public boolean isCalculatedOnInsert() {
        return this.calculated && this.calculateOn == CALC_MOMENT.INSERT;
    }

    public boolean isCalculatedOnUpdate() {
        return this.calculated && (this.calculateOn == CALC_MOMENT.INSERT || this.calculateOn == CALC_MOMENT.UPDATE);
    }

    public boolean isCalculatedByContext() {
        return calculateBy == CALC_METHOD.CONTEXT;
    }

    public String getContextExpression() {
        return contextExpression;
    }

    public String getSqlExpression() {
        return sqlExpression;
    }

    public CALC_MOMENT getCalculateOn() {
        return calculateOn;
    }

    public String getColumnName() {
        if (this.columnName == null || "".equals(this.columnName)) {
            return this.getName();
        } else {
            return this.columnName;
        }
    }

    public KeyGeneratorStrategy getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGeneratorStrategy keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public SQLitePropertyConverter getConverter() {
        if (converter == null) {
            throw new RuntimeException(String.format("No converter defined for property [%s].", this.getName()));
        }
        return converter;
    }

    // TODO PropertyType is supposed to be persistence independent, here some specific SQL features are
    //  included, create EntityMeta-PropertyType hierarchy?
    public static class DBPropertyTypeBuilder {
        protected final DBPropertyType property;

        protected DBPropertyType createInstance(String name, Class<?> type, String persistenceType, boolean primaryKey){
            return new DBPropertyType(name, type, persistenceType, primaryKey);
        }

        public DBPropertyTypeBuilder(DBPropertyType prop) {
            property = new DBPropertyType(prop.name, prop.type, prop.persistenceType, prop.primaryKey);
            property.columnName = prop.columnName;
            property.converter = prop.converter;
            property.calculated = prop.calculated;
            property.calculateBy = prop.calculateBy;
            property.calculateOn = prop.calculateOn;
            property.sqlExpression = prop.sqlExpression;
            property.contextExpression = prop.contextExpression;
        }

        public DBPropertyTypeBuilder(String name, Class<?> type, String persistenceType, boolean primaryKey) {
            property = createInstance(name, type, persistenceType, primaryKey);
        }

        public DBPropertyTypeBuilder withSQLExpression(String expression, CALC_MOMENT when) {
            this.property.calculated = true;
            this.property.sqlExpression = expression;
            this.property.calculateBy = CALC_METHOD.SQL_EXPRESSION;
            this.property.calculateOn = when;
            return this;
        }

        public DBPropertyTypeBuilder withContextExpression(String expression, CALC_MOMENT when) {
            this.property.calculated = true;
            this.property.contextExpression = expression;
            this.property.calculateBy = CALC_METHOD.CONTEXT;
            this.property.calculateOn = when;
            return this;
        }

        public DBPropertyTypeBuilder withConverter(SQLitePropertyConverter converter) {
            this.property.converter = converter;
            return this;
        }
        public DBPropertyTypeBuilder withColumnName(String name) {
            this.property.columnName = name;
            return this;
        }

        public DBPropertyType build() {
            return this.property;
        }
    }
}