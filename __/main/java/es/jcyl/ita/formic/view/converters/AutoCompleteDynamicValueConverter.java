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

import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AutoCompleteDynamicValueConverter extends AutoCompleteStaticValueConverter {

    @Override
    public void setViewValue(AutoCompleteView view, Object value) {
        view.setValue(value);
    }

//    @Override
    public void setViewValueAsString(AutoCompleteView view, String value) {
        view.setValue(value);
//        view.setText(value);
//        int pos = UIOptionsAdapterHelper.getSelectionOption(view.getAdapter(), value);
//        view.setSelection(pos);
//
//        if (value == null) {
//            view.setSelection(0); // empty option
//        } else {
//            // find the selected option
//            Adapter adapter = view.getAdapter();
//            int nOptions = adapter.getCount();
//            view.setSelected(false);
//            // Empty option is added at position 0
//            for (int i = 1; i < nOptions; i++) {
//                UIOption uiOption = (UIOption) adapter.getItem(i);
//                if (uiOption.getValue().equalsIgnoreCase(value)) {
//                    view.setSelection(i);
//                    return;
//                }
//            }
//            view.setSelection(0); // no value found, empty option
//        }
    }
}
