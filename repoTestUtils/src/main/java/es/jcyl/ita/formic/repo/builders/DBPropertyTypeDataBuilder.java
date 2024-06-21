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

import org.apache.commons.lang3.RandomStringUtils;

import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DBPropertyTypeDataBuilder extends AbstractDataBuilder<DBPropertyType> {

    private final SQLiteConverterFactory converterFactory;
    private boolean basicTypes = false;

    public DBPropertyTypeDataBuilder() {
        this.converterFactory = SQLiteConverterFactory.getInstance();
    }

    @Override
    protected DBPropertyType getModelInstance() {
        return new DBPropertyType("", String.class, "", false);
    }

    public DBPropertyTypeDataBuilder withBasicTypes(boolean value) {
        this.basicTypes = value;
        return this;
    }

    @Override
    public DBPropertyTypeDataBuilder withRandomData() {
        Class type;
        if (this.basicTypes) {
            type = RandomUtils.randomBasicType();
        } else {
            type = RandomUtils.randomType();
        }
        String name = RandomStringUtils.randomAlphabetic(10);
        SQLitePropertyConverter converter = converterFactory.getDefaultConverter(type);
        String persistenceType = converter.persistenceType().name();
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(name, type, persistenceType, false).
                withConverter(converter).build();
        return this;
    }

    public DBPropertyTypeDataBuilder withType(Class type) {
        // get default converter for given type
        SQLitePropertyConverter converter = converterFactory.getDefaultConverter(type);
        String persistenceType = converter.persistenceType().name();
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(baseModel.name, type, persistenceType, baseModel.isPrimaryKey())
                .withConverter(converter)
                .withExpression(baseModel.getExpression(), baseModel.getCalculateBy(), baseModel.getCalculateOn())
                .build();
        return this;
    }

    public DBPropertyTypeDataBuilder withName(String name) {
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(name, baseModel.type, baseModel.persistenceType, baseModel.isPrimaryKey())
                .withConverter(baseModel.getConverter())
                .withExpression(baseModel.getExpression(), baseModel.getCalculateBy(), baseModel.getCalculateOn())
                .build();
        return this;
    }

    public DBPropertyTypeDataBuilder withIsPrimaryKey(boolean isPk) {
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(baseModel.name, baseModel.type, baseModel.persistenceType, isPk)
                .withConverter(baseModel.getConverter())
                .withExpression(baseModel.getExpression(), baseModel.getCalculateBy(), baseModel.getCalculateOn())
                .build();
        return this;
    }

    public DBPropertyTypeDataBuilder withCalcContext(DBPropertyType.CALC_MOMENT when, String expression) {
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(baseModel)
                .withConverter(baseModel.getConverter())
                .withJexlExpresion(expression, when)
                .build();
        return this;
    }

    public DBPropertyTypeDataBuilder withCalcSQL(DBPropertyType.CALC_MOMENT when, String expression) {
        this.baseModel = new DBPropertyType.DBPropertyTypeBuilder(baseModel)
                .withConverter(baseModel.getConverter())
                .withSQLExpression(expression, when)
                .build();
        return this;
    }

    protected DBPropertyType doBuild(DBPropertyType templateModel) {
        this.basicTypes = false;
        return new DBPropertyType.DBPropertyTypeBuilder(baseModel.name, baseModel.type, baseModel.persistenceType, baseModel.primaryKey)
                .withConverter(baseModel.getConverter())
                .withExpression(baseModel.getExpression(), baseModel.getCalculateBy(), baseModel.getCalculateOn())
                .build();
    }
}
