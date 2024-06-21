package es.jcyl.ita.formic.forms.config.meta;
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
public class Attribute {
    public final String name;
    public final boolean assignable;
    public final Class type;
    public String resolver;
    public String setter;
    public boolean allowsExpression;

    public Attribute(String name) {
        this(name, true);
    }

    public Attribute(String name, boolean assignable) {
        this(name, assignable, String.class, null, null, true);
    }

    public Attribute(String name, Class type) {
        this(name, true, type, null, null, true);
    }

    public Attribute(String name, Class type, boolean allowsExpression) {
        this(name, true, type, null, null, allowsExpression);
    }

    public Attribute(String name, String setter, Class type) {
        this(name, true, type, null, setter, true);
    }

    public Attribute(String name, String setter, String resolver) {
        this(name, true, String.class, resolver, setter, false);
    }

    public Attribute(String name, boolean assignable, Class type, String resolver, String setter, boolean allowsExpression) {
        this.name = name;
        this.assignable = assignable;
        this.type = type;
        this.resolver = resolver;
        this.setter = setter;
        this.allowsExpression = allowsExpression;
    }

    @Override
    public String toString() {
        return name;
    }
}
