package es.jcyl.ita.formic.forms.view.widget;
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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;

/**
 * Base class to wrapper Android views that represent input fields to provide feature to access and
 * modify the view component (values, styles, focus, etc).
 * <p>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class InputWidget<C extends UIInputComponent, V extends View> extends Widget<C> implements StatefulWidget{

    private ViewValueConverter converter;
    private V inputView;

    /**
     * related UIComponents
     */
    private String formId;
    private String inputId;

    public InputWidget(Context context) {
        super(context);
    }

    public InputWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**********************************************/
    /**** Methods to access and modify the view ***/
    /**********************************************/

    public void setValue(Object value) {
        if (inputView == null) return; // view hasn't been rendered
        converter.setViewValue(inputView, value);
    }

    public Object getValue() {
        return (inputView == null) ? null : converter.getValueFromView(inputView);
    }

    /**
     * get/setState are used during saving process to store component state before the validation
     * takes places, so the view state (user most recent input) can be restored in case one of the
     * field validation rules is not met.
     *
     * @param value
     */
    public void setState(Object value) {
        setValue(value);
    }

    public Object getState() {
        return getValue();
    }

    /**
     * method called after related elements are bound to current InputFieldView.
     */
    public void setup() {
    }

    public void setFocus(boolean focusable) {
        this.inputView.requestFocus();
    }

    public boolean isVisible() {
        return this.inputView != null;
    }
    /*************************************/
    /**** GETTERS/SETTERS ***/
    /*************************************/


    public ViewValueConverter getConverter() {
        return converter;
    }

    public void setConverter(ViewValueConverter converter) {
        this.converter = converter;
    }

    public String getFormId() {
        return formId;
    }

    public String getInputId() {
        return inputId;
    }

    /******/
    /** Form and field ids are set during the rendering process */
    /*******/
    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }


    public V getInputView() {
        return inputView;
    }

    public void setInputView(V inputView) {
        this.inputView = inputView;
    }
}
