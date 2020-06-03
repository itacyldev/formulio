package es.jcyl.ita.frmdrd.ui.components.view;

import android.view.View;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.BaseGroupRenderer;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

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
public class UIViewRenderer extends BaseGroupRenderer<UIView> implements GroupRenderer<UIView> {

    protected ViewGroup createBaseView(RenderingEnv env, UIView component) {
        return ViewHelper.inflate(env.getViewContext(), R.layout.component_view, ViewGroup.class);
    }


    @Override
    protected void setupView(RenderingEnv env, ViewGroup baseView, UIView component) {

    }


    @Override
    public void addViews(RenderingEnv env, UIView component, ViewGroup root, View[] views) {
        for (View view : views) {
            root.addView(view);
        }
    }
}