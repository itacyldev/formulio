package es.jcyl.ita.formic.forms.router;
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

import android.app.Activity;
import android.content.Context;

import org.mini2Dx.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.DevConsole;

import static es.jcyl.ita.formic.forms.config.DevConsole.debug;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Provides funcionality to navegate among views.
 */

public class Router {

    MainController mc;
    private List<State> memento;
    private State current;
    private Activity currentActivity;
    private String[] currentViewMessages;

    public Router(MainController mainController) {
        this.mc = mainController;
        this.memento = new ArrayList<>();
    }

    public void navigate(UserAction action, String... messages) {
        this.currentViewMessages = messages;
        if ("back".equalsIgnoreCase(action.getRoute())) {
            this.back(action.getViewContext());
        } else {
            mc.navigate(action.getViewContext(), action.getRoute(), action.getParams());
            if (action.isRegisterInHistory()) {
                recordHistory(action.getRoute(), action.getParams());
            }
        }
    }

    public void clearGlobalMessages() {
        this.currentViewMessages = null;
    }

    public String[] getGlobalMessages() {
        return currentViewMessages;
    }

    public void back(android.content.Context context) {
        back(context, null);
    }

    public void back(android.content.Context context, String[] messages) {
        this.currentViewMessages = messages;
        State lastState = popHistory();
        if (lastState != null) {
            doNavigate(context, lastState);
        }
    }

    private void doNavigate(Context context, State state) {
        mc.navigate(context, state.formId, state.params);
    }

    /**
     * Removes the last element of the history and gives the last one in the list
     *
     * @return
     */
    private State popHistory() {
        this.current = null;
        this.currentActivity.finish();
        this.currentActivity = null;
        if (hasHistory()) {
            // get last state from history
            int lastPos = this.memento.size() - 1;
            this.current = this.memento.remove(lastPos);
        }
        if (DevConsole.isDebugEnabled()) {
            debugHistory();
        }
        return current;
    }

    /**
     * If there're still navigation records in the history.
     *
     * @return
     */
    private boolean hasHistory() {
        return (this.memento != null && this.memento.size() > 0);
    }


    private void recordHistory(String formId, Map<String, Serializable> params) {
        if (current != null) {
            this.memento.add(current);
        }
        this.current = new State(formId, params);
        if (DevConsole.isDebugEnabled()) {
            debugHistory();
        }
    }

    public void registerActivity(Activity activity) {
        if (currentActivity != null) {
            // destroy last activity
            this.currentActivity.finish();
        }
        this.currentActivity = activity;
    }

    /**
     * Removes from history the number of given steps
     *
     * @param steps
     */
    public void popHistory(int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("Negative number while trying to pop history from router");
        }
        if (steps == 0) {
            return;// nothing to do
        }
        for(int i=0;i<steps;i++){
            this.popHistory();
        }
    }

    public class State {
        String formId;
        Map<String, Serializable> params;

        public State(String formId, Map<String, Serializable> params) {
            this.formId = formId;
            this.params = params;
        }

        public String toString() {
            return String.format("%s - %s", formId, params);
        }
    }

    private void debugHistory() {
        debug("----- Router -----");
        if (CollectionUtils.isNotEmpty(memento)) {
            for (State state : memento) {
                debug(state.toString());
            }
        }
        debug("Current: " + this.current);
        debug("------------------");
    }
}