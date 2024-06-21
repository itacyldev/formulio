package es.jcyl.ita.formic.forms.components;
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
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class EntityHolderProxy implements EntityHolder {

    private final UIComponent delegate;
    private Entity entity;

    public EntityHolderProxy(UIComponent component) {
        this(component, null);
    }

    public EntityHolderProxy(UIComponent component, Entity entity) {
        this.delegate = component;
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setId(String id) {
        delegate.setId(id);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getAbsoluteId() {
        return delegate.getAbsoluteId();
    }

    @Override
    public void setChildren(List<UIComponent> children) {
        delegate.setChildren(children);
    }

    @Override
    public void setChildren(UIComponent[] children) {
        delegate.setChildren(children);
    }

    @Override
    public void addChild(UIComponent... lstChildren) {
        delegate.addChild(lstChildren);
    }

    @Override
    public boolean hasChildren() {
        return delegate.hasChildren();
    }

    @Override
    public UIComponent[] getChildren() {
        return delegate.getChildren();
    }

    @Override
    public UIComponent getChildById(String id) {
        return delegate.getChildById(id);
    }

    @Override
    public void setParent(UIComponent parent) {
        delegate.setParent(parent);
    }

    @Override
    public UIComponent getParent() {
        return delegate.getParent();
    }

    @Override
    public UIForm getParentForm() {
        return delegate.getParentForm();
    }

    @Override
    public void setRoot(UIView root) {
        delegate.setRoot(root);
    }

    @Override
    public UIView getRoot() {
        return delegate.getRoot();
    }

    @Override
    public void setValueExpression(ValueBindingExpression valueBindingExpression) {
        delegate.setValueExpression(valueBindingExpression);
    }

    @Override
    public ValueBindingExpression getValueExpression() {
        return delegate.getValueExpression();
    }

    @Override
    public Object getValue(Context context) {
        return delegate.getValue(context);
    }

    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        return delegate.getValueBindingExpressions();
    }

    @Override
    public String getRendererType() {
        return delegate.getRendererType();
    }

    @Override
    public boolean isRenderChildren() {
        return delegate.isRenderChildren();
    }

    @Override
    public String getOnBeforeRenderAction() {
        return delegate.getOnBeforeRenderAction();
    }

    @Override
    public String getOnAfterRenderAction() {
        return getOnAfterRenderAction();
    }

    @Override
    public UIAction getAction() {
        return delegate.getAction();
    }

    @Override
    public String getLabel() {
        return delegate.getLabel();
    }

    @Override
    public ValueBindingExpression getRenderExpression() {
        return delegate.getRenderExpression();
    }

    @Override
    public void setAction(UIAction componentAction) {
        delegate.setAction(componentAction);
    }

    @Override
    public boolean isRendered(Context context) {
        return delegate.isRendered(context);
    }

    @Override
    public ValueBindingExpression getPlaceHolder() {
        return null;
    }

    @Override
    public EntityHolder getEntityHolder() {
        return delegate.getEntityHolder();
    }

    public UIComponent getDelegate() {
        return delegate;
    }
}
