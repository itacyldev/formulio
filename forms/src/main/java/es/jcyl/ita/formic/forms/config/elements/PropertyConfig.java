package es.jcyl.ita.formic.forms.config.elements;
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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class PropertyConfig {
    private String name;
    private String columnName;
    private String valueConverter;
    private String expression;
    private String expressionType; // jexl/sql
    private String evaluatedOn;//insert/update/select


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getEvaluatedOn() {
        return evaluatedOn;
    }

    public void setEvaluatedOn(String evaluatedOn) {
        this.evaluatedOn = evaluatedOn;
    }

    public boolean isCalculatedOnSelect() {
        return StringUtils.isBlank(evaluatedOn) ? false : evaluatedOn.equalsIgnoreCase("select");
    }

    public String getValueConverter() {
        return valueConverter;
    }

    public void setValueConverter(String valueConverter) {
        this.valueConverter = valueConverter;
    }
}
