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

import android.widget.RadioGroup;

import es.jcyl.ita.formic.forms.components.radio.RadioButtonWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class RadioValueConverter implements ViewValueConverter<RadioGroup> {

    @Override
    public Object getValueFromView(RadioGroup view) {
        int buttonPosId = view.getCheckedRadioButtonId();
        if (buttonPosId == -1) {
            return null;
        } else {
            RadioButtonWidget radioBtn = view.findViewById(buttonPosId);
            return radioBtn.getOption().getValue();
        }
    }

    @Override
    public void setViewValue(RadioGroup view, Object value) {
        // iterate over the options and set as checked the one with the given value
        for (int index = 0; index < view.getChildCount(); index++) {
            RadioButtonWidget option = (RadioButtonWidget) view.getChildAt(index);
            if (option.getOption().getValue().equals(value)) {
                option.setChecked(true);
            } else {
                option.setChecked(false);
            }
        }
    }

}
