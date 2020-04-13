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

import android.text.InputType;

import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.validation.ValidatorFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Provides functionality to create fields easily from configuration.
 */
public class FieldBuilder {

    UIField baseModel;
    private ValueExpressionFactory exprFactory;
    ValidatorFactory validatorFactory;

    public FieldBuilder() {
        this.baseModel = new UIField();
        exprFactory = ValueExpressionFactory.getInstance();
        validatorFactory = ValidatorFactory.getInstance();
    }

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
        this.baseModel.setLabel(property.name);
        ValueBindingExpression ve = exprFactory.create("${entity." + property.name + "}", property.getType());
        this.baseModel.setValueExpression(ve);
        if (property.isPrimaryKey()) {
            // if the property is pk, do not show if the value is empty
            ve = exprFactory.create("${not empty(entity." + property.name + ")}", property.getType());
            this.baseModel.setRenderExpression(ve);
            this.baseModel.setReadOnly(true);
        }
        addValidators(this.baseModel, property);
        return this;
    }

    private void addValidators(UIField baseModel, PropertyType property) {
        Class type = property.getType();

        if (property.isMandatory() != null && property.isMandatory()) {
            baseModel.addValidator(validatorFactory.getValidator("required"));
        }

        if (type == Integer.class || type == Short.class || type == Long.class) {
            baseModel.addValidator(validatorFactory.getValidator("integer"));
            baseModel.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (type == Float.class || type == Double.class) {
            baseModel.addValidator(validatorFactory.getValidator("decimal"));
            baseModel.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        // used the label to set a validator email, correo, mail, phone, telefono,...
        String label = baseModel.getLabel();
        if (label.toLowerCase().contains("email") || label.toLowerCase().contains("correo")) {
            baseModel.addValidator(validatorFactory.getValidator("email"));
            baseModel.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if (label.toLowerCase().contains("phone") || label.toLowerCase().contains("telefono")) {
            baseModel.setInputType(InputType.TYPE_CLASS_PHONE);
        }
    }

    public UIField build() {
        UIField model = baseModel;
        this.baseModel = new UIField();
        return model;
    }
}
