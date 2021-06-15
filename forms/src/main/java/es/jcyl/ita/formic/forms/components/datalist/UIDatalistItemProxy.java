package es.jcyl.ita.formic.forms.components.datalist;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.EntityHolder;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIDatalistItemProxy extends UIDatalistItem implements EntityHolder {

    private String id;
    private final UIComponent delegate;
    private Entity entity;

    public UIDatalistItemProxy(UIDatalistItem component) {
        this(component.getId(), component, null);
    }

    public UIDatalistItemProxy(String id, UIComponent component, Entity entity) {
        this.id = id;
        this.delegate = component;
        this.entity = entity;
    }


    public Entity getEntity() {
        return entity;
    }


    public void setEntity(Entity entity) {
        this.entity = entity;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return this.id;
    }


    public String getAbsoluteId() {
        return delegate.getAbsoluteId();
    }


    public void setChildren(List<UIComponent> children) {
        delegate.setChildren(children);
    }


    public void setChildren(UIComponent[] children) {
        delegate.setChildren(children);
    }


    public void addChild(UIComponent... lstChildren) {
        delegate.addChild(lstChildren);
    }


    public boolean hasChildren() {
        return delegate.hasChildren();
    }


    public UIComponent[] getChildren() {
        return delegate.getChildren();
    }


    public UIComponent getChildById(String id) {
        return delegate.getChildById(id);
    }


    public void setParent(UIComponent parent) {
        delegate.setParent(parent);
    }


    public UIComponent getParent() {
        return delegate.getParent();
    }


    public UIForm getParentForm() {
        return delegate.getParentForm();
    }


    public void setRoot(UIView root) {
        delegate.setRoot(root);
    }


    public UIView getRoot() {
        return delegate.getRoot();
    }


    public void setValueExpression(ValueBindingExpression valueBindingExpression) {
        delegate.setValueExpression(valueBindingExpression);
    }


    public ValueBindingExpression getValueExpression() {
        return delegate.getValueExpression();
    }


    public Object getValue(Context context) {
        return delegate.getValue(context);
    }


    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return delegate.getValueBindingExpressions();
    }


    public String getRendererType() {
        return delegate.getRendererType();
    }


    public boolean isRenderChildren() {
        return delegate.isRenderChildren();
    }


    public String getOnBeforeRenderAction() {
        return delegate.getOnBeforeRenderAction();
    }


    public String getOnAfterRenderAction() {
        return delegate.getOnAfterRenderAction();
    }


    public UIAction getAction() {
        return delegate.getAction();
    }


    public String getLabel() {
        return delegate.getLabel();
    }


    public ValueBindingExpression getRenderExpression() {
        return delegate.getRenderExpression();
    }


    public void setAction(UIAction componentAction) {
        delegate.setAction(componentAction);
    }


    public boolean isRendered(Context context) {
        return delegate.isRendered(context);
    }


    public EntityHolder getEntityHolder() {
        return delegate.getEntityHolder();
    }

    public UIComponent getDelegate() {
        return delegate;
    }
}
