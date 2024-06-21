package es.jcyl.ita.formic.repo.query;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public enum Operator {

    EQ("eq"), LT("lt"), GT("gt"), LE("le"), GE("ge"), IN("in"),
    NOT_IN("notin"), IS_NULL("isnull"), NOT_NULL("notnull"), LIKE("like"),
    CONTAINS("contains"), STARTS_WITH("startswith"), ENDS_WITH("endswith");

    private Set<String> aliases;

    Operator(String... aliases) {
        this.aliases = new HashSet<String>(Arrays.asList(aliases));
    }

    public static Operator getValue(String value) {
        for (Operator op : Operator.values()) {
            if (op.aliases.contains(value.toLowerCase())) {
                return op;
            }
        }
        throw new RuntimeException(value + " is not a proper operator name in  " +
                "es.jcyl.ita.formic.repo.query.Operator");
    }

}
