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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteStaticValueConverter implements ViewValueConverter<AutoCompleteView> {

    @Override
    public String getValueFromViewAsString(AutoCompleteView view) {
        return getValueFromView(view, String.class);
    }

    @Override
    public <C> C getValueFromView(AutoCompleteView view, Class<C> expectedType) {
        Object value = view.getValue();
        if (value == null) {
            return null;
        } else {
            return (C) ConvertUtils.convert(expectedType);
        }
    }

    @Override
    public void setViewValue(AutoCompleteView view, Object value) {
        String strValue = (String) ConvertUtils.convert(value, String.class);
        setViewValueAsString(view, strValue);
    }

    @Override
    public void setViewValueAsString(AutoCompleteView view, String value) {
        if (value == null) {
            view.setSelection(-1); // empty option
        } else {
            // find the selected option
            UIOption[] options = view.getOptions();
            if (options == null) {
                view.setSelection(-1); // no value found, empty option
                return;
            }
            for (int i = 0; i < options.length; i++) {
                UIOption uiOption = options[i];
                if (uiOption.getValue().equalsIgnoreCase(value)) {
                    view.setSelection(i);
                    return;
                }
            }
            view.setSelection(-1); // no value found, empty option
        }
    }
}
