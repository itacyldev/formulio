package es.jcyl.ita.formic.forms.config.builders;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Default class to create basic components
 */
public class DefaultGroupComponentBuilder extends DefaultComponentBuilder {

    public DefaultGroupComponentBuilder(String tagName, Class clazz) {
        super(tagName, clazz);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode node) {
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);
        UIGroupComponent element = (UIGroupComponent) node.getElement();
        element.setChildren(uiComponents);
    }
}
