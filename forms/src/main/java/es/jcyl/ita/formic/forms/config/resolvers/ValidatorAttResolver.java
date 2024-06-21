package es.jcyl.ita.formic.forms.config.resolvers;
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

import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.validation.Validator;

/**
 * Reads "validator" attribute and creates a nested NodeConfig for the component so the
 * ValidatorBuilder will create the valdiation object later on.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ValidatorAttResolver extends AbstractAttributeResolver<String> {

    public String resolve(ConfigNode node, String attName) {
        String validatorSelector = node.getAttribute(attName);
        if (StringUtils.isNotEmpty(validatorSelector)) {
            String[] validators = validatorSelector.replace(" ", "").split(",");
            addValidatorNode(node, validators);
        }
        return validatorSelector;// no changes
    }

    private void addValidatorNode(ConfigNode<UIField> root, String[] validators) {
        for (String validator : validators) {
            if (!BuilderHelper.isValidatorIncluded(validator, root)) {
                ConfigNode<Validator> validatorNode = new ConfigNode<>("validator");
                validatorNode.setAttribute("type", validator);
                root.addChild(validatorNode);
            }
        }
    }
}
