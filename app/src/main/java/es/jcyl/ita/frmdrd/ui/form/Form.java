package es.jcyl.ita.frmdrd.ui.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class Form extends FormEntity {

    private Map<String, Tab> tabs = new LinkedHashMap<>();

    public Map<String, Tab> getTabs() {
        return tabs;
    }

    public void addTab(final Tab tab) {
        tabs.put(tab.getId(), tab);
    }

}