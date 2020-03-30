package es.jcyl.ita.frmdrd.view;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

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

public class FormEditViewHandlerActivity extends FragmentActivity {
    protected static final Log LOGGER = LogFactory
            .getLog(FormEditViewHandlerActivity.class);

    public static ActionMode actionMode;

    protected ContextThemeWrapper themeWrapper;

    private ViewRenderHelper renderHelper = new ViewRenderHelper();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.themeWrapper = new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Dialog);

        MainController mainController = MainController.getInstance();
        UIComponent root = mainController.getViewRoot();
        // render the view and set it to current Activity
        View rootView = renderHelper.render(this, mainController.getExecEnvironment(), root);
        setContentView(rootView);

        // set android context to current formController
        mainController.setViewContext(this);
        mainController.getFormController().setViewContext(this);
    }


    protected void close() {
        finish();
    }

}