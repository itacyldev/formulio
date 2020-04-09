package es.jcyl.ita.frmdrd.view.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.view.UserMessagesHelper;
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

public class FormEditViewHandlerActivity extends FragmentActivity implements FormActivity<FormEditController> {

    private Router router;
    private RenderingEnv env;
    private FormEditController formController;
    /**
     * View element used to render the forms defined for this controller
     */
    private ViewGroup contentView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_edit_view_handler);
        contentView = this.findViewById(R.id.form_content);

        MainController mc = MainController.getInstance();
        mc.registerActivity(this);

        // render edit view content and link content view
        View viewRoot = mc.renderView(this);
        contentView.addView(viewRoot);

        // add action buttons
        ViewGroup toolBar = findViewById(R.id.form_toolbar);
        renderToolBar((ViewGroup) toolBar);

        // check if there are messages to show
        UserMessagesHelper.showGlobalMessages(this, mc.getRouter());
    }

    private void renderToolBar(ViewGroup parentView) {
        if (this.formController.getActions() != null) {
            for (FCAction action : this.formController.getActions()) {
                renderActionButton(this, parentView, action);
            }
        }
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainController mc = MainController.getInstance();
        UserAction action = UserAction.back(this);
        action.setOrigin(formController.getId());
        mc.getActionController().doUserAction(action);
        finish();
    }

    private Button renderActionButton(Context context, ViewGroup parent, FCAction formAction) {
        Button button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(context, null, formAction.getType(),
                            formAction.getRoute()));
                }
            }
        });
        button.setText(formAction.getLabel());
        parent.addView(button);
        return button;
    }

    @Override
    public void setFormController(FormEditController formController) {
        this.formController = formController;
    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void setRenderingEnv(RenderingEnv env) {
        this.env = env;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public ViewGroup getContentView() {
        return contentView;
    }
}