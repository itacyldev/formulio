package es.jcyl.ita.formic.forms.components;

import java.util.ArrayList;
import java.util.List;

public class UIGroupComponent extends AbstractUIComponent {

    protected String LayoutType;

    protected List<UIInputComponent> fields;

    protected String[] properties;

    public void setChildren(UIComponent[] children) {
        super.setChildren(children);
        this.fields = findNestedFields(this, UIInputComponent.class);
    }

    public void addChild(UIComponent... lstChildren) {
        super.addChild(lstChildren);
        this.fields = findNestedFields(this, UIInputComponent.class);
    }

    public static <T> List<T> findNestedFields(UIComponent root, Class<T> clazz) {
        List<T> lst = new ArrayList<>();
        _findByClass(root, clazz, lst);
        return lst;
    }

    private static <T> void _findByClass(UIComponent root, Class<T> clazz, List<T> output) {
        if (clazz.isInstance(root)) {
            output.add((T) root);
        } else {
            if (!root.hasChildren()) {
                return;
            } else {
                for (UIComponent kid : root.getChildren()) {
                    _findByClass(kid, clazz, output);
                }
                return;
            }
        }
    }


    public void removeAll() {
        this.children = null;
        this.fields.clear();
        this.fields = null;
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
