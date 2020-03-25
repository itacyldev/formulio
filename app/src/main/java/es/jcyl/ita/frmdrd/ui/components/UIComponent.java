package es.jcyl.ita.frmdrd.ui.components;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.render.Renderer;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

public abstract class UIComponent implements Serializable {
    protected UIComponent root;
    protected String id;
    protected UIComponent parent;
    protected UIComponent parentForm;
    protected List<UIComponent> children;


    protected String label;

    protected String reRender;
    protected String renderCondition;
    protected String update;
    private String rendererType;

    /** if the children of this component have to be rendered individually */
    private boolean renderChildren;

    // behaviour
    // save/restore state from context

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }

    public List<UIComponent> getChildren() {
        return children;
    }
    public boolean hasChildren(){
        return this.children != null  && this.children.size() == 0;
    }

    public UIComponent findChild(String id){
        if(this.getId().equalsIgnoreCase(id)){
            return this;
        } else {
            if(!this.hasChildren()){
                return null;
            } else {
                for (UIComponent kid: this.getChildren()){
                    UIComponent found = kid.findChild(id);
                    if(found != null){
                        return found;
                    }
                }
                return null;
            }
        }
    }

    public void addChild(UIComponent child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getReRender() {
        return reRender;
    }

    public void setReRender(String reRender) {
        this.reRender = reRender;
    }

    public String getRenderCondition() {
        return renderCondition;
    }

    public void setRenderCondition(String renderCondition) {
        this.renderCondition = renderCondition;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getRendererType() {
        return rendererType;
    }

    public void setRendererType(String rendererType) {
        this.rendererType = rendererType;
    }

    @Override
    public String toString() {
        String output = "";

        if (id != null) {
            output += id;
        }
        if (id != null && label != null) {
            output += " / ";
        }
        if (label != null) {
            output += label;
        }

        if (output.length() > 0) {
            return output;
        } else {
            return super.toString();
        }
    }


    public UIComponent getRoot() {
        return root;
    }

    public void setRoot(UIComponent root) {
        this.root = root;
    }

    public void setChildren(List<UIComponent> children) {
        this.children = children;
    }

    public boolean isRenderChildren() {
        return renderChildren;
    }

    public void setRenderChildren(boolean renderChildren) {
        this.renderChildren = renderChildren;
    }

    public UIComponent getParentForm() {
        return parentForm;
    }

    public void setParentForm(UIComponent parentForm) {
        this.parentForm = parentForm;
    }

    public abstract void validate(es.jcyl.ita.frmdrd.context.Context context);

}