package es.jcyl.ita.frmdrd.view;
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

import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.render.GroupRenderer;
import es.jcyl.ita.frmdrd.render.Renderer;
import es.jcyl.ita.frmdrd.render.RendererFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Intermediate class to encapsulate rendering to facilitate testing
 */
public class ViewRenderHelper {

    public View render(Context viewContext, ExecEnvironment env, UIComponent root) {
        String rendererType = root.getRendererType();
        Renderer renderer = this.getRenderer(rendererType);
        // set current form context
        if (root.getParentForm() != null) {
            env.setFormContext(((UIForm) root.getParentForm()).getContext());
        }
        View rootView = renderer.render(viewContext, env, root);
        if (root instanceof UIForm) {
            // configure viewContext
            ((UIForm) root).getContext().setView(rootView);
        }

        // render children if needed
        if (renderer instanceof GroupRenderer) {
            GroupRenderer gRenderer = (GroupRenderer) renderer;
            if (root.isRenderChildren()) {
                // recursively render children components
                gRenderer.initGroup(viewContext, env, root, rootView);
                int numKids = root.getChildren().size();
                View[] views = new View[numKids];
                for (int i = 0; i < numKids; i++) {
                    views[i] = render(viewContext, env, root.getChildren().get(i));
                }
                gRenderer.addViews(viewContext, env, root, rootView, views);
                gRenderer.endGroup(viewContext, env, root, rootView);
            }
        }
        return rootView;
    }


    protected Renderer getRenderer(String rendererType) {
        Renderer renderer = RendererFactory.getInstance().getRenderer(rendererType);
        return renderer;
    }
}
