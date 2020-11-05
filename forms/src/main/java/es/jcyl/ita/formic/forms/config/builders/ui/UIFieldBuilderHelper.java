package es.jcyl.ita.formic.forms.config.builders.ui;
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

import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * Class with common methos to create Fiedl components from entity-meta information
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIFieldBuilderHelper {


    public static UIField.TYPE getType(PropertyType property) {
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
        throw new ConfigurationException(String.format("Unsupported data type in property [%s]: " + type, property.getName()));
    }

    public static void addValidators(ConfigNode node, PropertyType property) {
        Class type = property.getType();

        UIField baseModel = (UIField) node.getElement();
        if (property.isMandatory() != null && property.isMandatory()) {
            node.addChild(createValidatorNode("required"));
        }

        if (type == Integer.class || type == Short.class || type == Long.class) {
            node.addChild(createValidatorNode("integer"));
            node.setAttribute("inputType", String.valueOf(InputType.TYPE_CLASS_NUMBER));
        }

        if (type == Float.class || type == Double.class) {
            node.addChild(createValidatorNode("decimal"));
            node.setAttribute("inputType", String.valueOf(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
        }

        // used the label to set a validator email, correo, mail, phone, telefono,...
        String label = baseModel.getLabel();
        if (label.toLowerCase().contains("email") || label.toLowerCase().contains("correo")) {
            node.addChild(createValidatorNode("email"));
            baseModel.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if (label.toLowerCase().contains("phone") || label.toLowerCase().contains("telefono")) {
            baseModel.setInputType(InputType.TYPE_CLASS_PHONE);
        }
    }

    public static ConfigNode<Validator> createValidatorNode(String type) {
        ConfigNode<Validator> validatorNode = new ConfigNode<>("validator");
        validatorNode.setAttribute("type", type);
        return validatorNode;
    }
}
