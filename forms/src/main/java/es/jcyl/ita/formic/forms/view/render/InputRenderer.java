package es.jcyl.ita.formic.forms.view.render;
/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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
/**
 * Extends base renderer to define a common creation flow form UIInput components.
 * I, represents the AndroidView used as base user input, the component attached to InputFieldView
 * that will be used to get and set data from/to the view.
 * <p/>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;


public abstract class InputRenderer<C extends UIInputComponent, I extends View>
        extends AbstractRenderer<C, InputWidget<C, I>> {

    @Override
    protected void composeWidget(RenderingEnv env, InputWidget<C, I> widget) {
        C component = widget.getComponent();
        // find and attach label and input
        widget.setConverter(component.getConverter());
        if (component.getParentForm() != null) {
            widget.setFormId(component.getParentForm().getId());
        }
        widget.setInputId(component.getId());

        // find label and setup
        TextView fieldLabel = ViewHelper.findViewAndSetId(widget, getLabelViewId(),
                TextView.class);
        setLabel(env, fieldLabel, widget.getComponent());

        // get input view and set Tag and Value
        I inputView = (I) ViewHelper.findViewAndSetId(widget, getInputViewId());
        if (inputView == null) {
            Resources res = Config.getInstance().getResources();
            DevConsole.error(String.format("There's an error in the '%s' class, the method  " +
                            "getInputViewId() must return an existing component in the widget " +
                            "layout. Make sure there's a View with the id [%s] in the file [%s].",
                    this.getClass().getName(), res.getResourceName(getInputViewId()),
                    res.getResourceName(getWidgetLayoutId())));
        }
        widget.setInputView(inputView);
        setInputView(env, widget);

        // implement specific component rendering
        composeInputView(env, widget);

        // set value and error messages
        setValueInView(env, widget);
        setMessages(env, widget);
    }

    protected void setValueInView(RenderingEnv env, InputWidget<C, I> widget) {
        String value = getComponentValue(env, widget.getComponent(), String.class);
        widget.getConverter().setViewValue(widget.getInputView(), value);
    }

    protected int getLabelViewId() {
        return R.id.label_view;
    }

    protected int getInputViewId() {
        return R.id.input_view;
    }


    /**
     * Calculates the String to tag the view component that stores the user input
     * getInputTag
     *
     * @return
     */
    protected String getInputTag(C c) {
        return getWidgetViewTag(c) + ">input";
    }

    protected void setLabel(RenderingEnv env, TextView labelView, C component) {
        labelView.setTag("label_" + component.getId());
        String labelComponent = (component.isMandatory()) ?
                component.getLabel() + " *"
                : component.getLabel();
        labelView.setText(labelComponent);
    }

    protected void setInputView(RenderingEnv env, InputWidget<C, I> widget) {
        // link inputView with baseView
        C component = widget.getComponent();
        I inputView = widget.getInputView();
        String inputTag = getInputTag(component);
        inputView.setTag(inputTag);
        inputView.setEnabled(!widget.getComponent().isReadOnly());
    }

    /************************************/
    /** Extension points **/
    /************************************/

    protected abstract void setMessages(RenderingEnv env, InputWidget<C, I> widget);

    protected abstract void composeInputView(RenderingEnv env, InputWidget<C, I> widget);

}
