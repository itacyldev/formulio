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

import es.jcyl.ita.formic.forms.components.button.UIButton;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
 */
public class UIButtonBuilder extends AbstractComponentBuilder<UIButton> {

    public UIButtonBuilder() {
        super("button", UIButton.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIButton> node) {
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIButton> node) {
        //BuilderHelper.setUpValueExpressionType/(node);

        UIButton element = node.getElement();
        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = getParams(paramNodes);
            element.setParams(params);
        }
    }

    private UIParam[] getParams(List<ConfigNode> paramNodes) {
        UIParam[] params = new UIParam[paramNodes.size()];
        for (int i = 0; i < paramNodes.size(); i++) {
            UIParam uiParam = new UIParam();
            ConfigNode paramNode = paramNodes.get(i);
            if (paramNode.hasAttribute("name")) {
                uiParam.setName(paramNode.getAttribute("name"));
            }
            if (paramNode.hasAttribute("value")) {
                ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
                uiParam.setValue(exprFactory.create(paramNodes.get(i).getAttribute("value")));
            }
            params[i] = uiParam;
        }
        return params;
    }

}
