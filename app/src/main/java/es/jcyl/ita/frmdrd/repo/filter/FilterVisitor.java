package es.jcyl.ita.frmdrd.repo.filter;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Expression;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.elements.RepoFilter;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Evaluates the criteria conditions using context information.
 */
public class FilterVisitor {
    private static final FilterElement FILTER_ELEMENT = new FilterElement();

    public void visit(ConfigNode<RepoFilter> node) {
        element().accept(node);
    }

    public interface Element<T extends Expression> {
        Expression accept(T e, Context context);
    }

    public static class FilterElement<T extends Expression> {
        public Expression accept(ConfigNode<RepoFilter> node) {
            List<Expression> kids = new ArrayList<>();

            for (ConfigNode childNode : node.getChildren()) {
                Expression kidExpr = element().accept(childNode);
                if (kidExpr != null) {
                    kids.add(kidExpr);
                }
            }
            if (kids.size() == 0) {
                // if all the children are null, prune this branch
                return null;
            } else {

                Map attributes = node.getAttributes();

                return null;
                //return new Criteria(e.getType(), kids.toArray(new Expression[kids.size()]));
            }
        }
    }

    private static FilterElement element() {
       return FILTER_ELEMENT;
    }

}
