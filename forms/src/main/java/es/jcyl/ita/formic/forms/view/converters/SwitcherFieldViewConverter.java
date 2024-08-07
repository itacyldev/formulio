package es.jcyl.ita.formic.forms.view.converters;
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
    public Object getValueFromView(Switch view) {
        Boolean viewValue = view.isChecked();
        return viewValue;
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
    public void setPattern(String pattern) {

    }

    @Override
    public void setType(String type) {

    }
}
