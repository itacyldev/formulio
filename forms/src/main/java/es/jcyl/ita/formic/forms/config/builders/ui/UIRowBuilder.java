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

import es.jcyl.ita.formic.forms.components.tab.UITabItem;
import es.jcyl.ita.formic.forms.components.table.UIRow;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;

/**
 * Prepares ConfigNode subtree for table rows from XML configuration.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 */

public class UIRowBuilder extends UIGroupComponentBuilder<UIRow> {

    public UIRowBuilder() {
        super("row", UIRow.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIRow> node) {
        // Add config node for all the properties defined in the properties attribute
        if (node.hasAttribute("properties")) {
            ConfigNode ascendant = ConfigNodeHelper.findAscendantWithAttribute(node, "repo");
            Repository repo = (Repository) BuilderHelper.getElementValue(ascendant.getElement(), "repo");
            addNodesFromPropertiesAtt(node, repo);
        }
    }
}
