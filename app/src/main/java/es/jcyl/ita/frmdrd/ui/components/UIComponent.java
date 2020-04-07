package es.jcyl.ita.frmdrd.ui.components;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

public abstract class UIComponent implements Serializable {
    protected UIComponent root;
    protected String id;
    protected UIComponent parent;
    protected UIComponent parentForm;
    protected List<UIComponent> children;
    protected String label;
    private boolean readOnly = false;
    private String rendererType;

    private ValueBindingExpression valueExpression;
    private ValueBindingExpression renderExpression;

    /**
     * if the children of this component have to be rendered individually
     */
    private boolean renderChildren;

    public String getId() {
        return id;
    }

    /**
     * gets the id by concatenating the id of all your ancestors
     *
     * @return
     */
    public String getAbsoluteId() {
        String completeId = id;
        if (parent != null) {
            completeId = parent.getAbsoluteId() + "." + id;
        }
        return completeId;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
        if (parent instanceof UIForm) {
            this.parentForm = parent;
        }
    }

    public List<UIComponent> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }

    public UIComponent findChild(String id) {
        if (this.getId().equalsIgnoreCase(id)) {
            return this;
        } else {
            if (!this.hasChildren()) {
                return null;
            } else {
                for (UIComponent kid : this.getChildren()) {
                    UIComponent found = kid.findChild(id);
                    if (found != null) {
                        return found;
                    }
                }
                return null;
            }
        }
    }

    public void addChild(UIComponent... lstChildren) {
        if (children == null) {
            children = new ArrayList<>();
        }
        for (UIComponent kid : lstChildren) {
            kid.setParent(this);
            children.add(kid);
        }
    }

    public void addChild(UIComponent child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        child.setParent(this);
        children.add(child);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getRendererType() {
        return rendererType;
    }

    public void setRendererType(String rendererType) {
        this.rendererType = rendererType;
    }

    public UIComponent getRoot() {
        return root;
    }

    public void setRoot(UIComponent root) {
        this.root = root;
    }

    public void setChildren(UIComponent[] children) {
        this.children = new ArrayList<UIComponent>();
        for (UIComponent c : children) {
            c.setParent(this);
            this.children.add(c);
        }
    }

    public void setChildren(List<UIComponent> children) {
        this.children = children;
        // re-link children parent
        if (this.children != null) {
            for (UIComponent c : this.children) {
                c.setParent(this);
            }
        }
    }

    public boolean isRenderChildren() {
        return renderChildren;
    }

    public void setRenderChildren(boolean renderChildren) {
        this.renderChildren = renderChildren;
    }

    public UIForm getParentForm() {
        UIComponent node = this.getParent();
        // climb up the tree until you find a form
        while ((node != null) && !(node instanceof UIForm)) {
            node = node.getParent();
        }
        return (node instanceof UIForm) ? (UIForm) node : null;
    }

    public void setParentForm(UIForm parentForm) {
        this.parentForm = parentForm;
    }

    public ValueBindingExpression getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(ValueBindingExpression valueExpression) {
        this.valueExpression = valueExpression;
    }

    public ValueBindingExpression getRenderExpression() {
        return renderExpression;
    }

    public void setRenderExpression(ValueBindingExpression renderExpression) {
        this.renderExpression = renderExpression;
    }

    public Object getValue(Context context) {
        if (this.valueExpression == null) {
            return null;
        } else {
            // evaluate expression against context
            return JexlUtils.eval(context, this.valueExpression);
        }
    }

    public boolean isRendered(Context context) {
        if (this.renderExpression == null) {
            return true;
        } else {
            // evaluate expression against context
            Object value = JexlUtils.eval(context, this.renderExpression);
            try {
                return (Boolean) ConvertUtils.convert(value, Boolean.class);
            } catch (Exception e) {
                throw new ViewConfigException(String.format("Invalid rendering expression in " +
                                "component [%s] the resulting value couldn't be cast to Boolean.",
                        this.getId(), this.renderExpression, e));
            }
        }
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

    public ValueBindingExpression[] getValueBindingExpressions() {
        List<ValueBindingExpression> express = new ArrayList<ValueBindingExpression>();
        if (this.valueExpression != null) {
            express.add(valueExpression);
        }
        if (this.renderExpression != null) {
            express.add(renderExpression);
        }
        return express.toArray(new ValueBindingExpression[express.size()]);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}