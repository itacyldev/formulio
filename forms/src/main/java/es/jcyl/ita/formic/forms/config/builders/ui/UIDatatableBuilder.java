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

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIDatatableBuilder extends BaseUIComponentBuilder<UIDatatable> {

    private static final Set<String> NAV_ACTIONS = new HashSet<String>(Arrays.asList("add", "update"));

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    protected UIDatatableBuilder(String tagName) {
        super(tagName, UIDatatable.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIDatatable> node) {
//        UIBuilderHelper.inheritAttribute(node, "repo");
        UIBuilderHelper.setUpRepo(node, true);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIDatatable> node) {
        setUpColumns(node);
        setUpNumVisibleRows(node);
        setUpRoute(node);
    }

    private void setUpRoute(ConfigNode<UIDatatable> node) {
        if (node.hasAttribute("route")) {
            return; // already defined
        }
        // get add action from list controller to define default route
        ConfigNode listNode = ConfigNodeHelper.getAscendantByTag(node, "list");
        if (listNode == null) {
            // the table is not nested in the listController, doesn't have to be automatically set
            return;
        }
        // find add or update action to configure the destination for when user click on table element
        List<ConfigNode> addActions = ConfigNodeHelper.getDescendantByTag(listNode, NAV_ACTIONS);
        if (CollectionUtils.isEmpty(addActions)) {
            throw new ConfigurationException(DevConsole.error("Error trying to create default datatable for " +
                    "<list/> in file '${file}'. \nCan't create navigation from table to form if there's " +
                    "no 'add' action. use 'route' attribute on <datatable/> instead to set the id " +
                    "of the destination form."));
        } else {
            ConfigNode addAction = addActions.get(0); // TODO: xml validation to make sure there's just one
            node.getElement().setRoute(addAction.getAttribute("route"));
        }
    }

    private void setUpNumVisibleRows(ConfigNode<UIDatatable> node) {
        int numVisibleRows = 1; // Number of default visible rows
        if (node.hasAttribute("numVisibleRows")) {
            numVisibleRows = Integer.parseInt(node.getAttribute("numVisibleRows"));
        }
        if (node.getParent().getElement() instanceof FormListController) {
            numVisibleRows = -1; // Fill container with rows
        }
        UIDatatable dataTable = node.getElement();
        dataTable.setNumVisibleRows(numVisibleRows);
    }

    private void setUpColumns(ConfigNode<UIDatatable> node) {
        List<UIColumn> columns = new ArrayList<>();
        UIDatatable dataTable = node.getElement();

        // get nested defined columns
        List<ConfigNode> colNodes = ConfigNodeHelper.getDescendantByTag(node, "column");

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
                propertyFilter = StringUtils.split(propertySelector.replace(" ", ""), ",");
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
