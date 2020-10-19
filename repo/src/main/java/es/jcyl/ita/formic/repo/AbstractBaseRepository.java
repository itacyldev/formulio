package es.jcyl.ita.formic.repo;
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

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareRepo;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractBaseRepository<T extends Entity, F extends Filter>
        implements Repository<T, F>, ContextAwareRepo {
    protected String id;
    protected Context context;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        if (this.id != null) {
            return this.id;
        } else {
            return String.format("%s#%s", this.getSource().getSourceId(),
                    this.getSource().getEntityTypeId());
        }
    }

    public void setContext(Context ctx) {
        this.context = ctx;
    }

}
