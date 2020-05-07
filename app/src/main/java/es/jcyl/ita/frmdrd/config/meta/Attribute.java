package es.jcyl.ita.frmdrd.config.meta;
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
    public Class type;

    public Attribute(String name, boolean assignable) {
        this(name, assignable, String.class);
    }

    public Attribute(String name, boolean assignable, Class type) {
        this.name = name;
        this.assignable = assignable;
        this.type = type;
    }

}