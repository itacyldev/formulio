package es.jcyl.ita.frmdrd.ui.components.tab;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class UITabItem extends UIComponent {

    public UITabItem(){
        this.setRendererType("tabitem");
        this.setRenderChildren(true);
    }
}
