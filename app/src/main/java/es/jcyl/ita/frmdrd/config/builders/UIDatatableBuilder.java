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
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIDatatableBuilder extends AbstractUIComponentBuilder<UIDatatable> {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();


    protected UIDatatableBuilder(String tagName) {
        super(tagName, UIDatatable.class);
    }


    @Override
    protected void doWithAttribute(UIDatatable element, String name, String value) {

    }

    @Override
    protected void doConfigure(UIDatatable element, ConfigNode<UIDatatable> node) {

    }


    @Override
    public void processChildren(ConfigNode<UIDatatable> node) {
//        UIComponent[] uiComponents = getUIChildren(node);
//        node.getElement().setChildren(uiComponents);

        UIBuilderHelper.setUpRepo(node, true);
        setUpColumns(node);
    }

    /**
     * If repository attribute is not set, use resolver to get a parent reference
     *
     * @param node
     */
    private void setUpRepo(ConfigNode<UIDatatable> node) {
        UIDatatable table = node.getElement();
        // check if there's a nested repository definition
        boolean hasAttRepo = node.hasAttribute("repo");

        List<ConfigNode> lstRepos = ConfigNodeHelper.getChildrenByTag(node, "repo");
        if (CollectionUtils.isNotEmpty(lstRepos) && hasAttRepo) {
            throw new ConfigurationException(error("The element ${tag} has the attribute 'repo' set" +
                    "But it also has a nested repository defined, just one of the options can be used."));
        } else if (lstRepos.size() > 1) {
            throw new ConfigurationException(error("Just one nested repo can be defined " +
                    "in ${tag} component."));
        } else if (lstRepos.size() == 1) {
            // get nested repository definition from nested node
            Repository repo = (Repository) lstRepos.get(0).getElement();
            table.setRepo(repo);
        }
        // if not defined, try to get repo from parent nodes
        if (table.getRepo() == null) {
            RepositoryAttributeResolver repoResolver = (RepositoryAttributeResolver) getAttributeResolver("repo");
            Repository repo = repoResolver.findParentRepo(node);
            table.setRepo(repo);
        }
    }

    private void setUpColumns(ConfigNode<UIDatatable> node) {
        List<UIColumn> columns = new ArrayList<>();
        UIDatatable dataTable = node.getElement();

        // get nested defined columns
        List<ConfigNode> colNodes = ConfigNodeHelper.getNestedByTag(node, "column");

        for (ConfigNode n : colNodes) {
            UIColumn col = (UIColumn) n.getElement();
            columns.add(col);
        }

        // check if 'properties' attributes is defined to add more columns
        String propertySelector = node.getAttribute("properties");
        String[] propertyFilter;
        List<UIColumn> colsToAdd = null;
        if (StringUtils.isBlank(propertySelector)) {
            if (columns.size() == 0) {
                // no property is selected and no nested column elements, by default add all properties
                colsToAdd = createDefaultColumns(dataTable.getRepo(), new String[0]);
            }
        } else {
            if (propertySelector.equals("*") || propertySelector.equals("all")) {
                // use all repo properties
                propertyFilter = new String[0];
            } else {
                // comma-separated list of property names
                propertyFilter = StringUtils.split(propertySelector, ",");
            }
            // create columns with property selection
            colsToAdd = createDefaultColumns(dataTable.getRepo(), propertyFilter);
        }
        if (colsToAdd != null) {
            columns.addAll(colsToAdd);
        }

        // set columns in datatable
        dataTable.setColumns(columns.toArray(new UIColumn[columns.size()]));

    }

    public UIDatatable createDataTableFromRepo(Repository repo) {
        // select all entity properties
        EntityMeta meta = repo.getMeta();
        String[] fieldFilter = meta.getPropertyNames();
        return createDataTableFromRepo(repo, fieldFilter);
    }

    /**
     * Creates a datatable using repo meta using just the given fields
     *
     * @param repo
     * @param properties
     * @return
     */
    public UIDatatable createDataTableFromRepo(Repository repo, String[] properties) {
        UIDatatable datatable = new UIDatatable();
        datatable.setRepo(repo);
        List<UIColumn> lstCols = createDefaultColumns(repo, properties);
        datatable.setColumns(lstCols.toArray(new UIColumn[lstCols.size()]));

        return datatable;
    }

    private List<UIColumn> createDefaultColumns(Repository repo, String[] propertyNames) {
        List<UIColumn> lstCols = new ArrayList<>();

        PropertyType[] properties;
        properties = UIBuilderHelper.getPropertiesFromRepo(repo, propertyNames);
        for (PropertyType property : properties) {
            UIColumn col = createColumn(property);
            lstCols.add(col);
        }
        return lstCols;
    }

    private UIColumn createColumn(PropertyType property) {
        UIColumn col = new UIColumn();
        col.setId(property.getName());
        col.setHeaderText(property.getName());
        col.setValueExpression(exprFactory.create("${entity." + property.getName() + "}"));
        col.setFiltering(true);
        return col;
    }

}
