package es.jcyl.ita.formic.forms.components.buttonbar;

import es.jcyl.ita.formic.forms.components.UIGroupComponent;

public class UIButtonBar extends UIGroupComponent {

    public enum ButtonBarType { BOTTOM, MENU, FAB, DEFAULT};
    /**
     * button Bar types: bottom, menu, fab
     */
    private String type = ButtonBarType.DEFAULT.name();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
