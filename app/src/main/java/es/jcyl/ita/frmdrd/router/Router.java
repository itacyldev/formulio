package es.jcyl.ita.frmdrd.router;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.MainController;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Provides funcionality to navegate among views.
 */

public class Router {

    MainController mc;
    private List<State> memento;

    public Router(MainController mainController) {
        this.mc = mainController;
        this.memento = new ArrayList<>();
    }

    public void navigateList(android.content.Context context, String formId, Map<String, Serializable> params) {
        recordHistory(formId, "list", params);
        mc.navigate(context, formId, "list", params);
    }

    public void navigateEdit(android.content.Context context, String formId, Map<String, Serializable> params) {
        recordHistory(formId, "edit", params);
        mc.navigate(context, formId, "edit", params);
    }

    public void back(android.content.Context context) {
        State lastState = popHistory();
        if (lastState != null) {
            doNavigate(context, lastState);
        }
    }

    private void doNavigate(Context context, State state) {
        mc.navigate(context, state.formId, state.navType, state.params);
    }

    /**
     * Removes the last element of the history and gives the last one in the list
     *
     * @return
     */
    private State popHistory() {
        if (this.memento == null && this.memento.size() < 2) {
            return null;
        }
        // remove last element and get the previous one
        int pos = this.memento.size() - 1;
        this.memento.remove(pos);
        return this.memento.get(pos - 1);
    }


    private void recordHistory(String formId, String navType, Map<String, Serializable> params) {
        this.memento.add(new State(formId, navType, params));
    }

    public class State {
        String formId;
        Map<String, Serializable> params;
        String navType;

        public State(String formId, String navType, Map<String, Serializable> params) {
            this.formId = formId;
            this.navType = navType;
            this.params = params;
        }

        public String toString() {
            return String.format("%s/%s - %s", formId, navType, params);
        }
    }
}
