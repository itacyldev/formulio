package es.jcyl.ita.formic.forms.view;
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

import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;

/**
 * Stores view state of each WidgetContextHolder. For each holder, this object keeps a map referenced
 * by the id of the holder component (typically a form or a itemlist), and stores the state of the
 * hodler's registered views (Statefullviews, tytpically inputs).
 * It is used to store and restore the view state after each user interaction to restore the view elements.
 * <p>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewStateHolder {

    /**
     * A map for each WidgetContextHolder in which is stored the the state of each StateFull item of
     * the context
     */
    private Map<String, Map<String, Object>> state = new HashMap();

    public void clear() {
        this.state.clear();
    }

    /**
     * Gets all the inputFields and stores their state in the context "state"
     */
    public void saveState(ViewWidget rootWidget) {
        clear();
        if (rootWidget.getContextHolders() != null) {
            for (WidgetContextHolder holder : rootWidget.getContextHolders()) {
                saveState(holder);
            }
        }
    }

    /**
     * Gets all the inputFields and stores their state in the context "state"
     */
    private void saveState(WidgetContextHolder holder) {
        String holderId = holder.getHolderId();  // unique formId, dataitemId, ...
        Map<String, Object> holderState = this.state.get(holderId);
        if (holderState == null) {
            holderState = new HashMap<>();
            this.state.put(holderId, holderState);
        }
        ViewContext viewContext = holder.getWidgetContext().getViewContext();
        for (StatefulWidget widget : viewContext.getStatefulViews()) {
            holderState.put(widget.getComponent().getId(), widget.getState());
        }
    }

    /**
     * Restore view state form the context
     */
    private void restoreState(WidgetContextHolder holder, boolean partial) {
        String holderId = holder.getHolderId(); // unique formId, dataitemId, ...
        Map<String, Object> holderState = this.state.get(holderId);
        if (holderState != null) {
            ViewContext viewContext = holder.getWidgetContext().getViewContext();
            for (StatefulWidget widget : viewContext.getStatefulViews()) {
                if (!partial || partial && widget.allowsPartialRestore()) {
                    widget.setState(holderState.get(widget.getComponent().getId()));
                }
            }
        }
    }

    public void restoreState(ViewWidget rootWidget) {
        if (rootWidget.getContextHolders() != null) {
            for (WidgetContextHolder holder : rootWidget.getContextHolders()) {
                restoreState(holder, false);
            }
        }
    }

    /**
     * Limite the restoration to those components that has the attribute "AllowsPartialRestore" = true
     *
     * @param rootWidget
     */
    public void restorePartialState(ViewWidget rootWidget) {
        if (rootWidget.getContextHolders() != null) {
            for (WidgetContextHolder holder : rootWidget.getContextHolders()) {
                restoreState(holder, true);
            }
        }
    }

}
