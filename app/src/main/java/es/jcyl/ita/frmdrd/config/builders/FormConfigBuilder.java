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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigBuilder extends AbstractComponentBuilder<FormConfig> {


    public FormConfigBuilder(String tagName) {
        super(tagName, FormConfig.class); // main
    }

    @Override
    protected void doWithAttribute(FormConfig element, String name, String value) {

    }

    @Override
    protected void doConfigure(FormConfig formConfig, ConfigNode node) {
        // get list and edit views, check and set to configuration
        List<ConfigNode> list = getChildren(node, "list");
        if (list.size() != 1) {
            error(String.format("Each form file must contain one and just one 'list' element, found: [%s]", list.size()));
        }
        formConfig.setList((FormListController) list.get(0).getElement());

        List<ConfigNode> edits = getChildren(node, "edit");
        List<FormEditController> lst = new ArrayList<>();
        for (ConfigNode n : edits) {
            lst.add((FormEditController) n.getElement());
        }
        formConfig.setEdits(lst);
    }
}
