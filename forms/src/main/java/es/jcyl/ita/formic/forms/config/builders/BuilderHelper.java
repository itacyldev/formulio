package es.jcyl.ita.formic.forms.config.builders;
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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.BeanUtils;
import org.mini2Dx.beanutils.BeanUtilsBean;
import org.mini2Dx.beanutils.PropertyUtilsBean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.TagDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.util.TypeUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Helper class that sets UIComponent common attributes
 */
public class BuilderHelper {
    private static PropertyUtilsBean propUtils = BeanUtilsBean.getInstance().getPropertyUtils();

    public static void setUpRepo(ConfigNode node, boolean mandatory) {
        Object currentRepo = getElementValue(node.getElement(), "repo");
        boolean hasAttRepo = (currentRepo != null);
        if (hasAttRepo) {
            // it should be already setup
            return;
        }
        // go upwards in the tree looking for a parent with the repo attribute set
        ConfigNode ascendant = BuilderHelper.findParentRepo(node);
        if (ascendant != null) {
            Repository repo = (Repository) getElementValue(ascendant.getElement(), "repo");
            setElementValue(node.getElement(), "repo", repo);
        }
    }


    public static Object getElementValue(Object element, String property) {
        try {
            return propUtils.getNestedProperty(element, property);
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Error while trying to get " +
                    "attribute '%s' in object [%s] ", property, element.getClass().getName())), e);
        }
    }

    public static void setElementValue(Object element, String property, Object value) {
        try {
            BeanUtils.setProperty(element, property, value);
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Error while trying to set " +
                            "attribute '%s' with value [%s] in object [%s] ", property, value,
                    element.getClass().getName())), e);
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
        if (repo == null) {
            throw new ConfigurationException(error("No repository defined in file ${file}. " +
                    "Set the 'repo' attribute in main,edit or list tags to define the repository " +
                    "to use in the data list."));
        }
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
     * @param viewNode
     */
    public static void addDefaultRepoNode(ConfigNode viewNode) {
        String dbFile = viewNode.getAttribute("dbFile");
        String tableName = viewNode.getAttribute("tableName");

        if (StringUtils.isNotBlank(dbFile) && StringUtils.isNotBlank(tableName)) {
            ConfigNode repoNode = new ConfigNode("repo");
            repoNode.setAttribute("dbFile", dbFile);
            repoNode.setAttribute("tableName", tableName);
            viewNode.getChildren().add(0, repoNode);
        } else if (StringUtils.isNotBlank(dbFile) ^ StringUtils.isNotBlank(tableName)) {
            error(String.format("Incorrect repository definition, both 'dbFile' and 'dbTable' " +
                    "must be set in tag ${tag} id[%s].", viewNode.getId()));
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
        if (expr != null && !expr.isReadonly()) {
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
     * Gets the list of properties defined in the node attribute that are in the repository
     *
     * @param repo
     * @param node
     * @return A list with all existing properties
     */
    public static String[] getEffectiveAttributeProperties(Repository repo, ConfigNode node) {
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
                String value = child.getAttribute("value");
                if (value != null) {
                    if (value.equals("${entity." + propertyName + "}")) {
                        isIncluded = true;
                        break;
                    }
                } else if (child.getId().equals(propertyName)) {
                    isIncluded = true;
                    break;
                }
            }
        }

        return isIncluded;
    }

    /**
     * Checks if a node already has a child for a property name
     *
     * @param validatorType
     * @param root
     * @return
     */
    public static boolean isValidatorIncluded(String validatorType, ConfigNode root) {
        boolean isIncluded = false;

        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode child : children) {
                if (child.getName().equals("validator")
                        && child.getAttribute("type").equals(validatorType)) {
                    isIncluded = true;
                    break;
                }
            }
        }

        return isIncluded;
    }

    /**
     * Finds a child node of the given Class
     *
     * @param root
     * @param clazz
     * @return
     */
    public static <T> ConfigNode<T> findNodeByClass(ConfigNode root, Class<T> clazz) {
        ConfigNode<T> node = null;

        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode child : children) {
                if (clazz.isInstance(child)) {
                    node = child;
                    break;
                }
            }
        }
        return node;
    }

    /**
     * Finds a child node of the given tagName
     *
     * @param root
     * @param tagName
     * @return
     */
    public static <T> ConfigNode<T> findNodeByTag(ConfigNode root, String tagName) {
        ConfigNode<T> node = null;

        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode child : children) {
                if (child.getName().equalsIgnoreCase(tagName)) {
                    node = child;
                    break;
                }
            }
        }
        return node;
    }


    /**
     * Find all nested elements from the given one that has the specified tagName
     *
     * @param root
     * @param tagName
     * @return
     */
    public static List<ConfigNode> findChildrenByTag(ConfigNode root, String tagName) {
        List<ConfigNode> lst = new ArrayList<>();
        _findByTagName(root, tagName, lst);
        return lst;
    }


    private static void _findByTagName(ConfigNode root, String tagName, List<ConfigNode> output) {
        if (root.getName().equalsIgnoreCase(tagName)) {
            output.add(root);
        }
        if (!root.hasChildren()) {
            return;
        } else {
            List<ConfigNode> children = root.getChildren();
            for (ConfigNode kid : children) {
                _findByTagName(kid, tagName, output);
            }
            return;
        }
    }

    /**
     * Creates a ConfigNode for the attribute
     *
     * @return
     */
    public static ConfigNode createNodeFromAttribute(Attribute attribute, String value, String tag) {
        ConfigNode node = new ConfigNode(tag);
        node.setId(attribute.name);
        node.setAttribute("type", attribute.type.toString());
        node.setAttribute("name", attribute.name);

        ComponentBuilder<UIComponent> builder = ComponentBuilderFactory.getInstance().getBuilder(tag);

        UIComponent component = builder.build(node);
        node.setElement(component);
        node.setAttribute("value", value, TypeUtils.getType(attribute.type));


        return node;
    }


    /**
     * Finds first parent element that supports "repo" attribute
     *
     * @param node
     * @return
     */
    public static ConfigNode findParentRepo(ConfigNode node) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                String tagName = parent.getName().toLowerCase();
                if (TagDef.supportsAttribute(tagName, "repo") &&
                        !tagName.equals("edit") &&
                        !tagName.equals("list")) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
        }
    }

    /**
     * Finds current form controller
     *
     * @param node
     * @return
     */
    public static ConfigNode findParentController(ConfigNode node) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                if (parent.getName().toLowerCase().equals("list")
                        || parent.getName().toLowerCase().equals("edit")) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
        }
    }

    public static UIParam[] getParams(List<ConfigNode> paramNodes) {
        UIParam[] params = new UIParam[paramNodes.size()];
        for (int i = 0; i < paramNodes.size(); i++) {
            UIParam uiParam = new UIParam();
            ConfigNode paramNode = paramNodes.get(i);
            if (paramNode.hasAttribute("name")) {
                uiParam.setName(paramNode.getAttribute("name"));
            }
            if (paramNode.hasAttribute("value")) {
                ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
                uiParam.setValue(exprFactory.create(paramNodes.get(i).getAttribute("value")));
            }
            params[i] = uiParam;
        }
        return params;
    }

    /**
     * If current edit of list view element doesn't have a view node, create one copying all the
     * controller tag attributes (<list/> and <edit/>)  and nested all elements.
     *
     * @return
     */
    public static ConfigNode createDefaultView(ConfigNode root) {
        ConfigNode viewNode = ConfigNodeHelper.getFirstChildrenByTag(root, "view");
        if (viewNode != null) {
            // it already has a form
            return viewNode;
        }
        viewNode = ConfigNode.clone(root);
        viewNode.setName("view");
        viewNode.setId("view" + root.getId());
        viewNode.setChildren(root.getChildren());
        root.setChildren(Arrays.asList(viewNode));
        return viewNode;
    }
}
