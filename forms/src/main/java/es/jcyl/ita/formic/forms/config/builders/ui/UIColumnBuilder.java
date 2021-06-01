package es.jcyl.ita.formic.forms.config.builders.ui;
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

import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.column.UIColumnFilter;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.query.Operator;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIColumnBuilder extends BaseUIComponentBuilder<UIColumn> {

    public UIColumnBuilder(String tagName) {
        super(tagName, UIColumn.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIColumn> node) {
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIColumn> node) {
        if (node.hasChildren()) {
            for (ConfigNode child : node.getChildren()) {
                String name = child.getName();
                UIColumnFilter filter = new UIColumnFilter();
                if (node.getElement().getHeaderFilter() != null){
                    filter = node.getElement().getHeaderFilter();
                }
                if (name.equals("filter")) {
                    addHeaderFilter(child, filter);

                } else if (name.equals("order")) {
                    String property = child.getAttribute("property");
                    filter.setOrderProperty(property);
                }
                node.getElement().setHeaderFilter(filter);
            }
        }
    }

    /**
     * Adds values for the column filter
     *
     * @param filterNode
     */
    private void addHeaderFilter(ConfigNode filterNode, UIColumnFilter filter) {
        String property = filterNode.getAttribute("property");

        String matching = filterNode.getAttribute("matching");
        Operator op = Operator.valueOf(matching.toUpperCase());

        String valueExpressionStr = filterNode.getAttribute("valueExpression");
        ValueExpressionFactory fact = ValueExpressionFactory.getInstance();
        ValueBindingExpression valueExpression = fact.create(valueExpressionStr);

        filter.setFilterProperty(property);
        filter.setFilterValueExpression(valueExpression);
        filter.setMatchingOperator(op);
    }
}

