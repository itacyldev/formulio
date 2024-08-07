package es.jcyl.ita.formic.forms.view.toolbar;
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
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.selection.SelectionObserver;

/**
 * Manages visualization of
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FabToolBarManager implements SelectionObserver {

    Map<String, List<UserAction>> registry = new HashMap<String, List<UserAction>>();

    @Override
    public void update(UIComponent component, Object... params) {
        // si el

    }

    private void render() {
        // mostrar botones en función de acciones
    }
}
