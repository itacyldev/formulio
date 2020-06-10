package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.ArrayUtils;
import org.mini2Dx.collections.CollectionUtils;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.converters.ConverterMap;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIGroupComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.validation.Validator;
import es.jcyl.ita.frmdrd.validation.ValidatorFactory;

import static es.jcyl.ita.frmdrd.config.builders.UIBuilderHelper.getElementValue;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIGroupComponentBuilder<E extends UIGroupComponent> extends BaseUIComponentBuilder<E> {

    public UIGroupComponentBuilder(String tagName, Class<E> clazz) {
        super(tagName, clazz);
    }


    @Override
    protected void setupOnSubtreeStarts(ConfigNode<E> node) {
        Repository repo = null;
        if (node.getElement() instanceof UIForm) {
            UIBuilderHelper.setUpRepo(node, true);
            // Add a child node for all the properties defined in the properties attribute
            repo = ((UIForm) node.getElement()).getRepo();
        }

        if (repo == null) {
            ConfigNode ascendant = ConfigNodeHelper.findAscendantWithAttribute(node, "repo");
            repo = (Repository) getElementValue(ascendant.getElement(), "repo");
        }

        addNodesFromPropertiesAtt(node, repo);
    }

    /**
     * @param root
     * @param repo
     */
    protected void addNodesFromPropertiesAtt(ConfigNode<E> root, Repository repo) {
        // get the existing properties in the repo
        String[] propertyNames = UIBuilderHelper.getEffectiveAttributeProperties(repo, root);

        PropertyType[] properties;

        if (CollectionUtils.isEmpty(root.getChildren()) && ArrayUtils.isEmpty(propertyNames) || !ArrayUtils.isEmpty(propertyNames)) {
            properties = UIBuilderHelper.getPropertiesFromRepo(repo, propertyNames);
            for (PropertyType property : properties) {
                ConfigNode<UIInputComponent> node = createNode(property);
                root.addChild(node);
            }
        }
    }

    /**
     * Maps the property type to the most suitable Field type
     *
     * @param property
     * @return
     */
    private ConfigNode createNode(PropertyType property) {
        ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();


        ConfigNode node = new ConfigNode("input");
        node.setId(property.name);
        node.setAttribute("type", getType(property).toString());
        node.setAttribute("label", property.name);

        ComponentBuilder<UIField> builder = ComponentBuilderFactory.getInstance().getBuilder("input", UIField.class);


        UIField field = builder.build(node);
        node.setElement(field);
        node.setAttribute("value", "${entity." + property.name + "}", ConverterMap.getConverter(property.getType()));

        if (property.isPrimaryKey()) {
            // if the property is pk, do not show if the value is empty
            node.setAttribute("render", "${not empty(entity." + property.name + ")}");
            node.setAttribute("readonly", "true");
        }

        addValidators(node, property);

        return node;
    }

    private UIField.TYPE getType(PropertyType property) {
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

    private void addValidators(ConfigNode node, PropertyType property) {
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

    private ConfigNode<Validator> createValidatorNode(String type) {
        ConfigNode<Validator> validatorNode = new ConfigNode<>("validator");
        validatorNode.setAttribute("type", type);


        return validatorNode;
    }
}
