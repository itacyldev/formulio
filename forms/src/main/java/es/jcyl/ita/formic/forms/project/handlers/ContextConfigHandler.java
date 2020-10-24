package es.jcyl.ita.formic.forms.project.handlers;
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

import android.net.Uri;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.project.ProjectResource;
import es.jcyl.ita.formic.repo.RepositoryFactory;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Class responsible for Global Context initialization and for transferring this context to
 * ContextAware objects.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ContextConfigHandler extends AbstractProjectResourceHandler {

    @Override
    public void handle(ProjectResource resource) {
        reader.setListener(this.listener);
        ConfigNode root = reader.read(Uri.fromFile(resource.file));
        CompositeContext context = createContext(root);
        register(context);
    }

    private CompositeContext createContext(ConfigNode root) {
        CompositeContext context;
        try {
            context = (CompositeContext) root.getElement();
        } catch (Exception e) {
            throw new ConfigurationException(error("Invalid XML structure in file '${file}', " +
                    "does it start with <context/>?.", e), e);
        }
        return context;
    }

    /**
     * Register new contexts from config in the global context
     * @param contextMod
     */
    private void register(CompositeContext contextMod) {
        // put all
        CompositeContext globalCtx = Config.getInstance().getGlobalContext();
        globalCtx.addAllContext(contextMod.getContexts());
    }

}
