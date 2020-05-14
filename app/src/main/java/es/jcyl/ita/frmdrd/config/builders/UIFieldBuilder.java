package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Provides functionality to create fields easily from configuration.
 */
public class UIFieldBuilder extends BaseUIComponentBuilder<UIField> {

    public UIFieldBuilder(String tagName) {
        super(tagName, UIField.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIField> node) {
        // get input type from "type" attribute or use tagName
        String type = node.getAttribute("type");
        if (StringUtils.isBlank(type)) {
            type = node.getName();
        }
        try {
            node.getElement().setType(UIField.TYPE.valueOf(type.toUpperCase()));
        } catch (Exception e) {
            throw new ConfigurationException(error(String.format("Invalid input type: [%s] expected " +
                    "one of: %s", type, UIField.TYPE.values())));
        }
    }
}
