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
import android.widget.Button;
import android.widget.EditText;

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Date;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class DateFieldViewConverter implements ViewValueConverter {
    @Override
    public String getValueFromViewAsString(View view, UIComponent component) {
        Button input = (Button) view;
        String viewValue = "";
        if (input.getText() != null) {
            viewValue = input.getText().toString();
        }
        return viewValue;
    }

    @Override
    public <T> T getValueFromView(View view, UIComponent component, Class<T> expectedType) {
        Button input = (Button) view;
        String viewValue = "";
        if (input.getText() != null) {
            viewValue = input.getText().toString();
        }
        Object o = ConvertUtils.convert(viewValue, expectedType);
        return (T) o;
    }

    @Override
    public void setViewValue(View view, UIComponent component, Object value) {
        Button inputField = (Button) view;
        String textValue = (String) ConvertUtils.convert(value, String.class);
        inputField.setText(textValue);
    }

    @Override
    public void setViewValueAsString(View view, UIComponent component, String value) {
        Button inputField = (Button) view;
        inputField.setText(value);
    }
}
