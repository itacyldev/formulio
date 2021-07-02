package es.jcyl.ita.formic.forms.components;

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.repo.meta.Identificable;
import es.jcyl.ita.formic.forms.view.ViewConfigException;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

public abstract class AbstractUIComponent implements Identificable, UIComponent {

    protected String id;
    private String absoluteId;
    protected String label;

    protected ValueBindingExpression valueExpression;
    private ValueBindingExpression renderExpression;

    protected UIView root;
    protected UIComponent parent;
    protected UIForm parentForm;
    protected EntityHolder entityHolder;
    protected UIComponent[] children;

    protected String rendererType;
    protected boolean renderChildren;
    private boolean allowsPartialRestore = true;

    protected ValueBindingExpression readonly;
    protected String readonlyMessage;

    protected ValueBindingExpression placeHolder;
    /**
     * Indicates if current component value is referencing the value from a related entity and
     * not from the mainEntity.
     */
    private boolean isEntityMapping = false;

    protected UIAction action;
    /**
     * Scripting hooks
     */
    private String onBeforeRenderAction;
    private String onAfterRenderAction;

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
        if (this.absoluteId == null) {
            this.absoluteId = calculateAbsoluteId();
        }
        return this.absoluteId;
    }

    private String calculateAbsoluteId() {
        String completeId = id;
        if (parent != null) {
            completeId = parent.getAbsoluteId() + "." + id;
        }
        return completeId;
    }

    /**
     * finds form child by its id
     *
     * @param id
     */
    public UIComponent getChildById(String id) {
        return UIComponentHelper.findChild(this, id);
    }


    public void setId(final String id) {
        this.id = id;
        this.absoluteId = this.calculateAbsoluteId();
    }

    public UIComponent getParent() {
        return parent;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
        this.parentForm = null;//force recalculate on next access
        if (parent != null) {
            this.root = parent.getRoot();
        }
        this.absoluteId = calculateAbsoluteId();
    }

    public UIComponent[] getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return this.children != null && this.children.length > 0;
    }

    public void addChild(UIComponent child) {
        addChild(new UIComponent[]{child});
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

    public void removeAll() {
        this.children = null;
    }

    public String getRendererType() {
        return rendererType;
    }

    public void setRendererType(String rendererType) {
        this.rendererType = rendererType;
    }

    public UIView getRoot() {
        return root;
    }

    public void setRoot(UIView root) {
        this.root = root;
        if (this.children != null) {
            for (UIComponent kid : this.children) {
                kid.setRoot(root);
            }
        }
    }

    public void setChildren(List<UIComponent> children) {
        setChildren(children.toArray(new AbstractUIComponent[children.size()]));
    }

    public void setChildren(UIComponent[] children) {
        this.children = children;
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

    @Override
    public EntityHolder getEntityHolder() {
        if (entityHolder == null) {
            // find
            UIComponent node = this.getParent();
            // climb up the tree until you find a form
            while ((node != null) && !(node instanceof EntityHolder)) {
                node = node.getParent();
            }
            this.entityHolder = (node instanceof EntityHolder) ? (EntityHolder) node : null;
        }
        return entityHolder;
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
            Object value = getValue(context, this.valueExpression);
            if (value == null) {
                if (this.placeHolder == null) {
                    return null;
                }
                value = getValue(context, this.placeHolder);
            }
            return value;
        }
    }

    private Object getValue(Context context, ValueBindingExpression valueBindingExpression) {
        Object value;
        try {
            value = JexlFormUtils.eval(context, valueBindingExpression);
        } catch (Exception e) {
            error("Error while trying to evaluate JEXL expression: " + valueBindingExpression.toString(), e);
            value = null;
        }
        return value;
    }

    public boolean isRendered(Context context) {
        if (this.renderExpression == null) {
            return true;
        } else {
            // evaluate expression against context
            Object value = JexlFormUtils.eval(context, this.renderExpression);
            try {
                return (Boolean) ConvertUtils.convert(value, Boolean.class);
            } catch (Exception e) {
                throw new ViewConfigException(String.format("Invalid rendering expression in " +
                                "component [%s] the resulting value couldn't be cast to Boolean.",
                        this.getId(), this.renderExpression, e));
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOnBeforeRenderAction() {
        return onBeforeRenderAction;
    }

    public void setOnBeforeRenderAction(String onBeforeRenderAction) {
        this.onBeforeRenderAction = onBeforeRenderAction;
    }

    public String getOnAfterRenderAction() {
        return onAfterRenderAction;
    }

    public void setOnAfterRenderAction(String onAfterRenderAction) {
        this.onAfterRenderAction = onAfterRenderAction;
    }

    public UIAction getAction() {
        return action;
    }

    public void setAction(UIAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", valueExpr=" + valueExpression +
                ", renderExpr=" + renderExpression +
                '}';
    }

    /**
     * Method used to get all the binding expressions defined for this component so dependencies
     * inter-components can be easily calculated.
     *
     * @return
     */
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return ExpressionHelper.getExpressions(this);
    }

    public void setEntityMapping(boolean entityMapping) {
        isEntityMapping = entityMapping;
    }

    public String getReadonlyMessage() {
        return readonlyMessage;
    }

    public void setReadonlyMessage(String readonlyMessage) {
        this.readonlyMessage = readonlyMessage;
    }

    public boolean isReadonly(Context context) {
        if (this.readonly == null) {
            return false;
        } else {
            try {
                return (Boolean) ConvertUtils.convert(JexlFormUtils.eval(context, this.readonly), Boolean.class);
            } catch (Exception e) {
                error("Error while trying to evaluate JEXL expression: " + this.readonly.toString(), e);
                return false;
            }
        }
    }

    public void setReadonly(ValueBindingExpression readonly) {
        this.readonly = readonly;
    }

    public ValueBindingExpression getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(ValueBindingExpression placeHolder) {
        this.placeHolder = placeHolder;
    }

    public Boolean getAllowsPartialRestore() {
        return allowsPartialRestore;
    }

    public void setAllowsPartialRestore(Boolean allowsPartialRestore) {
        this.allowsPartialRestore = allowsPartialRestore;
    }
}