package es.jcyl.ita.formic.repo.builders;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.db.spatialite.converter.SpatialiteBlobConverter;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatialitePropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityMetaDataBuilder extends AbstractDataBuilder<EntityMeta<DBPropertyType>> {

    DBPropertyTypeDataBuilder propDataBuilder = new DBPropertyTypeDataBuilder();
    private boolean useBasicTypes = false;

    @Override
    protected EntityMeta getModelInstance() {
        return new EntityMeta("", null, null);
    }

    public EntityMetaDataBuilder withBasicTypes(boolean value) {
        this.useBasicTypes = value;
        return this;
    }

    @Override
    public EntityMetaDataBuilder withRandomData() {
        int numProps = RandomUtils.randomInt(5, 10);
        return withNumProps(numProps);
    }

    public EntityMetaDataBuilder withEntityName(String name) {
        this.baseModel.setName(name);
        return this;
    }

    public EntityMetaDataBuilder withNumProps(int numProps) {
        DBPropertyType[] pArray = new DBPropertyType[numProps];
        boolean first = true;
        for (int i = 0; i < numProps; i++) {
            // use basic types for pk
            pArray[i] = propDataBuilder.withBasicTypes(this.useBasicTypes || first)
                    .withRandomData().withIsPrimaryKey(first)
                    .build();
            if (first) {
                // mark as pk just the first column
                first = false;
            }
        }
        String[] idProps = null;
        if(numProps >0) {
            idProps = new String[]{pArray[0].getName()};
        }
        String entityName = this.baseModel.getName();
        if (StringUtils.isBlank(entityName)) {
            entityName = RandomStringUtils.randomAlphabetic(10);
        }
        this.baseModel = new EntityMeta(entityName, pArray, idProps);
        return this;
    }

    public EntityMetaDataBuilder addProperties(Class[] clazzes) {
        for (Class clzz : clazzes) {
            addProperty(clzz);
        }
        return this;
    }

    public EntityMetaDataBuilder addProperty(Class type) {
        return addProperty(RandomUtils.randomString(5), type);
    }

    /**
     * Appends new property for the returning created object.
     *
     * @param name
     * @param type
     * @return
     */
    public EntityMetaDataBuilder addProperty(String name, Class type) {
        DBPropertyType[] existing = this.baseModel.getProperties();
        int numProps = (existing != null) ? existing.length : 0;
        numProps += 1;
        DBPropertyType[] pArray = new DBPropertyType[numProps];

        if (existing != null) {
            // copy existing props
            System.arraycopy(existing, 0, pArray, 0, existing.length);
        }
        boolean first = (existing == null);

        // add new property at the end of the array
        if (type.equals(Geometry.class)) {
            // create geometry property type
            SpatialitePropertyType.SpatialitePropertyTypeBuilder builder
                    = new SpatialitePropertyType.SpatialitePropertyTypeBuilder(name,
                    Geometry.class, "BLOB", false)
                    .withGeometryType(GeometryType.POLYGON)
                    .withSRID(25830);
            builder.withColumnName(name).withConverter(new SpatialiteBlobConverter(Geometry.class));
            pArray[pArray.length - 1] = builder.build();
        } else {
            pArray[pArray.length - 1] = propDataBuilder.withRandomData()
                    .withName(name).withType(type).withIsPrimaryKey(first).build();
        }

        String[] idProps;
        if (existing != null) {// copy from base model
            idProps = this.baseModel.getIdPropertiesName();
        } else {
            idProps = new String[]{pArray[0].getName()};
        }
        this.baseModel = new EntityMeta(this.baseModel.getName(), pArray, idProps);
        return this;
    }

    public EntityMetaDataBuilder addProperties(String[] names, Class[] types) {
        for (int i = 0; i < names.length; i++) {
            addProperty(names[i], types[i]);
        }
        return this;
    }

    public EntityMetaDataBuilder withIdProperties(String[] names) {
        baseModel.setIdProperties(names);
        return this;
    }

    @Override
    protected EntityMeta<DBPropertyType> doBuild(EntityMeta<DBPropertyType> templateModel) {
        EntityMeta meta =  super.doBuild(templateModel);
        meta.setIdProperties(templateModel.getIdPropertiesName());
        return meta;
    }
}
