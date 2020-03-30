package es.jcyl.ita.frmdrd.view.converters;
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
import android.widget.EditText;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class TextFieldViewConverter implements ViewValueConverter {
    @Override
    public String getValueFromViewAsString(View view, UIComponent component) {
        EditText inputField = (EditText) view;
        String viewValue = "";
        if (inputField.getText() != null) {
            viewValue = inputField.getText().toString();
        }
        return viewValue;
    }

    @Override
    public <T> T getValueFromView(View view, UIComponent component, Class<T> expectedType) {
        EditText inputField = (EditText) view;
        String viewValue = "";
        if (inputField.getText() != null) {
            viewValue = inputField.getText().toString();
        }
        Object o = ConvertUtils.convert(viewValue, expectedType);
        return (T) o;
    }

    @Override
    public void setViewValue(View view, UIComponent component, Object value) {
        EditText inputField = (EditText) view;
        String textValue = (String) ConvertUtils.convert(value, String.class);
        inputField.setText(textValue);
    }

    @Override
    public void setViewValueAsString(View view, UIComponent component, String value) {
        setViewValue(view, component, value);
    }
}
