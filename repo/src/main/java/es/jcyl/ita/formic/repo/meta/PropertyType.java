package es.jcyl.ita.formic.repo.meta;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class PropertyType {
    // mandatory fields
    public final Class<?> type;
    public final String name;
    public final boolean primaryKey;
    public final String persistenceType;
    // optional fields
    private Boolean mandatory;
    private Integer length;
    private Integer decimalPlaces;

    public PropertyType(String name, Class<?> type, String persistenceType, boolean primaryKey) {
        this.name = name;
        this.type = type;
        this.persistenceType = persistenceType;
        this.primaryKey = primaryKey;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s) PRIMARY KEY: %s", this.name, this.type, this.persistenceType, primaryKey);
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
}
