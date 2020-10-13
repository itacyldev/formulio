package es.jcyl.ita.crtrepo.builders;
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

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityDataBuilder extends AbstractDataBuilder<Entity> {

    private EntityMeta meta;
    DBPropertyTypeDataBuilder propDataBuilder = new DBPropertyTypeDataBuilder();

    @Override
    protected Entity getModelInstance() {
        // empty meta
        EntityMeta emptyMeta = new EntityMeta("",new PropertyType[]{}, new String[] {});
        return new Entity(null, emptyMeta, null);
    }

    public EntityDataBuilder(EntityMeta meta) {
        this.meta = meta;
        this.baseModel = createEmptyModel();
    }

    @Override
    public EntityDataBuilder withRandomData() {
        this.baseModel.setMetadata(this.meta);
        // create random property values based on metadata
        for (PropertyType p : this.meta.getProperties()) {
            Object value = RandomUtils.randomObject(p.getType());
            this.baseModel.set(p.getName(), value);
        }
        return this;
    }

    /**
     * Limits the possible value of the property to one on the given values.
     *
     * @param property
     * @param values
     * @return
     */
    public EntityDataBuilder withLimitedValues(String property, Object[] values) {
        int selectedValue = RandomUtils.randomInt(0, values.length - 1);
        this.baseModel.set(property, values[selectedValue]);
        return this;
    }

    protected Entity doBuild(Entity templateModel) {
        Entity entity = new Entity(null, this.meta, this.baseModel.getId());
        entity.setProperties(this.baseModel.getProperties());
        return entity;
    }
}
