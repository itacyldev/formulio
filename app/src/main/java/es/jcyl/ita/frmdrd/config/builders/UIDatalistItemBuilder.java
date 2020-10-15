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

import java.util.Map;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.datalist.UIDatalist;
import es.jcyl.ita.frmdrd.ui.components.datalist.UIDatalistItem;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIDatalistItemBuilder extends BaseUIComponentBuilder<UIDatalistItem> {


    protected UIDatalistItemBuilder(String tagName) {
        super(tagName, UIDatalistItem.class);
    }

    @Override
    public void setupOnSubtreeStarts(ConfigNode<UIDatalistItem> node) {

    }

    private void setProperties(ConfigNode<UIDatalistItem> node) {
        String[] labels = new String[node.getChildren().size()];
        String[] values = new String[node.getChildren().size()];
        int i = 0;
        for (ConfigNode child : node.getChildren()) {
            Map attributes = child.getAttributes();
            labels[i] = (String) attributes.get("label");
            values[i] = (String) attributes.get("value");
            i++;
        }

        node.getElement().setPropertyLabels(labels);
        node.getElement().setPropertyValues(values);
    }

    private void setProperties(Repository repo) {
        String[] repoProps = repo.getMeta().getPropertyNames();
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UIDatalistItem> node) {
        UIDatalist parent = (UIDatalist) node.getParent().getElement();
        // if the datalist has a repo
        if (parent.getRepo() != null) {
            setProperties(parent.getRepo());
        }

        if (node.hasChildren()) {
            setProperties(node);
        }
    }
}
