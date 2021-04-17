package es.jcyl.ita.formic.forms.utils;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.UserAction;

/**
 * Utility class to simulate form navigation in tests
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DevFormNav {

    private final android.content.Context context;
    private final MainController mc;

    public DevFormNav(android.content.Context context, MainController mc) {
        this.context = context;
        this.mc = mc;
    }

    public void nav(String formControllerId) {
        nav(formControllerId, null);
    }

    public void nav(String formControllerId, Map<String, Serializable> params) {
        UserAction navAction = UserAction.navigate(formControllerId);
        if (params != null) {
            navAction.setParams(params);
        }
        mc.getRouter().navigate(context, navAction);
        // manually render the view
        mc.renderView(context);
    }

    public void navToEntity(String formControllerId, String entityId) {
        Map<String, Serializable> params = new HashMap<>();
        params.put("entityId", entityId);
        nav(formControllerId, params);
    }

}
