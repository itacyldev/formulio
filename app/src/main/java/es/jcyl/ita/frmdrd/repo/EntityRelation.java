package es.jcyl.ita.frmdrd.repo;
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
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.EntityHolder;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * The form's main entity can have additional related entities.
 * This class stores the information needed to retrieve a related entity and the restrictions for
 * its modifications.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityRelation {

    /**
     * Repo and filter used to retrieve the entity.
     */
    private Repository repo;
    private Filter filter;
    private EntityHolder entityHolder;
    /**
     * Expression used to obtain the id of the entity to retrieve.
     */
    private ValueBindingExpression entityPropertyExpr;
    /**
     * The of the transient property created in the main entity to store the related entity.
      */
    private String name;
    /**
     * Modification restrictions
     */
    private boolean insertable = true;
    private boolean updatable = true;
    private boolean deletable = true;


    // TODO: right now we just need a one2one relation

    public EntityRelation(Repository repo, String name, ValueBindingExpression expr){
        this.repo = repo;
        this.name = name;
        this.entityPropertyExpr = expr;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public ValueBindingExpression getEntityPropertyExpr() {
        return entityPropertyExpr;
    }

    public void setEntityPropertyExpr(ValueBindingExpression entityPropertyExpr) {
        this.entityPropertyExpr = entityPropertyExpr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public EntityHolder getEntityHolder() {
        return entityHolder;
    }

    public void setEntityHolder(EntityHolder entityHolder) {
        this.entityHolder = entityHolder;
    }
}
