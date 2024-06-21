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
import org.greenrobot.greendao.query.WhereCondition;

import es.jcyl.ita.formic.repo.query.Condition;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)ç
 * <p>
 * Repo operator to Greendao conditions translator.
 */
public class PropertyConditionHelper {

    public static WhereCondition create(Condition c, Property property) {
        switch (c.getOp()) {
            case EQ:
                return property.eq(c.getValue());
            case LT:
                return property.lt(c.getValue());
            case GT:
                return property.gt(c.getValue());
            case LE:
                return property.le(c.getValue());
            case GE:
                return property.ge(c.getValue());
            case NOT_IN:
                return property.notIn(c.getValues());
            case IN:
                return property.in(c.getValues());
            case NOT_NULL:
                return property.isNotNull();
            case IS_NULL:
                return property.isNull();
            case LIKE:
                return property.like(c.getValue().toString());
            case CONTAINS:
                return property.like("%" + c.getValue().toString() + "%");
            case STARTS_WITH:
                return property.like(c.getValue().toString() + "%");
            case ENDS_WITH:
                return property.like("%" + c.getValue().toString());
            default:
                throw new UnsupportedOperationException("Not implemented yet!");
        }
    }

}
