package es.jcyl.ita.frmdrd.builders;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.ui.components.UIField;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Provides functionality to create fields easily from configuration.
 */
public class FieldBuilder {

    UIField baseModel;

    private UIField.TYPE getMappedField(PropertyType property) {
        Class type = property.getType();
        if (Number.class.isAssignableFrom(type) || ByteArray.class == type || String.class == type) {
            return UIField.TYPE.TEXT;
        }
        if (Boolean.class == type) {
            return UIField.TYPE.TEXT;
        }
        if (Geometry.class == type) {
            // TODO: by now, lets show the wkt
            return UIField.TYPE.TEXT;
        }
        throw new IllegalArgumentException("Unsupported property type: " + type.getName());
    }

    /**
     * Maps the property type to the most suitable Field type
     *
     * @param property
     * @return
     */
    public FieldBuilder withProperty(PropertyType property) {
        UIField.TYPE inputType = getMappedField(property);
        this.baseModel.setType(inputType);
        this.baseModel.setId(property.name);
        return this;
    }

    public UIField build() {
        UIField model = baseModel;
        baseModel = null;
        return model;
    }
}
