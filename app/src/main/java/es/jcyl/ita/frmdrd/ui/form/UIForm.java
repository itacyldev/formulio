package es.jcyl.ita.frmdrd.ui.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class UIForm extends UIComponent {

    private Map<String, UITab> tabs = new LinkedHashMap<>();

    public Map<String, UITab> getTabs() {
        return tabs;
    }

    public void addTab(final UITab tab) {
        tab.setParent(this);
        this.addChild(tab);
        tabs.put(tab.getId(), tab);
    }
}