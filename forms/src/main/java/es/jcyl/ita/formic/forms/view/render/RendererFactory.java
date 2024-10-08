package es.jcyl.ita.formic.forms.view.render;
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

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteRenderer;
import es.jcyl.ita.formic.forms.components.base.UIGroupBaseRenderer;
import es.jcyl.ita.formic.forms.components.card.UICardRenderer;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistRenderer;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatableRenderer;
import es.jcyl.ita.formic.forms.components.form.UIFormRender;
import es.jcyl.ita.formic.forms.components.image.UIImageGalleryItemRenderer;
import es.jcyl.ita.formic.forms.components.image.UIImageGalleryRenderer;
import es.jcyl.ita.formic.forms.components.image.UIImageRenderer;
import es.jcyl.ita.formic.forms.components.inputfield.CheckBoxFieldRenderer;
import es.jcyl.ita.formic.forms.components.inputfield.DateFieldRenderer;
import es.jcyl.ita.formic.forms.components.inputfield.TextAreaRenderer;
import es.jcyl.ita.formic.forms.components.inputfield.TextFieldRenderer;
import es.jcyl.ita.formic.forms.components.link.UIButtonRenderer;
import es.jcyl.ita.formic.forms.components.link.UILinkRenderer;
import es.jcyl.ita.formic.forms.components.placeholders.UIDivisorRenderer;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraphRenderer;
import es.jcyl.ita.formic.forms.components.radio.RadioRenderer;
import es.jcyl.ita.formic.forms.components.select.SelectRenderer;
import es.jcyl.ita.formic.forms.components.tab.TabItemRenderer;
import es.jcyl.ita.formic.forms.components.tab.UITabRenderer;
import es.jcyl.ita.formic.forms.components.table.UIRowRenderer;
import es.jcyl.ita.formic.forms.components.table.UITableRenderer;
import es.jcyl.ita.formic.forms.components.view.UIViewRenderer;
import es.jcyl.ita.formic.forms.view.ViewConfigException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RendererFactory {

    private static RendererFactory _instance;

    private static final Map<String, Renderer> renderInstances = new HashMap<String, Renderer>();

    private RendererFactory() {
        renderInstances.put("view", new UIViewRenderer());
        renderInstances.put("form", new UIFormRender());
        renderInstances.put("text", new TextFieldRenderer());
        renderInstances.put("textarea", new TextAreaRenderer());
        renderInstances.put("date", new DateFieldRenderer());
        renderInstances.put("datetime", new DateFieldRenderer());
        renderInstances.put("switcher", new CheckBoxFieldRenderer());
        renderInstances.put("datatable", new UIDatatableRenderer());
        renderInstances.put("link", new UILinkRenderer());
        renderInstances.put("select", new SelectRenderer());
        renderInstances.put("radio", new RadioRenderer());
        renderInstances.put("autocomplete", new AutoCompleteRenderer());
        renderInstances.put("tab", new UITabRenderer());
        renderInstances.put("tabitem", new TabItemRenderer());
        renderInstances.put("table", new UITableRenderer());
        renderInstances.put("row", new UIRowRenderer());
        renderInstances.put("image", new UIImageRenderer());
        renderInstances.put("datalist", new UIDatalistRenderer());
        renderInstances.put("datalistitem", new UIGroupBaseRenderer(R.layout.widget_datalistitem));
        renderInstances.put("card", new UICardRenderer());
        renderInstances.put("p", new UIParagraphRenderer());
        renderInstances.put("divisor", new UIDivisorRenderer());
        renderInstances.put("button", new UIButtonRenderer());
        renderInstances.put("buttonbar", new UIGroupBaseRenderer(R.layout.widget_buttonbar));
        renderInstances.put("imagegallery", new UIImageGalleryRenderer());
        renderInstances.put("imagegalleryitem", new UIImageGalleryItemRenderer());
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
