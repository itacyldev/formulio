package es.jcyl.ita.frmdrd.ui.components;

import java.util.LinkedHashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.context.Context;

public class UIForm extends UIComponent {

    private Map<String, UITab> tabs = new LinkedHashMap<>();

    public UIForm() {
        this.setRendererType("form");
        this.setRenderChildren(true);
    }

    public Map<String, UITab> getTabs() {
        return tabs;
    }

    public void addTab(final UITab tab) {
        tab.setParent(this);
        this.addChild(tab);
        tabs.put(tab.getId(), tab);
    }


    public void processValidators(Context context) {

    }
}