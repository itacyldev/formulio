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

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIDatatableBuilder extends BaseUIComponentBuilder<UIDatatable> {

    private static final Set<String> NAV_ACTIONS = new HashSet<String>(Arrays.asList("add", "update"));

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    public UIDatatableBuilder(String tagName) {
        super(tagName, UIDatatable.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIDatatable> node) {
//        BuilderHelper.inheritAttribute(node, "repo");
        BuilderHelper.setUpRepo(node, true);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIDatatable> node) {
        setUpColumns(node);
        setUpNumVisibleRows(node);
        setUpRoute(node);

        UIDatatable element = node.getElement();

        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        UIAction uiAction = element.getAction();
        // TODO: FORMIC-229 Terminar refactorización de acciones
        if (uiAction == null) { // default action
            uiAction = new UIAction();
            uiAction.setType("nav");
            uiAction.setRoute(element.getRoute());
            element.setAction(uiAction);
        }
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = getParams(paramNodes);
            uiAction.setParams(params);
        }
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
                    "no 'add' action. Use 'route' attribute on <datatable/> instead to set the id " +
                    "of the destination form."));
        } else {
            ConfigNode addAction = addActions.get(0); // TODO: xml validation to make sure there's just one
            node.getElement().setRoute(addAction.getAttribute("route"));
        }
    }

    private void setUpNumVisibleRows(ConfigNode<UIDatatable> node) {
        if (!node.hasAttribute("numVisibleRows") && node.getParent().getElement() instanceof FormListController) {
            // Fill container with rows
            UIDatatable dataTable = node.getElement();
            dataTable.setNumVisibleRows(-1);
        }
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
        datatable.setId(String.valueOf(RandomUtils.nextInt()));
        datatable.setRepo(repo);
        List<UIColumn> lstCols = createDefaultColumns(repo, properties);
        datatable.setColumns(lstCols.toArray(new UIColumn[lstCols.size()]));

        return datatable;
    }

    private List<UIColumn> createDefaultColumns(Repository repo, String[] propertyNames) {
        List<UIColumn> lstCols = new ArrayList<>();

        PropertyType[] properties;
        properties = BuilderHelper.getPropertiesFromRepo(repo, propertyNames);
        int i = 0;
        UIColumn col;
        for (PropertyType property : properties) {
            if (propertyNames.length > 0) {
                col = createColumn(propertyNames[i], property);
            } else {
                col = createColumn(property.getName(), property);
            }
            lstCols.add(col);
            i++;
        }
        return lstCols;
    }

    private UIColumn createColumn(String propertyName, PropertyType property) {
        UIColumn col = new UIColumn();
        col.setId(property.getName());
        col.setHeaderText(property.getName());
        col.setValueExpression(exprFactory.create("${entity." + propertyName + "}"));
        col.setFiltering(true);
        return col;
    }

}
