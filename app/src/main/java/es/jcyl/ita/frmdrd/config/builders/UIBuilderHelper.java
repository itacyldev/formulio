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

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.BeanUtils;
import org.mini2Dx.beanutils.BeanUtilsBean;
import org.mini2Dx.beanutils.PropertyUtilsBean;

import java.util.Arrays;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

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
     * @param node
     * @param attName
     */
    public static void inheritAttribute(ConfigNode node, String attName) {
        if(!node.hasAttribute(attName)){
            String ascendantAttValue = ConfigNodeHelper.findAscendantAtt(node, attName);
            if(ascendantAttValue !=null){
                node.setAttribute(attName, ascendantAttValue);
            }
        }
    }
}
