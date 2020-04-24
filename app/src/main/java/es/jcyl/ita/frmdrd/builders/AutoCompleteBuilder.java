package es.jcyl.ita.frmdrd.builders;
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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.ui.components.option.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteBuilder extends BaseInputBuilder<UIAutoComplete> {
    private List<UIOption> options;

    public AutoCompleteBuilder() {
        this.options = new ArrayList<UIOption>();
    }


    @Override
    protected UIAutoComplete emptyModel() {
        return new UIAutoComplete();
    }


    public AutoCompleteBuilder addOption(String label, String value) {
        options.add(new UIOption(label, value));
        return this;
    }

    @Override
    protected UIAutoComplete doBuild(UIAutoComplete baseModel) {
        if(StringUtils.isBlank(this.baseModel.getId())){
            throw new FormBuilderException("The id cannot be null!.");
        }
        baseModel.setOptions(options.toArray(new UIOption[options.size()]));
        return baseModel;
    }
}
