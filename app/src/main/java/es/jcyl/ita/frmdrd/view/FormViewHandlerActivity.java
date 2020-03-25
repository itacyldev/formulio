package es.jcyl.ita.frmdrd.view;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.View;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import androidx.fragment.app.FragmentActivity;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.render.GroupRenderer;
import es.jcyl.ita.frmdrd.render.Renderer;
import es.jcyl.ita.frmdrd.render.RendererFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class FormViewHandlerActivity extends FragmentActivity {
    protected static final Log LOGGER = LogFactory
            .getLog(FormViewHandlerActivity.class);

    public static ActionMode actionMode;

    protected ContextThemeWrapper themeWrapper;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.themeWrapper = new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Dialog);

        UIComponent root = MainController.getInstance().getViewRoot();
        es.jcyl.ita.frmdrd.context.Context context = buildContext();
        ExecEnvironment env = new ExecEnvironment(context);
        View rootView = doRender(this, env, root);
        setContentView(rootView);
    }

    private es.jcyl.ita.frmdrd.context.Context buildContext() {
        // parameter context
        // form context
        // local context
        BasicContext lifecycleContext = new BasicContext("lifecycle");
        return lifecycleContext;
    }


    private View doRender(Context viewContext, ExecEnvironment env, UIComponent root) {
        String rendererType = root.getRendererType();
        Renderer renderer = this.getRenderer(rendererType);

        View rootView = renderer.render(viewContext, env, root);

        if (renderer instanceof GroupRenderer) {
            GroupRenderer gRenderer = (GroupRenderer) renderer;
            if (root.isRenderChildren()) {
                // recursively render children components
                gRenderer.initGroup(viewContext, env, root, rootView);
                int numKids = root.getChildren().size();
                View[] views = new View[numKids];
                for (int i = 0; i < numKids; i++) {
                    views[i] = doRender(viewContext, env, root.getChildren().get(i));
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

    protected void close() {
        finish();
    }

}