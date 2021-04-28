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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface UIComponent {

    void setId(String id);

    String getId();

    String getAbsoluteId();

    void setChildren(List<UIComponent> children);

    void setChildren(UIComponent[] children);

    void addChild(UIComponent... lstChildren);

    boolean hasChildren();

    UIComponent[] getChildren();

    UIComponent getChildById(String id);

    void setParent(UIComponent parent);

    UIComponent getParent();

    UIForm getParentForm();

    void setRoot(UIView root);

    UIView getRoot();

    void setValueExpression(ValueBindingExpression valueBindingExpression);

    ValueBindingExpression getValueExpression();

    Object getValue(Context context);

    Set<ValueBindingExpression> getValueBindingExpressions();

    String getRendererType();

    boolean isRenderChildren();

    String getOnBeforeRenderAction();

    String getOnAfterRenderAction();

    UIAction getAction();

    String getLabel();

    ValueBindingExpression getRenderExpression();

    void setAction(UIAction componentAction);

    boolean isRendered(Context context);

    /**
     * Returns the first UIComponent capable of holding an Entity, the corresponding widget
     * will be a WidgetContextHolder
     *
     * @return
     */
    EntityHolder getEntityHolder();
}