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
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.BeanUtils;
import org.mini2Dx.beanutils.BeanUtilsBean;
import org.mini2Dx.beanutils.PropertyUtilsBean;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.validation.ValidatorFactory;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Helper class that sets UIComponent common attributes
 */
public class UIBuilderHelper {
    private static PropertyUtilsBean propUtils = BeanUtilsBean.getInstance().getPropertyUtils();

    public static void setUpRepo(ConfigNode node, boolean mandatory) {
        Object currentRepo = getElementValue(node.getElement(), "repo");
        boolean hasAttRepo = (currentRepo != null);
        if (hasAttRepo) {
            // it should be already setup
            return;
        }
        // go upwards in the tree looking for a parent with the repo attribute set
        ConfigNode ascendant = ConfigNodeHelper.findAscendantWithAttribute(node, "repo");
        Repository repo = (Repository) getElementValue(ascendant.getElement(), "repo");
        setElementValue(node.getElement(), "repo", repo);
    }

    public static Object getElementValue(Object element, String property) {
        try {
            return propUtils.getNestedProperty(element, property);
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Error while trying to get " +
                    "attribute '%s' in object [%s] ", property, element.getClass().getName())));
        }
    }

    public static void setElementValue(Object element, String property, Object value) {
        try {
            BeanUtils.setProperty(element, property, value);
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Error while trying to set " +
                            "attribute '%s' with value [%s] in object [%s] ", property, value,
                    element.getClass().getName())));
        }
    }

    /**
     * Helper class used from builders that handles repositories to get validated list of properties
     * from repository
     *
     * @param repo
     * @param propertyNames
     * @return
     */
    public static PropertyType[] getPropertiesFromRepo(Repository repo, String[] propertyNames) {
        PropertyType[] properties;
        int i = 0;
        PropertyType prop;
        EntityMeta meta = repo.getMeta();
        if (propertyNames == null || propertyNames.length == 0) {
            // get al repo properties
            properties = meta.getProperties();
        } else {
            properties = new PropertyType[propertyNames.length];
            // find and validate property names
            boolean hasError = false;
            for (String propName : propertyNames) {
                prop = meta.getPropertyByName(propName);
                if (prop == null) {
                    error(String.format("No property found [%s] in meta from [%s] repository. " +
                            "Available properties: [%s].", propName, repo.getId(), Arrays.toString(meta.getPropertyNames())));
                    hasError = true;
                } else {
                    properties[i] = prop;
                }
                i++;
            }
            if (hasError) {
                throw new ConfigurationException(error("Invalid repository property referenced in " +
                        "'properties' attribute check file '${file}. See developer console for more info."));
            }
        }
        return properties;
    }


    /**
     * If dbFile and dbTableName are defined, created nested <repo/> node to be
     * processed by RepoConfigBuilder
     *
     * @param node
     */
    public static void addDefaultRepoNode(ConfigNode node) {
        String dbFile = node.getAttribute("dbFile");
        String tableName = node.getAttribute("tableName");

        if (StringUtils.isNotBlank(dbFile) && StringUtils.isNotBlank(tableName)) {
            ConfigNode repoNode = new ConfigNode("repo");
            repoNode.setAttribute("dbFile", dbFile);
            repoNode.setAttribute("tableName", tableName);
            node.getChildren().add(0, repoNode);
        } else if (StringUtils.isNotBlank(dbFile) ^ StringUtils.isNotBlank(tableName)) {
            error(String.format("Incorrect repository definition, both 'dbFile' and 'dbTable' " +
                    "must be set in tag ${tag} id[%s].", node.getId()));
        }
    }

    /**
     * If given attribute is not set in current node, get it from the first ancestor that has it set
     *
     * @param node
     * @param attName
     */
    public static void inheritAttribute(ConfigNode node, String attName) {
        if (!node.hasAttribute(attName)) {
            String ascendantAttValue = ConfigNodeHelper.findAscendantAtt(node, attName);
            if (ascendantAttValue != null) {
                node.setAttribute(attName, ascendantAttValue);
            }
        }
    }

    public static void setUpValueExpressionType(ConfigNode<? extends UIInputComponent> node) {

        // set converter if needed
        UIInputComponent element = node.getElement();
        ValueBindingExpression expr = element.getValueExpression();
        if (expr != null && !expr.isReadOnly()) {
            String propertyName = expr.getBindingProperty();
            EntityMeta meta = null;
            try {
                UIForm form = (UIForm) ConfigNodeHelper.getAscendantByTag(node, "form").getElement();
                meta = form.getRepo().getMeta();
                String[] splits = propertyName.split("\\.");
                int pos = splits.length - 1;
                PropertyType property = meta.getPropertyByName(splits[pos]);

                // set convering type
                expr.setExpectedType(property.getType());
            } catch (Exception e) {
                // TODO: I'm pretty sure this is never not going to happen #204350
            }
        }
    }

    /**
     * @param root
     * @param repo
     */
    public static void addNodesFromPropertiesAtt(ConfigNode root, Repository repo) {
        // get the existing properties in the repo
        String[] propertyNames = getEffectiveAttributeProperties(repo, root);

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
     * Gets the list of properties defined in the node attribute that are in the repository
     *
     * @param repo
     * @param node
     * @return A list with all existing properties
     */
    private static String[] getEffectiveAttributeProperties(Repository repo, ConfigNode node) {
        List<String> effectiveProps = new ArrayList<>();
        EntityMeta meta;
        try {
            meta = repo.getMeta();
            String propertySelector = node.getAttribute("properties");

            String[] properties;
            if (!propertySelector.equals("*") && !propertySelector.equals("all")) {
                properties = propertySelector.replace(" ", "").split(",");
                for (String property : properties) {
                    //TODO check JEXL exception
                    if (meta.containsProperty(property) && !isChildIncluded(property, node)) {
                        effectiveProps.add(property);
                    }
                }
            } else {
                properties = meta.getPropertyNames();
                for (String property : properties) {
                    if (!isChildIncluded(property, node)) {
                        effectiveProps.add(property);
                    }
                }
            }


        } catch (Exception e) {
            //TODO manage when repo.getMeta() throws an exception
        }

        return effectiveProps.toArray(new String[effectiveProps.size()]);
    }

    /**
     * Checks if a node already has a child for a property name
     *
     * @param propertyName
     * @param root
     * @return
     */
    private static boolean isChildIncluded(String propertyName, ConfigNode root) {
        boolean isIncluded = false;

        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode child : children) {
                if (child.getId().equals(propertyName)) {
                    isIncluded = true;
                    break;
                }
            }
        }

        return isIncluded;
    }

    /**
     * Maps the property type to the most suitable Field type
     *
     * @param property
     * @return
     */
    public static ConfigNode createNode(PropertyType property) {
        ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

        UIField.TYPE type = UIBuilderHelper.getType(property);

        ConfigNode node = new ConfigNode("input");
        node.setId(property.name);
        node.setAttribute("type", type.toString());
        node.setAttribute("label", property.name);

        ComponentBuilder<UIField> builder = ComponentBuilderFactory.getInstance().getBuilder("input", UIField.class);

        UIField field = builder.build(node);
        node.setElement(field);


        ValueBindingExpression ve = exprFactory.create("${entity." + property.name + "}", property.getType());
        field.setValueExpression(ve);
        if (property.isPrimaryKey()) {
            // if the property is pk, do not show if the value is empty
            ve = exprFactory.create("${not empty(entity." + property.name + ")}", property.getType());
            field.setRenderExpression(ve);
            field.setReadOnly(true);
        }

        UIBuilderHelper.addValidators(field, property);

        return node;
    }

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

    public static void addValidators(UIField baseModel, PropertyType property) {
        ValidatorFactory validatorFactory = ValidatorFactory.getInstance();
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
