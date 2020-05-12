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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.validation.ValidatorFactory;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIFormBuilder extends AbstractUIComponentBuilder<UIForm> {

    private ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    private ValidatorFactory validatorFactory = ValidatorFactory.getInstance();

    public UIFormBuilder(String tagName) {
        super("form", UIForm.class);
    }

    @Override
    protected void doWithAttribute(UIForm element, String name, String value) {
    }

    @Override
    protected void doConfigure(UIForm element, ConfigNode<UIForm> node) {
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);
        element.setChildren(uiComponents);
        UIBuilderHelper.setUpRepo(node, true);
        setUpFields(node);
    }

    /**
     * Creates form input components automatically from repository meta
     *
     * @param node
     */
    private void setUpFields(ConfigNode<UIForm> node) {

        UIForm form = node.getElement();

        List<UIInputComponent> fields = form.getFields();

        // check if 'properties' attributes is defined to add more columns
        String propertySelector = node.getAttribute("properties");
        String[] propertyFilter;

        List<UIInputComponent> fieldsToAdd = null;
        if (StringUtils.isBlank(propertySelector)) {
            if (fields.size() == 0) {
                // no property is selected and no nested input fields, by default add all properties
                fieldsToAdd = createDefaultFields(form.getRepo(), new String[0]);
            }
        } else {
            if (propertySelector.equals("*") || propertySelector.equals("all")) {
                // use all repo properties
                propertyFilter = new String[0];
            } else {
                // comma-separated list of property names
                propertyFilter = StringUtils.split(propertySelector, ",");
            }
            // check if the property is already included in the XML (equal ID)
            List<String> filteredProperties = new ArrayList<>();
            for (String str : propertyFilter) {
                for (UIInputComponent c : fields) {
                    if (!c.getId().equalsIgnoreCase(str)) {
                        filteredProperties.add(str);
                    }
                }
            }
            // create columns with property selection
            fieldsToAdd = createDefaultFields(form.getRepo(), filteredProperties.toArray(new String[filteredProperties.size()]));
        }
        if (fieldsToAdd != null) {
            fields.addAll(fieldsToAdd);
            form.setChildren(fields.toArray(new UIComponent[fields.size()]));
        }
    }


    /**
     * checks the given property names exist in the form repository
     *
     * @param repo
     * @param propertyNames
     */
    private List<UIInputComponent> createDefaultFields(Repository repo, String[] propertyNames) {
        List<UIInputComponent> kids = new ArrayList<>();

        PropertyType[] properties;
        properties = UIBuilderHelper.getPropertiesFromRepo(repo, propertyNames);
        for (PropertyType property : properties) {
            UIField field = createField(property);
            kids.add(field);
        }
        return kids;
    }




    public UIField.TYPE getType(PropertyType property) {
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


    /**
     * Maps the property type to the most suitable Field type
     *
     * @param property
     * @return
     */
    public UIField createField(PropertyType property) {
        UIField.TYPE type = getType(property);

        ConfigNode node = new ConfigNode("input");
        node.setId(property.name);
        node.setAttribute("type", type.toString());
        node.setAttribute("label", property.name);

        ComponentBuilder<UIField> builder = getFactory().getBuilder("input", UIField.class);

        UIField field = builder.build(node);
        node.setElement(field);
        builder.processChildren(node);

        ValueBindingExpression ve = exprFactory.create("${entity." + property.name + "}", property.getType());
        field.setValueExpression(ve);
        if (property.isPrimaryKey()) {
            // if the property is pk, do not show if the value is empty
            ve = exprFactory.create("${not empty(entity." + property.name + ")}", property.getType());
            field.setRenderExpression(ve);
            field.setReadOnly(true);
        }
        addValidators(field, property);
        return field;
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

}
