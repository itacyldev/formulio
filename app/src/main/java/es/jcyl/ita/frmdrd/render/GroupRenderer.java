package es.jcyl.ita.frmdrd.render;
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
import android.view.View;


import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.ExecEnvironment;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public interface GroupRenderer {

    void initGroup(Context viewContext, ExecEnvironment env, UIComponent component, View root);

    void addViews(Context viewContext, ExecEnvironment env, UIComponent component, View root, View[] views);

    void endGroup(Context viewContext, ExecEnvironment env, UIComponent component, View root);

}
