package es.jcyl.ita.frmdrd.ui.components;

import java.util.List;

public class UIGroupComponent extends UIComponent {

    protected String LayoutType;

    protected List<UIInputComponent> fields;

    protected String[] properties;

    @Override
    public void setChildren(UIComponent[] children) {
        super.setChildren(children);
        this.fields = UIComponentHelper.findDescendatByClass(this, UIInputComponent.class);
    }

    @Override
    public void addChild(UIComponent... lstChildren) {
        super.addChild(lstChildren);
        this.fields = UIComponentHelper.findDescendatByClass(this, UIInputComponent.class);
    }

    public List<UIInputComponent> getFields() {
        return this.fields;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public String getLayoutType() {
        return LayoutType;
    }

    public void setLayoutType(String layoutType) {
        LayoutType = layoutType;
    }


}
