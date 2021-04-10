package es.jcyl.ita.formic.forms.config.builders.scripts;
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

import java.util.List;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.scripts.ScriptSource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ScriptSourceBuilder extends AbstractComponentBuilder<ScriptSource> {

    public ScriptSourceBuilder(String tagName) {
        super(tagName, ScriptSource.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<ScriptSource> node) {

    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<ScriptSource> node) {
        // get script text and store relacted to current formController
        List<String> texts = node.getTexts();
        String source = StringUtils.join(texts, '\n');

        ConfigNode controllerNode = BuilderHelper.findParentController(node);
        try {
            ScriptEngine.getInstance().store(controllerNode.getId(), source);
        } catch (Exception e) {
            DevConsole.error(source);
            throw new ConfigurationException("An error occurred while trying to load script in file ${file}", e);
        }
    }
}
