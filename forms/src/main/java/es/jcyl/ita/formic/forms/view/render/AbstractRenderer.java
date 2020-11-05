package es.jcyl.ita.formic.forms.view.render;
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

import android.view.View;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.RandomUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class AbstractRenderer<C extends UIComponent, W extends Widget<C>>
        implements Renderer<C> {

    public final Widget<C> render(RenderingEnv env, C component) {
        W widget = createWidget(env, component);
        // check render condition
        boolean isRendered = component.isRendered(env.getContext());
        widget.setVisibility(isRendered ? View.VISIBLE : View.GONE);
        if (!isRendered) {
            return widget;
        }
        composeWidget(env, widget);
        setupWidget(env, widget);
        return widget;
    }

    /**
     * Provides the resource id to be inflated to get the widget instance.
     *
     * @return
     */
    protected abstract int getWidgetLayoutId(C component);

    /**
     * Create a base view from context and component information to view used as placeholder in the form view
     *
     * @param env
     * @param component
     * @return
     */
    protected W createWidget(RenderingEnv env, C component) {
        Widget widget = ViewHelper.inflate(env.getViewContext(), getWidgetLayoutId(component), Widget.class);
        // set unique id and tag
        widget.setId(RandomUtils.nextInt());
        widget.setTag(getWidgetViewTag(component));
        widget.setComponent(component);
        return (W) widget;
    }

    protected abstract void composeWidget(RenderingEnv env, W widget);

    /**
     * Configure widget after creation.
     *
     * @param env
     * @param widget
     */
    protected void setupWidget(RenderingEnv env, W widget) {
        widget.setup(env);
    }

    /**
     * Calculates the tag for the GroupView component that contains all the input, so when a partial
     * re-render is applied, the hole component can be easily found in the view.
     * The base view will be tagged as form:id and the input view below this as form:id>input
     *
     * @return
     */
    protected String getWidgetViewTag(C c) {
        UIForm form = c.getParentForm();
        String formId = (form == null) ? "root" : form.getId();
        return formId + ":" + c.getId();
    }

    /**
     * Tries to retrieve the component value first accessing the form context and then using
     * global context
     *
     * @param component
     * @param env
     * @return
     */
    protected <T> T getComponentValue(RenderingEnv env, C component, @Nullable Class<T> clazz) {
        Object value = component.getValue(env.getContext());
        if (value == null) {
            return handleNullValue(component);
        }
        if (clazz == null) {
            return (T) value;
        } else {
            return (T) ConvertUtils.convert(value, clazz);
        }
    }

    protected <T> T handleNullValue(C component) {
        return null;
    }

}
