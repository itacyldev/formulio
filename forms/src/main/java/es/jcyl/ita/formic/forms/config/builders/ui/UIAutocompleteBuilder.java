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

import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.OptionsConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.components.option.UIOption;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIAutocompleteBuilder extends BaseUIComponentBuilder<UIAutoComplete> {

    public UIAutocompleteBuilder(String tagName) {
        super(tagName, UIAutoComplete.class);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIAutoComplete> node) {
        UIBuilderHelper.setUpValueExpressionType(node);

        UIAutoComplete element = node.getElement();
        // attach nested options
        List<ConfigNode> optionNodes = ConfigNodeHelper.getDescendantByTag(node, "option");
        if (CollectionUtils.isNotEmpty(optionNodes)) {
            if (node.hasAttribute("repo")) {
                throw new ConfigurationException(error(String.format("In component <autocomplete id=\"%s\"/> " +
                        "you have defined both <options/> and repo attribute. You can use just " +
                        "one of them.", node.getId())));
            }
            UIOption[] options = new UIOption[optionNodes.size()];
            for (int i = 0; i < optionNodes.size(); i++) {
                options[i] = (UIOption) optionNodes.get(i).getElement();
            }
            element.setOptions(options);
        }
        List<ConfigNode> optionsList = ConfigNodeHelper.getDescendantByTag(node, "options");
        if (element.getRepo() == null) {
            // make sure dynamic properties aren't present
            if (CollectionUtils.isNotEmpty(optionsList)) {
                OptionsConfig optionsConf = (OptionsConfig) optionsList.get(0).getElement(); // Just one
                if (hasDynamicAttributes(optionsConf)) {
                    throw new ConfigurationException(error(String.format("In component <options> inside " +
                            "<autocomplete id=\"%s\"/>, you have defined attributes to collection options " +
                            "from repo, but 'repo' attribute is not set in the <autocomplete/> element.", node.getId())));
                }
            }
        } else {
            // check if there's a <options/> nested element with dynamic values
            if (CollectionUtils.isNotEmpty(optionsList)) {
                OptionsConfig optionsConf = (OptionsConfig) optionsList.get(0).getElement(); // Just one
                element.setOptionValueProperty(optionsConf.getValueProperty());
                element.setOptionLabelExpression(optionsConf.getLabelExpression());
                element.setOptionLabelFilteringProperty(optionsConf.getLabelFilteringProperty());
            }
            // set default values
            if (element.getOptionValueProperty() == null) {
                element.setOptionValueProperty("id");
            }
            if (element.getOptionLabelExpression() == null) {
                ValueBindingExpression idExpression = getFactory().getExpressionFactory().create("id");
                element.setOptionLabelExpression(idExpression);
            }
            if (element.getOptionLabelFilteringProperty() == null) {
                element.setOptionLabelFilteringProperty("id");
            }
            // check properties against repo
            checkRepoProperties(element);
        }
    }


    /**
     * When a repo is used to configure options properties, this method helps to validate the option
     * attributes to make sure the referenced properties exist.
     *
     * @param element
     */
    private void checkRepoProperties(UIAutoComplete element) {
        EntityMeta meta = element.getRepo().getMeta();
        String propertyName = element.getOptionValueProperty();
        if (!meta.containsProperty(propertyName)) {
            throw new ConfigurationException(DevConsole.error(String.format("Invalid repo property referenced " +
                            "in element <autocomplete/>. Property '%s' doesn't exists in  repo [%s]",
                    propertyName, element.getRepo().getId())));
        }
        propertyName = element.getOptionLabelFilteringProperty();
        if (!meta.containsProperty(propertyName)) {
            throw new ConfigurationException(DevConsole.error(String.format("Invalid repo property referenced " +
                            "in element <autocomplete/>. Property '%s' doesn't exists in  repo [%s]",
                    propertyName, element.getRepo().getId())));
        }
    }


    private boolean hasDynamicAttributes(OptionsConfig optionsConf) {
        return optionsConf.getLabelExpression() != null
                || optionsConf.getLabelFilteringProperty() != null
                || optionsConf.getValueProperty() != null;
    }

}
