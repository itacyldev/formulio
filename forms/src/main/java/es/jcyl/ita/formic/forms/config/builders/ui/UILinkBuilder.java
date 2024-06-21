package es.jcyl.ita.formic.forms.config.builders.ui;
/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import java.util.List;

import es.jcyl.ita.formic.forms.components.link.UILink;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
 */
public class UILinkBuilder extends BaseUIComponentBuilder<UILink> {

    public UILinkBuilder() {
        super("link", UILink.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UILink> node) {
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UILink> node) {
        //BuilderHelper.setUpValueExpressionType/(node);

        UILink element = node.getElement();

        UIAction uiAction = element.getAction();
        // FORMIC-245 Crear atribute resolverpara el attribute "route"
        if (uiAction == null) { // default action
            uiAction = new UIAction();
            uiAction.setType("nav");
            uiAction.setRoute(element.getRoute());
            element.setAction(uiAction);
        }
        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = BuilderHelper.getParams(paramNodes);
            uiAction.setParams(params);
        }
    }

}
