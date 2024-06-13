package es.jcyl.ita.formic.repo.db.query;
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

import org.greenrobot.greendao.query.WhereCondition;

import es.jcyl.ita.formic.repo.query.Predicate;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Gives access to Greendao conditions to avoid losing expressiveness in the filters
 */
public class RawWhereCondition extends Predicate {

    private final WhereCondition condition;

    public RawWhereCondition(WhereCondition cond) {
        this.condition = cond;
    }

    public WhereCondition getWhereCondition() {
        return this.condition;
    }


    public static RawWhereCondition fromString(String str) {
        return new RawWhereCondition(new WhereCondition.StringCondition(str));
    }
}
