package es.jcyl.ita.formic.forms.config.builders.repo;
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

import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.elements.RepoConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * Creates EntityMeta object from XML configuration
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepoMetaConfigBuilder extends AbstractComponentBuilder<EntityMeta> {

    public RepoMetaConfigBuilder() {
        super("meta", EntityMeta.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<EntityMeta> node) {
        // get current repo source
        Object obj = node.getParent().getElement();
        if (!(obj instanceof RepoConfig)) {
            throw new ConfigurationException(DevConsole.error("The <meta/> element must be nested" +
                    " directly under a <repo/> element. Error found in file ${file}."));
        }
        RepoConfig repoConfig = (RepoConfig) obj;
        Repository repo = getFactory().getRepoFactory().getRepo(repoConfig.getId());
        EntityMeta meta = repo.getMeta();

        PropertyType[] properties;
        if (node.hasAttribute("properties")) {
            properties = filterProperties(meta, node.getAttribute("properties"));
        } else {
            properties = meta.getProperties();
        }
        // get nested property definition
        List<ConfigNode> prpList = BuilderHelper.findChildrenByTag(node, "property");
        DBTableEntitySource source = (DBTableEntitySource) repo.getSource();
        if (prpList != null) {
            // contains final property list
            List<PropertyType> lstProps = Arrays.asList(properties);
            SQLiteMetaModeler modeler = new SQLiteMetaModeler();
            for (ConfigNode propNode : prpList) {
                DBPropertyType dbProperty = modeler.createPropertyFromColumnDef(propNode.getAttribute("columnName"),
                        propNode.getAttribute("type"), false, false, source);
                // if exists remove from list, otherwise add
                addOrUpdate(lstProps, dbProperty);
            }
            // set effective properties to entity-meta
            meta.setProperties(lstProps.toArray(new PropertyType[lstProps.size()]));
        }
    }

    private void addOrUpdate(List<PropertyType> lstProps, DBPropertyType dbProperty) {
        int i = 0;
        for (PropertyType p : lstProps) {
            if (p.name.equalsIgnoreCase(dbProperty.name)) {
                lstProps.add(i, dbProperty);
                return;
            }
            i++;
        }
        // add
        lstProps.add(dbProperty);
    }

    /**
     * Selects the entity properties to be used based on the attribute "properties" of the tag
     * <meta/>.
     *
     * @param meta
     * @param filter : emtpy, * or all, returs all the properties. Otherwise, the attribute is
     *               interpreted as a comma-separated list of property names.
     * @return
     */
    private PropertyType[] filterProperties(EntityMeta meta, String filter) {
        if (StringUtils.isBlank(filter) || filter.equalsIgnoreCase("all")
                || filter.equalsIgnoreCase("*")) {
            return meta.getProperties();
        }
        String[] splits = filter.trim().split(",");
        if (splits.length == 0) {
            throw new ConfigurationException(DevConsole.error("The <meta properties=\"\"/> " +
                    "attribute must contain the property names separated by comma. Found: " + filter
                    + ". Error found in file ${file}."));
        }
        PropertyType[] filtered = new PropertyType[splits.length];
        int i = 0;
        for (String prop : splits) {
            PropertyType property = meta.getPropertyByName(prop);
            if (property == null) {
                throw new ConfigurationException(DevConsole.error(String.format("The property %s " +
                        "doesn't exists" +
                        " in the repository entity. Error found in file ${file}.", prop)));
            }
            filtered[i] = property;
        }
        return filtered;
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<EntityMeta> node) {

    }

    @Override
    protected void doWithAttribute(EntityMeta element, String name, String value) {
    }


}