package es.jcyl.ita.formic.repo.memo.source;
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

import java.io.File;

import es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder;
import es.jcyl.ita.formic.repo.source.AbstractEntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;

/**
 * Identification for in memory repositories
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MemoSource extends AbstractEntitySource {

    public MemoSource(String id) {
        this.source = new Source(id,id,null);
        this.entityTypeId = this.source.getId();
    }

    public class MemorySourceBuilder extends AbstractEntitySourceBuilder<MemoSource> {
        public MemorySourceBuilder(EntitySourceFactory factory) {
            super(factory);
        }

        @Override
        protected MemoSource doBuild() {
            return new MemoSource(this.getSource().getId());
        }
    }
}
