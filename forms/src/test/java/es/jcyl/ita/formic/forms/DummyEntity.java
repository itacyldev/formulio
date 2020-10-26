package es.jcyl.ita.formic.forms;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySource;

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

public class DummyEntity extends Entity {


    public DummyEntity(EntitySource source, EntityMeta meta, Object id) {
        super(source, meta, id);
    }

    @Override
    public void set(String prop, Object value) {
        this.getProperties().put(prop, value);
    }

    public void setId(Object id) {

    }
}