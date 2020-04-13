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

import android.widget.Switch;

import org.mini2Dx.beanutils.ConvertUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class SwitcherFieldViewConverter implements ViewValueConverter<Switch> {

    @Override
    public String getValueFromViewAsString(Switch view) {
        Switch inputField = (Switch) view;
        Boolean viewValue = inputField.isChecked();
        return viewValue.toString();
    }

    @Override
    public <C> C getValueFromView(Switch view, Class<C> expectedType) {
        Boolean viewValue = view.isChecked();
        Object o = ConvertUtils.convert(viewValue, expectedType);
        return (C) o;
    }

    @Override
    public void setViewValue(Switch view, Object value) {
        Boolean boolValue = (Boolean) ConvertUtils.convert(value, Boolean.class);
        if (boolValue == null) {
            view.setChecked(false);
        } else {
            view.setChecked(boolValue);
        }
    }

    @Override
    public void setViewValueAsString(Switch view, String value) {
        setViewValue(view, value);
    }
}
