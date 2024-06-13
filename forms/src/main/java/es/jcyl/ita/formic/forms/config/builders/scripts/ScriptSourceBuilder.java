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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.scripts.ScriptRef;
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
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<ScriptSource> node) {
        // get script text and store relacted to current formController
        ScriptRef scriptRef;
        String currentFile = this.getFactory().getInfo().getCurrentFile();
        if (node.hasAttribute("src")) {
            String absFilePath = node.getElement().getSrc();
            scriptRef = ScriptRef.createSourceFileScriptRef(absFilePath, currentFile);
        } else {
            List<String> texts = node.getTexts();
            scriptRef = ScriptRef.createInlineScriptRef(StringUtils.join(texts, '\n'), currentFile);
        }
        ConfigNode controllerNode = BuilderHelper.findParentController(node);
        if (controllerNode == null) {
            throw new ConfigurationException(error("Invalid script definition, make sure " +
                    "the <script> tag is nested inside an <edit> or <list> tag."));
        }
        try {
            this.getFactory().getScriptEngine().store(controllerNode.getId(), scriptRef);
        } catch (Exception e) {
            error(scriptRef.toString());
            throw new ConfigurationException("An error occurred while trying to load script in file ${file}", e);
        }
    }
}
