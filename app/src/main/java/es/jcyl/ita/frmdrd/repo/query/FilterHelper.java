package es.jcyl.ita.frmdrd.repo.query;
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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Reusable functions to treat filters from dynamic components.
 */
public class FilterHelper {

    private static final CriteriaVisitor criteriaVisitor = new CriteriaVisitor();

    public static Filter createInstance(Repository repo) {
        Filter f;
        try {
            f = (Filter) repo.getFilterClass().newInstance();
        } catch (Exception e) {
            throw new ViewConfigException("An error occurred while trying to instantiate the filter " +
                    "class: " + repo.getFilterClass().getName());
        }
        return f;
    }

    /**
     * Uses the filter definition param to create a new versi0n of the filter structure but filled
     * with the values retrieved from the context using the filter ValueBindingExpressions.
     * It evaluates filters expressions from the context using a criteria visitor.
     *
     * @param context:    context used to evaluate filter expressions.
     * @param definition: filter that defines the where structure of the filter
     * @param output:     the object used to set the values extracted from the context
     */
    public static void evaluateFilter(Context context, Filter definition, Filter output) {
        // evaluate filter conditions
        Criteria effectiveCriteria = criteriaVisitor.visit(definition.getCriteria(), context);
        output.setCriteria(effectiveCriteria);
        output.setSorting(definition.getSorting());
    }

}
