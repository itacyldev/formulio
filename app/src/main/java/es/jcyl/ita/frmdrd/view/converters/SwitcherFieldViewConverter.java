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
import android.widget.Switch;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class SwitcherFieldViewConverter implements ViewValueConverter {
    @Override
    public String getValueFromViewAsString(View view, UIComponent component) {
        Switch inputField = (Switch) view;
        Boolean viewValue = inputField.isChecked();
        return viewValue.toString();
    }


    @Override
    public <T> T getValueFromView(View view, UIComponent component, Class<T> expectedType) {
        Switch inputField = (Switch) view;
        Boolean viewValue = inputField.isChecked();
        Object o = ConvertUtils.convert(viewValue, expectedType);
        return (T) o;
    }

    @Override
    public void setViewValue(View view, UIComponent component, Object value) {
        Switch inputField = (Switch) view;
        Boolean boolValue = (Boolean) ConvertUtils.convert(value, Boolean.class);
        inputField.setChecked(boolValue);
    }

    @Override
    public void setViewValueAsString(View view, UIComponent component, String value) {
        setViewValue(view,component, value);
    }

}
