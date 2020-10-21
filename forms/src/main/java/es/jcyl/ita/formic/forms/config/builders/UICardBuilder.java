package es.jcyl.ita.formic.forms.config.builders;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.card.UICard;
import es.jcyl.ita.formic.forms.components.card.UIHeading1;
import es.jcyl.ita.formic.forms.components.card.UIHeading2;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UICardBuilder extends BaseUIComponentBuilder<UICard> {


    protected UICardBuilder(String tagName) {
        super(tagName, UICard.class);
    }

    @Override
    public void setupOnSubtreeStarts(ConfigNode<UICard> node) {

    }

    private void setProperties(ConfigNode<UICard> node) {
        String[] names = new String[node.getChildren().size()];
        String[] labels = new String[node.getChildren().size()];
        String[] values = new String[node.getChildren().size()];
        int i = 0;
        for (ConfigNode child : node.getChildren()) {
            Map attributes = child.getAttributes();
            names[i] = (String) attributes.get("name");
            labels[i] = (String) attributes.get("label");
            values[i] = (String) attributes.get("value");
            i++;
        }

    }

    private void setProperties(Repository repo) {
        String[] repoProps = repo.getMeta().getPropertyNames();
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UICard> node) {
        UICard card = node.getElement();
        UIComponent[] children = ConfigNodeHelper.getUIChildren(node);
        for (UIComponent child : children) {
            if (child instanceof UIHeading1) {
                card.setTitle((UIHeading1) child);
            } else if (child instanceof UIHeading2) {
                card.setSubtitle((UIHeading2) child);
            } else if (child instanceof UIImage) {
                card.setImage((UIImage) child);
            }

        }
    }
}