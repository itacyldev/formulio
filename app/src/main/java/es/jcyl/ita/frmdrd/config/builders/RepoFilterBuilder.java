package es.jcyl.ita.frmdrd.config.builders;
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

import es.jcyl.ita.crtrepo.query.BaseFilter;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.elements.RepoFilter;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.repo.filter.RepoFilterVisitor;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class RepoFilterBuilder extends AbstractComponentBuilder<RepoFilter> {
    public RepoFilterBuilder() {
        super("repofilter", RepoFilter.class);
    }

    @Override
    protected void doWithAttribute(RepoFilter element, String name, String value) {

    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoFilter> node) {

    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoFilter> node) {
        RepoFilter repofilter = node.getElement();

        RepoFilterVisitor visitor = new RepoFilterVisitor();
        List<String> mandatoryFields = new ArrayList<>();
        Criteria criteria = (Criteria) visitor.visit(node, mandatoryFields);

        Filter filter = new BaseFilter();
        filter.setExpression(criteria);
        repofilter.setFilter(filter);

        if (mandatoryFields.size() > 0) {
            repofilter.setMandatoryFields(mandatoryFields.toArray(new String[mandatoryFields.size()]));
        }

        setupParentRepoFilter(node);
    }


    /**
     * Set filter and mandatory fields on repofilter's parent component
     *
     * @param node
     */
    private void setupParentRepoFilter(ConfigNode<RepoFilter> node) {
        RepoFilter repoFilter = node.getElement();

        ConfigNode<FilterableComponent> parent = node.getParent();
        FilterableComponent parentComponent = parent.getElement();

        parentComponent.setFilter(repoFilter.getFilter());
        parentComponent.setMandatoryFilters(repoFilter.getMandatoryFields());
    }
}
