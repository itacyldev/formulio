package es.jcyl.ita.formic.forms.components.datalist;
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

import java.util.List;

import es.jcyl.ita.formic.forms.components.EntityListProvider;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIDatalist extends UIComponent implements FilterableComponent, EntityListProvider {

    private Repository repo;

    private int numItems;

    private List<Entity> entities;

    public UIDatalist() {
        setRendererType("datalist");
    }

    public Repository getRepo() {
        return repo;
    }

    @Override
    public void setFilter(Filter filter) {

    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public String[] getMandatoryFilters() {
        return new String[0];
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFields) {

    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    @Override
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }
}
