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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.builders.AbstractUIComponentBuilder;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIDatatableBuilder extends AbstractUIComponentBuilder<UIDatatable> {

    public UIDatatableBuilder(String tagName) {
        super(tagName, UIDatatable.class);
    }


    @Override
    protected void doWithAttribute(UIDatatable element, String name, String value) {

    }

    @Override
    protected void doConfigure(UIDatatable element, ConfigNode<UIDatatable> node) {
        RepositoryAttributeResolver repoResolver = this.getFactory().getRepoAttResolver();
        Repository repo = repoResolver.resolve(node);
        element.setRepo(repo);
    }


    @Override
    public void processChildren(ConfigNode<UIDatatable> node) {
//        UIComponent[] uiComponents = getUIChildren(node);
//        node.getElement().setChildren(uiComponents);

        setUpColumns(node);

    }

    private void setUpColumns(ConfigNode<UIDatatable> node) {
        List<UIColumn> columns = new ArrayList<>();
        UIDatatable dataTable = node.getElement();

        // get nested defined columns
        List<ConfigNode> colNodes = getNestedByTag(node, "column");

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
                colsToAdd = createColumns(dataTable.getRepo(), new String[0]);
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
            colsToAdd = createColumns(dataTable.getRepo(), propertyFilter);
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
        List<UIColumn> lstCols = createColumns(repo, properties);
        datatable.setColumns(lstCols.toArray(new UIColumn[lstCols.size()]));

        return datatable;
    }

    private List<UIColumn> createColumns(Repository repo, String[] properties) {
        EntityMeta meta = repo.getMeta();
        List<UIColumn> lstCols = new ArrayList<UIColumn>();

        ValueExpressionFactory exprFactory = getFactory().getExpressionFactory();

        for (String propName : properties) {
            PropertyType p = meta.getPropertyByName(propName);
            if (p == null) {
                throw new ConfigurationException(error(String.format("No property found with name [%s] in " +
                        "EntityMeta from repository [%s]", propName, repo.getId())));
            }
            // create columns
            UIColumn col = new UIColumn();
            col.setId(p.getName());
            col.setHeaderText(p.getName());
            col.setValueExpression(exprFactory.create("${entity." + p.getName() + "}"));
            col.setFiltering(true);
            lstCols.add(col);
        }
        return lstCols;
    }

}
