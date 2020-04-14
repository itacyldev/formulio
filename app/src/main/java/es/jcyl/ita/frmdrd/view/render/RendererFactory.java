package es.jcyl.ita.frmdrd.view.render;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.autocomplete.AutoCompleteRenderer;
import es.jcyl.ita.frmdrd.ui.components.datatable.DatatableRenderer;
import es.jcyl.ita.frmdrd.ui.components.form.FormRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.CheckBoxFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.DateFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.TextFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.link.LinkRenderer;
import es.jcyl.ita.frmdrd.ui.components.select.SelectRenderer;
import es.jcyl.ita.frmdrd.ui.components.view.UIViewRenderer;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RendererFactory {

    private static RendererFactory _instance;

    private static final Map<String, Renderer> renderInstances = new HashMap<String, Renderer>();

    private RendererFactory() {
        renderInstances.put("view", new UIViewRenderer());
        renderInstances.put("form", new FormRenderer());
        renderInstances.put("text", new TextFieldRenderer());
        renderInstances.put("date", new DateFieldRenderer());
        renderInstances.put("boolean", new CheckBoxFieldRenderer());
        renderInstances.put("datatable", new DatatableRenderer());
        renderInstances.put("link", new LinkRenderer());
        renderInstances.put("select", new SelectRenderer());
        renderInstances.put("autocomplete", new AutoCompleteRenderer());

    }

    public static RendererFactory getInstance() {
        if (_instance == null) {
            _instance = new RendererFactory();
        }
        return _instance;
    }

    public Renderer getRenderer(String rendererType) {
        if (rendererType == null) {
            throw new ViewConfigException("No rendererType given, check the method getRenderType in your component.");
        }
        if (!renderInstances.containsKey(rendererType.toLowerCase())) {
            throw new ViewConfigException("No renderer found for renderType, register it in RenderFactory: " + rendererType);
        }
        return renderInstances.get(rendererType);
    }
}
