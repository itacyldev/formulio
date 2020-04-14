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

import android.widget.Adapter;
import android.widget.AutoCompleteTextView;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.select.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteValueConverter implements ViewValueConverter<AutoCompleteTextView> {

    @Override
    public String getValueFromViewAsString(AutoCompleteTextView view) {
        int position = view.getListSelection();
        if (position == -1) {
            return null;
        }
        return ((UIOption) view.getAdapter().getItem(position)).getValue();
    }

    @Override
    public <C> C getValueFromView(AutoCompleteTextView view, Class<C> expectedType) {
        String value = getValueFromViewAsString(view);
        if (value == null) {
            return null;
        } else {
            return (C) ConvertUtils.convert(expectedType);
        }
    }

    @Override
    public void setViewValue(AutoCompleteTextView view, Object value) {
        String strValue = (String) ConvertUtils.convert(value, String.class);
        setViewValueAsString(view, strValue);
    }

    @Override
    public void setViewValueAsString(AutoCompleteTextView view, String value) {
        if (value == null) {
            view.setSelection(0); // empty option
        } else {
            // find the selected option
            Adapter adapter = view.getAdapter();
            int nOptions = adapter.getCount();
            view.setSelected(false);
            // Empty option is added at position 0
            for (int i = 1; i < nOptions; i++) {
                UIOption uiOption = (UIOption) adapter.getItem(i);
                if (uiOption.getValue().equalsIgnoreCase(value)) {
                    view.setSelection(i);
                    return;
                }
            }
            view.setSelection(0); // no value found, empty option
        }
    }
}
