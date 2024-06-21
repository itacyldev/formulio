package es.jcyl.ita.formic.repo.source;
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

public class Source<T> {

    private String id;
    private String uri;
    private T implementor;

    public Source(String id, String uri, T impl) {
        this.id = id;
        this.uri = uri;
        this.implementor = impl;
    }

    public String getId() {
        return this.id;
    }

    public String getURI() {
        return this.uri;
    }

    public T getImplementor() {
        return this.implementor;
    }
}
