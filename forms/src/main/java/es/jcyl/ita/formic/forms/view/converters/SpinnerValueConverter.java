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

import android.widget.Spinner;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.option.UIOptionsAdapterHelper;
import es.jcyl.ita.formic.forms.components.select.SelectRenderer;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class SpinnerValueConverter implements ViewValueConverter<Spinner> {

    @Override
    public Object getValueFromView(Spinner view) {
        if (!isSelected(view)) {
            return null;
        } else {
            UIOption selectedItem = (UIOption) view.getSelectedItem();
            return (selectedItem == null) ? null : selectedItem.getValue();
        }
    }

    @Override
    public void setViewValue(Spinner view, Object value) {
        view.setSelected(false);
        String strValue = (String) ConvertUtils.convert(value, String.class);
        int pos = UIOptionsAdapterHelper.getSelectionOption(view.getAdapter(), strValue);
        view.setSelection(pos);
    }

    /**
     * Android spinners don't have an empyt value, there's and additional option to give this
     * possibility. This methods checks if selected option is the emtpy-value one.
     *
     * @param spinner
     * @return
     */
    private boolean isSelected(Spinner spinner) {
        return !(spinner.getSelectedItem() instanceof SelectRenderer.EmptyOption);
    }
}