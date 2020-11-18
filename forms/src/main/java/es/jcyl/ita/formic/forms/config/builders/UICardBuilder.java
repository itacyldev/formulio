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

import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.card.UICard;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.ui.BaseUIComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDABLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.EXPANDED;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.IMAGE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.SUBTITLE;
import static es.jcyl.ita.formic.forms.config.meta.AttributeDef.TITLE;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UICardBuilder extends BaseUIComponentBuilder<UICard> {


    protected UICardBuilder(String tagName) {
        super(tagName, UICard.class);
    }

    protected void setAttributes(UICard card, ConfigNode node) {
        Map<String, String> attributes = node.getAttributes();

        String attValue = attributes.get(IMAGE.name);
        if (attValue != null) {
            ConfigNode imageNode = BuilderHelper.createNodeFromAttribute(IMAGE, attValue, "image");
            node.addChild(imageNode);
            attributes.remove(IMAGE.name);
        }

        attValue = attributes.get(TITLE.name);
        if (attValue != null) {
            ConfigNode titleNode = BuilderHelper.createNodeFromAttribute(TITLE, attValue, "head");
            node.addChild(titleNode);
            attributes.remove(TITLE.name);
        }

        attValue = attributes.get(SUBTITLE.name);
        if (attValue != null) {
            ConfigNode subtitleNode = BuilderHelper.createNodeFromAttribute(SUBTITLE, attValue, "head");
            node.addChild(subtitleNode);
            attributes.remove(SUBTITLE.name);
        }

        super.setAttributes(card, node);
    }

    @Override
    public void setupOnSubtreeEnds(ConfigNode<UICard> node) {
        UICard card = node.getElement();
        UIComponent[] children = ConfigNodeHelper.getUIChildren(node);

        for (UIComponent child : children) {
            if (child.getId().equals(TITLE.name)) {
                card.setTitle((UIHeading) child);
            } else if (child.getId().equals(SUBTITLE.name)) {
                card.setSubtitle((UIHeading) child);
            } else if (child instanceof UIImage) {
                card.setChildren(new UIComponent[]{child});
            }

        }

    }

    @Override
    public void setupOnSubtreeStarts(ConfigNode<UICard> node) {
        List<ConfigNode> children = node.getChildren();



        ConfigNode contentNode;
        List<ConfigNode> contentList = BuilderHelper.findChildrenByTag(node, "header");
        if (!contentList.isEmpty()) {
            contentNode = contentList.get(0);
            children.remove(contentNode);
        } else {

        }

        for (ConfigNode child : children) {
            if (child.getName().equals("header")) {
                processHeader(node, child);
            }
        }
    }

    private void processHeader(ConfigNode<UICard> node, ConfigNode child) {
        if(child.getAttributes().containsKey(EXPANDED.name)){

        }

        if(child.getAttributes().containsKey(EXPANDABLE.name)){

        }

    }


}