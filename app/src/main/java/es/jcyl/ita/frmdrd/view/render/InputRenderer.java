package es.jcyl.ita.frmdrd.view.render;
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
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Extends base renderer to define a common creation flow form UIInput components.
 * I, represents the AndroidView used as base user input, the component attached to InputFieldView
 * that will be used to get and set data from/to the view.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.RandomUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;


public abstract class InputRenderer<I extends View, C extends UIInputComponent>
        extends BaseRenderer<InputFieldView, C> {

    protected static final Object EMPTY_STRING = "";

    @Override
    protected InputFieldView createBaseView(RenderingEnv env, C component) {
//        ViewGroup baseView = ViewHelper.inflate(env.getViewContext(),
//                getComponentLayout(), LinearLayout.class);

        ViewGroup baseView = (ViewGroup) LayoutInflater
                .from(env.getViewContext())
                .inflate(getComponentLayout(), new LinearLayout(env.getViewContext()), true);

        baseView.setId(RandomUtils.nextInt());
        return createInputFieldView(env.getViewContext(), baseView, component);
    }


    protected InputFieldView createInputFieldView(Context viewContext, View baseView, C component) {
//        InputFieldView fieldView = (InputFieldView) View.inflate(viewContext,
//                R.layout.input_field_view, null);
        InputFieldView fieldView = (InputFieldView) LayoutInflater
                .from(viewContext)
                .inflate(R.layout.input_field_view, new InputFieldView(viewContext),true);


        fieldView.setComponent(component);
        fieldView.setConverter(component.getConverter());
        fieldView.setTag(getBaseViewTag(component));
        fieldView.addView(baseView);
        if (component.getParentForm() != null) {
            fieldView.setFormId(component.getParentForm().getId());
        }
        fieldView.setFieldId(component.getId());

        return fieldView;
    }

    @Override
    protected void setupView(RenderingEnv env, InputFieldView baseView, C component) {
        // get label and set Tag
        TextView fieldLabel = ViewHelper.findViewAndSetId(baseView, getLabelViewId(),
                TextView.class);
        setupLabel(env, fieldLabel, component);

        // get input view and set Tag and Value
        I inputView = (I) ViewHelper.findViewAndSetId(baseView, getInputViewId());
        setupInputView(env, baseView, inputView, component);
        composeView(env, baseView, component);

        // set value and error messages
        setValue(env, baseView, component, inputView);
        setMessages(env, baseView, component);
    }

    private void setValue(RenderingEnv env, InputFieldView baseView, C component, I inputView) {
        String value = getValue(component, env, String.class);
        baseView.getConverter().setViewValue(inputView, value);
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
        return getBaseViewTag(c) + ">input";
    }

    protected void setupLabel(RenderingEnv env, TextView labelView, C component) {
        labelView.setTag("label_" + component.getId());
        labelView.setText(component.getLabel());
    }

    protected void setupInputView(RenderingEnv env, InputFieldView<I> baseView, I inputView, C component) {
        // link inputView with baseView
        baseView.setInputView(inputView);
        inputView.setTag(getInputTag(component));
        // set component value using converter
        inputView.setEnabled(!component.isReadOnly());
    }

    /************************************/
    /** Extension points **/
    /************************************/

    protected abstract int getComponentLayout();

    protected abstract void setMessages(RenderingEnv env, InputFieldView<I> baseView, C component);

    protected abstract void composeView(RenderingEnv env, InputFieldView<I> baseView, C component);

}
