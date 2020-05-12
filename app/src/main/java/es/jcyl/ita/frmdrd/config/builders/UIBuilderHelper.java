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

import org.mini2Dx.beanutils.BeanUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Helper class that sets UIComponent common attributes
 */
public class UIBuilderHelper {

    public static void setUpRepo(ConfigNode node, boolean mandatory) {
        Object currentRepo = getElementValue(node.getElement(), "repo");
        boolean hasAttRepo = (currentRepo != null);
        if (hasAttRepo) {
            // it should be already setup
            return;
        }

        Object element = node.getElement();
        Repository repo = null;

        List<ConfigNode> lstRepos = ConfigNodeHelper.getChildrenByTag(node, "repo");
        if (CollectionUtils.isNotEmpty(lstRepos) && hasAttRepo) {
            throw new ConfigurationException(error("The element ${tag} has the attribute 'repo' set" +
                    "But it also has a nested repository defined, just one of the options can be used."));
        } else if (lstRepos.size() > 1) {
            throw new ConfigurationException(error("Just one nested repo can be defined " +
                    "in ${tag} component."));
        } else if (lstRepos.size() == 1) {
            // get nested repository definition from nested node
            repo = (Repository) lstRepos.get(0).getElement();
        }
        // if not defined, try to get repo from parent nodes
        if (repo == null) {
            repo = RepositoryAttributeResolver.findParentRepo(node);
        }
        if (repo == null && mandatory) {
            throw new ConfigurationException(error("No repository found for <${tag}/> with id =" + node.getId()));
        }
        setElementValue(element, "repo", repo);
    }

    private static Object getElementValue(Object element, String property) {
        try {
            return BeanUtils.getProperty(element, property);
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Error while trying to get " +
                    "attribute '%s' in object [%s] ", property, element.getClass().getName())));
        }
    }

    private static void setElementValue(Object element, String property, Object value) {
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
}
