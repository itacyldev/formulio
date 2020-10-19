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

import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.converters.ConverterMap;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class BindingExpressionAttResolver extends AbstractAttributeResolver<ValueBindingExpression> {


    ValueExpressionFactory factory = ValueExpressionFactory.getInstance();

    public ValueBindingExpression resolve(ConfigNode node, String attName) {
        String expStr = node.getAttribute(attName);
        ValueBindingExpression expression = null;
        if (StringUtils.isNotBlank(expStr)) {
            if (!node.hasAttribute("converter")) {
                expression = factory.create(expStr);
            } else {
                // TODO: take in count converters when creating expression #204351
                String converter = node.getAttribute("converter");
                expression = factory.create(expStr, ConverterMap.getConverter(converter));
            }
        }
        return expression;
    }
}
