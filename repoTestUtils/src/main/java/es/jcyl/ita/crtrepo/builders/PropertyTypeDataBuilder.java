package es.jcyl.ita.crtrepo.builders;
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

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class PropertyTypeDataBuilder extends AbstractDataBuilder<PropertyType> {

    private final SQLiteConverterFactory converterFactory;
    private boolean basicTypes = false;

    public PropertyTypeDataBuilder() {
        this.converterFactory = SQLiteConverterFactory.getInstance();
    }

    @Override
    protected PropertyType getModelInstance() {
        return new PropertyType("", String.class, "", false);
    }

    public PropertyTypeDataBuilder withBasicTypes(boolean value) {
        this.basicTypes = value;
        return this;
    }

    @Override
    public PropertyTypeDataBuilder withRandomData() {
        Class type;
        if (this.basicTypes) {
            type = RandomUtils.randomBasicType();
        } else {
            type = RandomUtils.randomType();
        }
        String name = RandomStringUtils.randomAlphabetic(10);
        SQLitePropertyConverter converter = converterFactory.getDefaultConverter(type);
        this.baseModel = new PropertyType(name, type, converter.persistenceType().name(), false);
        return this;
    }

    public PropertyTypeDataBuilder withType(Class type) {
        this.baseModel = new PropertyType(baseModel.name, type, baseModel.persistenceType, baseModel.isPrimaryKey());
        return this;
    }

    public PropertyTypeDataBuilder withName(String name) {
        this.baseModel = new PropertyType(name, baseModel.type, baseModel.persistenceType, baseModel.isPrimaryKey());
        return this;
    }

    public PropertyTypeDataBuilder withIsPrimaryKey(boolean isPk) {
        this.baseModel = new PropertyType(baseModel.name, baseModel.type, baseModel.persistenceType, isPk);
        return this;
    }

    protected PropertyType doBuild(PropertyType templateModel) {
        this.basicTypes = false;
        return new PropertyType(templateModel.name, templateModel.type, templateModel.persistenceType, templateModel.isPrimaryKey());
    }
}
