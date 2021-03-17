package es.jcyl.ita.formic.forms.controllers;
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

import es.jcyl.ita.formic.forms.components.EntitySelector;
import es.jcyl.ita.formic.repo.query.Filter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Implements entity edition actions using a edit-form.
 */
public class FormListController extends FormController {
    // form used to refer to the repository used to count entities
    private EntitySelector entitySelector;

    public FormListController(String id, String name) {
        super(id, name);
    }

    /****************************/
    /**  >>> Form List methods **/
    /****************************/

    /**
     * Number of entities obtained with the main repository. Used in the form list to give
     * information of the number of "form instances (table records)" registered
     *
     * @return
     */
    public long count() {
        return this.getEntitySelector().getRepo().count(this.getEntitySelector().getFilter());
    }

    /****************************/
    /** GETTER/SETTERS **/
    /****************************/

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public EntitySelector getEntitySelector() {
        return entitySelector;
    }

    public void setEntitySelector(EntitySelector entitySelector) {
        this.entitySelector = entitySelector;
    }

}
