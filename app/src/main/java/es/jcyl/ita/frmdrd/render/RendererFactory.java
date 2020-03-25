package es.jcyl.ita.frmdrd.render;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.datatable.DatatableRenderer;
import es.jcyl.ita.frmdrd.ui.components.form.FormRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.CheckBoxFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.DateFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.TextFieldRenderer;
import es.jcyl.ita.frmdrd.ui.components.view.ViewRenderer;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

public class RendererFactory {

    private static RendererFactory _instance;

    private static final Map<String, Renderer> renderInstances = new HashMap<String, Renderer>();

    private RendererFactory() {
        renderInstances.put("view", new ViewRenderer());
        renderInstances.put("form", new FormRenderer());
        renderInstances.put("textfield", new TextFieldRenderer());
        renderInstances.put("date", new DateFieldRenderer());
        renderInstances.put("checkbox", new CheckBoxFieldRenderer());
        renderInstances.put("datatable", new DatatableRenderer());
    }

    public static RendererFactory getInstance() {
        if (_instance == null) {
            _instance = new RendererFactory();
        }
        return _instance;
    }

    public Renderer getRenderer(String rendererType) {
        if (!renderInstances.containsKey(rendererType.toLowerCase())) {
            throw new ViewConfigException("No renderer found for renderType: " + rendererType);
        }
        return renderInstances.get(rendererType);
    }
}
