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

import android.widget.TextView;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Class to
 */
public class TextViewConverter implements ViewValueConverter<TextView> {
    @Override
    public String getValueFromViewAsString(TextView view) {
        String viewValue = "";
        if (view.getText() != null) {
            viewValue = view.getText().toString();
        }
        return viewValue;
    }

    @Override
    public <C> C getValueFromView(TextView view, Class<C> expectedType) {
        String viewValue = "";
        if (view.getText() != null) {
            viewValue = view.getText().toString();
        }
        Object o = ConvertUtils.convert(viewValue, expectedType);
        return (C) o;
    }


    @Override
    public void setViewValue(TextView view, Object value) {
        String textValue = (String) ConvertUtils.convert(value, String.class);
        view.setText(textValue);
    }

    @Override
    public void setViewValueAsString(TextView view, String value) {
        setViewValue(view, value);
    }
}
