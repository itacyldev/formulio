package es.jcyl.ita.frmdrd.context.impl;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.view.widget.InputWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores view state using a FormViewContext as intermediate
 */
public class ViewStateHolder {

    private Map<String, Object> state = new HashMap<String, Object>();

    public void clear() {
        this.state.clear();
    }

    /**
     * Gets all the inputFields and stores their state in the context "state"
     */
    public void saveState(FormViewContext viewContext) {
        clear();
        for (InputWidget fieldView : viewContext.getInputFields()) {
            if(fieldView.isVisible()){
                state.put(fieldView.getInputId(), fieldView.getValue());
            }
        }
    }

    /**
     * Restore view state form the context
     */
    public void restoreState(FormViewContext viewContext) {
        for (InputWidget fieldView : viewContext.getInputFields()) {
            if(fieldView.isVisible()){
                String fieldId = fieldView.getInputId();
                fieldView.setValue(state.get(fieldId));
            }
        }

    }
}
