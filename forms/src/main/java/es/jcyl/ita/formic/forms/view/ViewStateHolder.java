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
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores view state using a FormViewContext as intermediate
 */
public class ViewStateHolder {

    private Map<Integer, Object> state = new HashMap<Integer, Object>();

    public void clear() {
        this.state.clear();
    }

    /**
     * Gets all the inputFields and stores their state in the context "state"
     */
    public void saveState(ViewWidget rootWidget) {
        clear();
        for (WidgetContextHolder holder : rootWidget.getContextHolders()) {
            ViewContext viewContext = holder.getWidgetContext().getViewContext();
            saveState(viewContext);
        }
    }

    /**
     * Gets all the inputFields and stores their state in the context "state"
     */
    public void saveState(ViewContext viewContext) {
        clear();
        for (StatefulWidget widget : viewContext.getStatefulViews()) {
            state.put(widget.getId(), widget.getState());
        }
    }

    /**
     * Restore view state form the context
     */
    public void restoreState(ViewContext viewContext) {
        for (StatefulWidget widget : viewContext.getStatefulViews()) {
            widget.setState(state.get(widget.getId()));
        }
    }

    public void restoreState(ViewWidget rootWidget) {
        for (WidgetContextHolder holder : rootWidget.getContextHolders()) {
            ViewContext viewContext = holder.getWidgetContext().getViewContext();
            restoreState(viewContext);
        }
    }
}
