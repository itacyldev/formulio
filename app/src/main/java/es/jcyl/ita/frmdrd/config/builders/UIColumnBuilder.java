package es.jcyl.ita.frmdrd.config.builders;
/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIColumnBuilder extends BaseUIComponentBuilder<UIColumn> {

    protected UIColumnBuilder(String tagName) {
        super(tagName, UIColumn.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIColumn> node) {
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIColumn> node) {

    }

    private void setUpColumns(ConfigNode<UIColumn> node) {
        List<UIColumn> columns = new ArrayList<>();
        UIColumn dataTable = node.getElement();

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
        }
        if (colsToAdd != null) {
            columns.addAll(colsToAdd);
        }

        // set columns in datatable

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

        col.setValueExpression(ValueExpressionFactory.getInstance().create("${entity." + property.getName() + "}"));
        col.setFiltering(true);
        return col;
    }

}
