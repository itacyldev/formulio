package es.jcyl.ita.frmdrd.ui.components;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.el.JexlUtils;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

public abstract class UIComponent implements Serializable {

    protected String id;
    protected UIComponent root;
    protected UIComponent parent;
    protected UIForm parentForm;
    protected UIComponent[] children;

    private ValueBindingExpression valueExpression;
    private ValueBindingExpression renderExpression;

    private String rendererType;
    private boolean renderChildren;

    /**
     * if the children of this component have to be rendered individually
     */

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
    }

    public UIComponent[] getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return this.children != null && this.children.length > 0;
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
        UIComponent[] newKids;
        if (children == null) {
            newKids = Arrays.copyOf(lstChildren, lstChildren.length);
        } else {
            newKids = Arrays.copyOf(this.children, this.children.length + lstChildren.length);
            System.arraycopy(lstChildren, 0, newKids, this.children.length, lstChildren.length);
        }
        this.children = newKids;
        linkParent();
    }

    public void addChild(UIComponent child) {
        UIComponent[] newKids;
        if (children == null) {
            newKids = new UIComponent[1];
        } else {
            newKids = Arrays.copyOf(this.children, this.children.length + 1);
        }
        newKids[newKids.length - 1] = child;
        this.children = newKids;
        child.setParent(this);
    }

    public void removeAll() {
        this.children = null;
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
        this.children = children;
        // re-link children parent
        linkParent();
    }

    public void setChildren(List<UIComponent> children) {
        this.children = children.toArray(new UIComponent[children.size()]);
        // re-link children parent
        linkParent();
    }

    private void linkParent() {
        if (this.children != null) {
            for (UIComponent kid : this.children) {
                kid.setParent(this);
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
        if (this.parentForm == null) {
            // find
            UIComponent node = this.getParent();
            // climb up the tree until you find a form
            while ((node != null) && !(node instanceof UIForm)) {
                node = node.getParent();
            }
            this.parentForm = (node instanceof UIForm) ? (UIForm) node : null;
        }
        return this.parentForm;
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
        if (output.length() > 0) {
            return output;
        } else {
            return super.toString();
        }
    }

    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return ExpressionHelper.getExpressions(this);
    }

}