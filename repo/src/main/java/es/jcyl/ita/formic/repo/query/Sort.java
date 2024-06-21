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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class Sort {

    public enum SortType {ASC, DESC}

    private final String property;
    private final SortType type;

    public Sort(String property) {
        this(property, SortType.ASC);
    }

    public Sort(String property, SortType sortType) {
        this.property = property;
        this.type = sortType;
    }

    public static Sort asc(String property) {
        return new Sort(property, SortType.ASC);
    }

    public static Sort desc(String property) {
        return new Sort(property, SortType.DESC);
    }

    public String getProperty() {
        return property;
    }

    public SortType getType() {
        return type;
    }
    public boolean isAsc(){
        return this.type == SortType.ASC;
    }
}
