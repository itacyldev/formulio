package es.jcyl.ita.frmdrd.config.builders;
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

import org.mini2Dx.collections.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Builder class to create UIForm instances from an Entity metadata information. It maps each
 * metadata property to the most most suitable field component for each table column.
 */
public class FormListControllerBuilder extends AbstractComponentBuilder<FormListController> {

    private static final Set<String> ACTION_SET = new HashSet<String>(Arrays.asList("new", "update", "cancel", "delete", "nav"));

    private static RepositoryAttributeResolver repoResolver = new RepositoryAttributeResolver();

    public FormListControllerBuilder(String tagName) {
        super(tagName, FormListController.class);
    }

    @Override
    protected void doWithAttribute(FormListController element, String name, String value) {
    }


    @Override
    protected void doConfigure(FormListController ctl, ConfigNode node) {
        Repository repo = repoResolver.resolve(node);
        ctl.setRepo(repo);

        // find nested filter if exists
        List<ConfigNode> repoFilters = getChildren(node, "repoFilter");
        if (CollectionUtils.isNotEmpty(repoFilters)) {
            if (repoFilters.size() > 1)
                error(String.format("Just one nested repoFilter element can be defined in 'list', found: []", repoFilters.size()));
            else if (repoFilters.size() == 1) {
                ctl.setFilter((Filter) repoFilters.get(0).getElement());
            }
        }
        // find entitySelector
        UIView listView = new UIView(ctl.getId() + ">view");
        ctl.setView(listView);
    }

    @Override
    public void processChildren(ConfigNode<FormListController> node) {
        // add nested ui elements
        UIComponent[] uiComponents = getUIChildren(node);
        node.getElement().getView().setChildren(uiComponents);

        setUpActions(node);
    }

    /**
     * Searchs for actions in nested configuration
     *
     * @param node
     */
    private void setUpActions(ConfigNode<FormListController> node) {
        List<ConfigNode> actions = getNestedByTag(node, ACTION_SET);
        FCAction[] lstActions = new FCAction[actions.size()];

        for (int i = 0; i < actions.size(); i++) {
            lstActions[i] = (FCAction) actions.get(i).getElement();
            lstActions[i].setType(actions.get(i).getName());
        }
        node.getElement().setActions(lstActions);
    }

    @Override
    protected FormListController instantiate() {
        return new FormListController("", "");
    }

}
