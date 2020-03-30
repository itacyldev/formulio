package es.jcyl.ita.frmdrd.ui.components.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.render.BaseRenderer;
import es.jcyl.ita.frmdrd.render.GroupRenderer;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ViewRenderer extends BaseRenderer implements GroupRenderer {


    protected View createBaseView(Context viewContext, ExecEnvironment env, UIComponent component) {
        // TODO: provide different layout implementors
        return View.inflate(viewContext, R.layout.view_layout, null);
    }

    @Override
    protected void setupView(View baseView, ExecEnvironment env, UIComponent component) {

    }

    @Override
    public void initGroup(Context viewContext, ExecEnvironment env, UIComponent component, View root) {
    }

    @Override
    public void addViews(Context viewContext, ExecEnvironment env, UIComponent component, View root, View[] views) {
        for (View view : views) {
            ((ViewGroup) root).addView(view);
        }
    }

    @Override
    public void endGroup(Context viewContext, ExecEnvironment env, UIComponent component, View root) {
    }

}