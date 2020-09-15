package es.jcyl.ita.frmdrd.ui.components.image;
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
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Image component
 */
public class UIImage extends UIInputComponent implements FilterableComponent {

    private static final String IMAGE = "image";
    /**
     * filterable component
     **/
    private Repository repo;
    private Filter filter;
    private String[] mandatoryFilters;
    private Integer width;
    private Integer height;


    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String getRendererType() {
        return IMAGE;
    }

    /**
     * Returns the key that references the viewvalueConverter set in the component. By default,
     * the component uses a ImageViewUrlConverter.
     *
     * @return
     */
    @Override
    public String getValueConverter() {
        String converter = super.getValueConverter();
        return (converter == null) ? "urlImage" : converter;
    }

    /******* Filterable component interface **/
    @Override
    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    @Override
    public Repository getRepo() {
        return this.repo;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return this.mandatoryFilters;
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFields) {
        this.mandatoryFilters = mandatoryFilters;
    }


    public boolean isBound() {
        return (getValueExpression() == null) ? false : !getValueExpression().isReadOnly();
    }


}
