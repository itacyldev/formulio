package es.jcyl.ita.formic.forms.config.builders.ui;
/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.image.UIImageGallery;
import es.jcyl.ita.formic.forms.components.image.UIImageGalleryItem;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIImageGalleryBuilder extends BaseUIComponentBuilder<UIImageGallery> {

    private static final Set<String> NAV_ACTIONS = new HashSet<String>(Arrays.asList("add", "update"));

    public UIImageGalleryBuilder(String tagName) {
        super(tagName, UIImageGallery.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIImageGallery> node) {
        BuilderHelper.setUpRepo(node, true);
        // Add item node if doesn't exist
        addItemNode(node);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIImageGallery> node) {
        super.setupOnSubtreeEnds(node);
        setNumItems(node);
//        setUpRoute(node);
    }

    @Override
    protected Object getDefaultAttributeValue(UIImageGallery element, ConfigNode node, String attName) {
        if (AttributeDef.ALLOWS_PARTIAL_RESTORE.name.equals(attName)) {
            return Boolean.TRUE;
        }
        return super.getDefaultAttributeValue(element, node, attName);
    }

    private void setNumItems(ConfigNode<UIImageGallery> node) {
        int numItems = 1; // Number of default visible items
        if (node.hasAttribute("numItems")) {
            numItems = Integer.parseInt(node.getAttribute("numItems"));
        }
        if (node.getParent().getElement() instanceof FormListController) {
            numItems = -1; // Fill container with rows
        }
        UIImageGallery imageGallery = node.getElement();
        imageGallery.setNumItems(numItems);
    }

    public UIImageGallery createImageGalleryFromRepo(Repository repo) {
        // select all entity properties
        EntityMeta meta = repo.getMeta();
        String[] fieldFilter = meta.getPropertyNames();
        return createImageGalleryFromRepo(repo, fieldFilter);
    }

    /**
     * Creates a datatable using repo meta using just the given fields
     *
     * @param repo
     * @param properties
     * @return
     */
    public UIImageGallery createImageGalleryFromRepo(Repository repo, String[] properties) {
        UIImageGallery imagegallery = new UIImageGallery();
        imagegallery.setRepo(repo);

        return imagegallery;
    }

    /**
     * Adds the datalistitem node if needed
     *
     * @param node
     */
    private void addItemNode(ConfigNode<UIImageGallery> node) {
        String tag = "imagegalleryitem";
        List<ConfigNode> imageGalleryKids = BuilderHelper.findChildrenByTag(node, tag);
        if (imageGalleryKids.size() == 0) {
            ConfigNode<UIImageGalleryItem> iamgeGalleryItemNode = new ConfigNode<>(tag);
            iamgeGalleryItemNode.setId(node.getId() + "_" + tag);
            // add all nested elements as children of the template datalistItem excluding repo tags
            List<ConfigNode> children = new ArrayList<ConfigNode>();

            for (ConfigNode n : node.getChildren()) {
                if (!isRepoTag(n.getName())) {
                    children.add(n);
                } else {
                    // keep within datalist
                    imageGalleryKids.add(n);
                }
            }
            iamgeGalleryItemNode.setChildren(children);
            imageGalleryKids.add(iamgeGalleryItemNode);
            node.setChildren(imageGalleryKids);
        }
    }

    private boolean isRepoTag(String name) {
        name = name.toLowerCase();
        return name.equals("repo") || name.equals("repofilter") || name.equals("meta");
    }

}
