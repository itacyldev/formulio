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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.formic.forms.components.option.UIOptionsAdapterHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteStaticValueConverter implements ViewValueConverter<AutoCompleteView> {

    @Override
    public Object getValueFromView(AutoCompleteView view) {
        return view.getValue();
    }

    @Override
    public void setViewValue(AutoCompleteView view, Object value) {
        String strValue = (String) ConvertUtils.convert(value, String.class);
        view.setText(strValue);
        int pos = UIOptionsAdapterHelper.getSelectionOption(view.getAdapter(), strValue);
        view.setSelection(pos);
    }

}
