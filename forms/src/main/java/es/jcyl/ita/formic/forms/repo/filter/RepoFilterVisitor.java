package es.jcyl.ita.formic.forms.repo.filter;
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

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.elements.RepoFilter;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Expression;
import es.jcyl.ita.formic.repo.query.Operator;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

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
                Operator operator = null;
                try {
                    operator = Operator.getValue(node.getName().toUpperCase());
                } catch (Exception e) {
                    throw new ConfigurationException(error(
                            String.format("Invalid operator expression found in repoFilter declaration: [%s], " +
                                    "valid values are: [%s]", node.getName().toUpperCase(), Operator.values())));

                }
                String property = node.getAttribute("property");
                String value = node.getAttribute("value");
                boolean isMandatory = false;
                if ("true".equalsIgnoreCase(node.getAttribute("mandatory"))) {
                    isMandatory = true;
                }

                Condition condition = null;

                if (value.startsWith("$")) {
                    // Jexl Expression
                    condition = new ConditionBinding(property, operator, value);
                    ValueBindingExpression valueExpression = ValueExpressionFactory.getInstance().create(value);
                    ((ConditionBinding) condition).setBindingExpression(valueExpression);
                    if (isMandatory) {
                        for (String variable : valueExpression.getDependingVariables()) {
                            mandatoryFields.add(variable);
                        }
                    }
                } else {
                    condition = new Condition(property, operator, value);
                    if (isMandatory) {
                        mandatoryFields.add(value);
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
