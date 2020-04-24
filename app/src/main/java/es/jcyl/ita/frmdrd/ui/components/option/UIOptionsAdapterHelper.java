package es.jcyl.ita.frmdrd.ui.components.select;
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

import android.content.Context;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.ui.components.option.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIOptionsAdapterHelper {
    private static final SelectRenderer.EmptyOption EMPTY_OPTION = new SelectRenderer.EmptyOption(null, null);


    public static int getSelectionOption(Adapter adapter, String value) {
        int nOptions = adapter.getCount();

        if (nOptions == 0) {
            // no options found, clears list with INVALID_POSITION
            return -1;
        } else {
            // check if there's null option at the beginning of option array
            int startPos = (adapter.getItem(0) instanceof SelectRenderer.EmptyOption) ? 1 : 0;
            for (int i = startPos; i < nOptions; i++) {
                UIOption uiOption = (UIOption) adapter.getItem(i);
                if (uiOption.getValue().equalsIgnoreCase(value)) {
                    return i;
                }
            }
            // no option found that matches "value" given parameter, set default option
            if (startPos == 1) {
                // there's null option, set it
                return 0;
            } else {
                // clears list with INVALID_POSITION
                return -1;
            }
        }
    }

    public ArrayAdapter createArrayAdapterFromOptions(Context context, UISelect component) {
        // create items from options
        List<UIOption> items = new ArrayList<UIOption>();
        // empty value option
        if (component.isHasNullOption()) {
            items.add(EMPTY_OPTION);
        }
        if (component.getOptions() != null) {
            for (UIOption option : component.getOptions()) {
                items.add(option);
            }
        }
        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = new ArrayAdapter<UIOption>(context,
                android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }
}
