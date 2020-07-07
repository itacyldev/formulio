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

import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumnFilter;

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
        if (node.hasChildren()) {
            UIColumnFilter filter = createHeaderFilter(node.getChildren().get(0));
            node.getElement().setHeaderFilter(filter);
        }
    }

    /**
     * @param filterNode
     * @return
     */
    private UIColumnFilter createHeaderFilter(ConfigNode filterNode) {
        String property = filterNode.getAttribute("property");

        String matching = filterNode.getAttribute("matching");
        Operator op = Operator.valueOf(matching.toUpperCase());

        String valueExpressionStr = filterNode.getAttribute("valueExpression");
        ValueExpressionFactory fact = ValueExpressionFactory.getInstance();
        ValueBindingExpression valueExpression = fact.create(valueExpressionStr);

        UIColumnFilter filter = new UIColumnFilter();
        filter.setFilterProperty(property);
        filter.setFilterValueExpression(valueExpression);
        filter.setMatchingOperator(op);
        
        return filter;
    }
}
