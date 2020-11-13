package es.jcyl.ita.formic.repo;
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

import java.util.List;

import es.jcyl.ita.formic.repo.query.Filter;

/**
 * The form's main entity can have additional related entities.
 * This class stores the information needed to retrieve a related entity and the restrictions for
 * its modifications.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityMapping {

    /**
     * Repo and filter used to retrieve the entity.
     */
    private Repository repo;
    private Filter filter;
    /**
     * Property or Expression used to obtain the id of the entity to retrieve.
     */
    private String fk;
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
    private boolean isFkExpression = false;
    private List<CalculatedProperty> calcProps;

//    private List<CalculatedProperty> calcProps;

    // TODO: right now we just need a one2one relation
    public EntityMapping(Repository repo, String fk, String propertyName) {
        this.repo = repo;
        this.name = propertyName;
        this.fk = fk;
        if (fk.contains("$")) {
            this.isFkExpression = true;
        }
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

    public List<CalculatedProperty> getCalcProps() {
        return this.calcProps;
    }

    public boolean isFkExpression() {
        return isFkExpression;
    }

    public void setCalcProps(List<CalculatedProperty> calcProps) {
        this.calcProps = calcProps;
    }

    public String getFk() {
        return fk;
    }

    public boolean hasCalcProps() {
        return this.calcProps != null && this.calcProps.size() > 0;
    }
}
