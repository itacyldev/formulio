package es.jcyl.ita.frmdrd.view;
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
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import es.jcyl.ita.frmdrd.view.converters.ViewValueConverter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Base class to wrapper Android views that represent input fields to provide feature to access and
 * modify the view component (values, styles, focus, etc).
 */
public class InputFieldView extends LinearLayout {

    private View inputView;
    private ViewValueConverter converter;
    /**
     * related UIComponents
     */
    private String formId;
    private String fieldId;

    public InputFieldView(Context context) {
        super(context);
    }

    public InputFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputFieldView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**********************************************/
    /**** Methods to access and modify the view ***/
    /**********************************************/

    public void setValue(Object value) {
        converter.setViewValue(inputView, value);
    }

    public void setValueString(String value) {
        converter.setViewValueAsString(inputView, value);
    }

    public <T> T getValue(Class<T> expectedType) {
        return (T) converter.getValueFromView(inputView, expectedType);
    }

    public String getValueString() {
        return converter.getValueFromViewAsString(inputView);
    }

    public void setFocus(boolean focusable) {
        this.inputView.requestFocus();
    }

    /*************************************/
    /**** GETTERS/SETTERS ***/
    /*************************************/

    public View getInputView() {
        return inputView;
    }

    public void setInputView(View inputView) {
        this.inputView = inputView;
    }

    public ViewValueConverter getConverter() {
        return converter;
    }

    public void setConverter(ViewValueConverter converter) {
        this.converter = converter;
    }

    public String getFormId() {
        return formId;
    }

    public String getFieldId() {
        return fieldId;
    }

    /*******/
    /** Form and field ids are set during the rendering process */
    /*******/
    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}