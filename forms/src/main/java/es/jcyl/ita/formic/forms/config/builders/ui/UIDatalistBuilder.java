package es.jcyl.ita.formic.forms.config.builders.ui;
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

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.card.UICard;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIDatalistBuilder extends BaseUIComponentBuilder<UIDatalist> {

    private static final Set<String> NAV_ACTIONS = new HashSet<String>(Arrays.asList("add", "update"));

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    public UIDatalistBuilder(String tagName) {
        super(tagName, UIDatalist.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIDatalist> node) {
        BuilderHelper.setUpRepo(node, true);
        // Add item node if doesn't exist
        addItemNode(node);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIDatalist> node) {
        super.setupOnSubtreeEnds(node);
        setNumItems(node);
//        setUpRoute(node);
    }

    @Override
    protected Object getDefaultAttributeValue(UIDatalist element, ConfigNode node, String attName) {
        if (AttributeDef.ALLOWS_PARTIAL_RESTORE.name.equals(attName)) {
            return Boolean.TRUE;
        }
        return super.getDefaultAttributeValue(element, node, attName);
    }

    private void setNumItems(ConfigNode<UIDatalist> node) {
        int numItems = 1; // Number of default visible items
        if (node.hasAttribute("numItems")) {
            numItems = Integer.parseInt(node.getAttribute("numItems"));
        }
        if (node.getParent().getElement() instanceof FormListController) {
            numItems = -1; // Fill container with rows
        }
        UIDatalist datalist = node.getElement();
        datalist.setNumItems(numItems);
    }

    public UIDatalist createDatalistFromRepo(Repository repo) {
        // select all entity properties
        EntityMeta meta = repo.getMeta();
        String[] fieldFilter = meta.getPropertyNames();
        return createDatalistFromRepo(repo, fieldFilter);
    }

    /**
     * Creates a datatable using repo meta using just the given fields
     *
     * @param repo
     * @param properties
     * @return
     */
    public UIDatalist createDatalistFromRepo(Repository repo, String[] properties) {
        UIDatalist datalist = new UIDatalist();
        datalist.setRepo(repo);

        return datalist;
    }

    /**
     * Adds the datalistitem node if needed
     *
     * @param node
     */
    private void addItemNode(ConfigNode<UIDatalist> node) {
        String tag = "datalistitem";
        List<ConfigNode> dataListKids = BuilderHelper.findChildrenByTag(node, tag);
        if (dataListKids.size() == 0) {
            ConfigNode<UICard> dataListItemNode = new ConfigNode<>(tag);
            dataListItemNode.setId(node.getId() + "_" + tag);
            // add all nested elements as children of the template datalistItem excluding repo tags
            List<ConfigNode> children = new ArrayList<ConfigNode>();

            for (ConfigNode n : node.getChildren()) {
                if (!isRepoTag(n.getName())) {
                    children.add(n);
                } else {
                    // keep within datalist
                    dataListKids.add(n);
                }
            }
            dataListItemNode.setChildren(children);
            dataListKids.add(dataListItemNode);
            node.setChildren(dataListKids);
        }
    }

    private boolean isRepoTag(String name) {
        name = name.toLowerCase();
        return name.equals("repo") || name.equals("repofilter") || name.equals("meta");
    }

}
