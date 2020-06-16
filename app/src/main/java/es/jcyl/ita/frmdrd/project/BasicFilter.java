package es.jcyl.ita.frmdrd.project;
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

import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Expression;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Sort;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class BasicFilter implements Filter {

    private Criteria criteria;

    @Override
    public void setOffset(int i) {

    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void setPageSize(int i) {

    }

    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public Sort[] getSorting() {
        return new Sort[0];
    }

    @Override
    public void setSorting(Sort[] sorts) {

    }

    @Override
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public void setCriteria(Expression expression) {

    }

    @Override
    public Criteria getCriteria() {
        return criteria;
    }
}
