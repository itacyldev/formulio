package es.jcyl.ita.formic.forms.config.builders.ui;
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

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.option.MultiOptionComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIMultiOptionBuilder<T extends UIComponent> extends BaseUIComponentBuilder<T> {

    public UIMultiOptionBuilder(String tagName, Class<? extends T> clazz) {
        super(tagName, clazz);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<T> node) {
        // attach nested options
        List<ConfigNode> optionNodes = ConfigNodeHelper.getDescendantByTag(node, "option");
        if (CollectionUtils.isNotEmpty(optionNodes)) {
            if (node.hasAttribute("repo")) {
                throw new ConfigurationException(DevConsole.error(String.format("In component <%s id=\"%s\"/> " +
                        "you have defined both <options/> and repo attribute. You can use just " +
                        "one of them.", node.getName(), node.getId())));
            }
            UIOption[] options = new UIOption[optionNodes.size()];
            for (int i = 0; i < optionNodes.size(); i++) {
                options[i] = (UIOption) optionNodes.get(i).getElement();
            }
            ((MultiOptionComponent) node.getElement()).setOptions(options);
        }
    }
}
