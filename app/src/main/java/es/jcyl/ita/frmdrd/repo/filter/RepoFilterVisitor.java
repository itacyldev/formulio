package es.jcyl.ita.frmdrd.repo.filter;
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


import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Expression;
import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.config.elements.RepoFilter;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Creates a Criteria based on repofilter node descendants
 */
public class RepoFilterVisitor {
    private static final FilterElement FILTER_ELEMENT = new FilterElement();

    public Expression visit(ConfigNode<RepoFilter> node, List<String> mandatoryFields) {
        return element().accept(node, mandatoryFields);
    }

    public interface Element<T extends Expression> {
        Expression accept(T e, Context context);
    }

    public static class FilterElement<T extends Expression> {

        public Expression accept(ConfigNode node, List<String> mandatoryFields) {
            List<Expression> kids = new ArrayList<>();

            for (ConfigNode childNode : ((List<ConfigNode>) node.getChildren())) {
                Expression kidExpr = element().accept(childNode, mandatoryFields);
                if (kidExpr != null) {
                    kids.add(kidExpr);
                }
            }
            if (kids.size() == 0) {
                Operator operator = Operator.valueOf(node.getName().toUpperCase());
                String property = node.getAttribute("property");
                String value = node.getAttribute("value");

                Condition condition = null;

                if (value.startsWith("$")) {
                    // Jexl Expression
                    condition = new ConditionBinding(property, operator, value);
                    ValueBindingExpression valueExpression = ValueExpressionFactory.getInstance().create(value);
                    ((ConditionBinding) condition).setBindingExpression(valueExpression);
                } else {
                    condition = new Condition(property, operator, value);
                }

                // add mandatory fields
                if (node.getAttributes().containsKey("mandatory")) {
                    String isMandatory = node.getAttribute("mandatory");
                    if ("true".equalsIgnoreCase(isMandatory)) {
                        mandatoryFields.add(property);
                    }
                }

                return condition;
            } else {
                Criteria.CriteriaType criteriaType = null;
                try {
                    criteriaType = Criteria.CriteriaType.valueOf(node.getName().toUpperCase());
                } catch (Exception e) {
                    //default value
                    criteriaType = Criteria.CriteriaType.AND;
                }

                return new Criteria(criteriaType, kids.toArray(new Expression[kids.size()]));
            }
        }
    }

    private static FilterElement element() {
        return FILTER_ELEMENT;
    }

}
