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

import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.card.UICard;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraph;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.DESCRIPTION;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDABLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.IMAGE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.LABEL;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.NAME;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.SUBTITLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TITLE;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UICardBuilder extends BaseUIComponentBuilder<UICard> {

    public UICardBuilder(String tagName) {
        super(tagName, UICard.class);
    }

    @Override
    protected UICard instantiate(ConfigNode<UICard> node) {
        createNestedNodes(node);
        return super.instantiate(node);
    }

    private void createNestedNodes(ConfigNode<UICard> node) {
        Map<String, String> attributes = node.getAttributes();

        String attValue = attributes.get(IMAGE.name);
        if (attValue != null) {
            ConfigNode imageNode = BuilderHelper.createNodeFromAttribute(IMAGE, attValue, "image");
            node.addChild(imageNode);
            attributes.remove(IMAGE.name);
        }
        attValue = attributes.get(TITLE.name);
        if (attValue != null) {
            ConfigNode titleNode = BuilderHelper.createNodeFromAttribute(TITLE, attValue, "p");
            node.addChild(titleNode);
            attributes.remove(TITLE.name);
        }
        attValue = attributes.get(SUBTITLE.name);
        if (attValue != null) {
            ConfigNode subtitleNode = BuilderHelper.createNodeFromAttribute(SUBTITLE, attValue, "p");
            node.addChild(subtitleNode);
            attributes.remove(SUBTITLE.name);
        }
    }

    @Override
    public void setupOnSubtreeStarts(ConfigNode<UICard> node) {
        processHeader(node);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UICard> node) {
        UICard card = node.getElement();
        for (ConfigNode child : node.getChildren()) {
            if (TITLE.name.equals(child.getAttribute(NAME.name))) {
                card.setTitle((UIParagraph) child.getElement());
            } else if (SUBTITLE.name.equals(child.getAttribute(NAME.name))) {
                card.setSubtitle((UIParagraph) child.getElement());
            } else if (DESCRIPTION.name.equals(child.getAttribute(NAME.name))) {
                card.setDescription((UIParagraph) child.getElement());
            } else if (child.getElement() instanceof UIImage) {
                card.setChildren(new UIComponent[]{(UIImage) child.getElement()});
            }
        }
    }

    protected Object getDefaultAttributeValue(UIDatalist element, ConfigNode node, String attName) {
        if (AttributeDef.ALLOWS_PARTIAL_RESTORE.name.equals(attName)) {
            return Boolean.TRUE;
        }
        return null;
    }

    private void processHeader(ConfigNode<UICard> node) {
        UICard card = node.getElement();
        if (node.getAttributes().containsKey(LABEL.name)) {
            card.setLabel(node.getAttribute(LABEL.name));
        }
        if (node.getAttributes().containsKey(EXPANDED.name)) {
            card.setExpanded(Boolean.parseBoolean(node.getAttribute(EXPANDED.name)));
        }
        if (node.getAttributes().containsKey(EXPANDABLE.name)) {
            card.setExpandable(Boolean.parseBoolean(node.getAttribute(EXPANDABLE.name)));
        }
    }
}