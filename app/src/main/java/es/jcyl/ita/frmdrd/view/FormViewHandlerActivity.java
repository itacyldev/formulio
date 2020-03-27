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
import es.jcyl.ita.frmdrd.render.GroupRenderer;
import es.jcyl.ita.frmdrd.render.Renderer;
import es.jcyl.ita.frmdrd.render.RendererFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

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

public class FormViewHandlerActivity extends FragmentActivity {
    protected static final Log LOGGER = LogFactory
            .getLog(FormViewHandlerActivity.class);

    public static ActionMode actionMode;

    protected ContextThemeWrapper themeWrapper;

    private ViewRenderHelper renderHelper = new ViewRenderHelper();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.themeWrapper = new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Dialog);

        UIComponent root = MainController.getInstance().getViewRoot();

        ExecEnvironment env = new ExecEnvironment(MainController.getInstance().getGlobalContext());
        View rootView = renderHelper.render(this, env, root);
        setContentView(rootView);
    }



    protected void close() {
        finish();
    }

}