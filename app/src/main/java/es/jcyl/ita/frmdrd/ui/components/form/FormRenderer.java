package es.jcyl.ita.frmdrd.ui.components.form;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.view.render.BaseRenderer;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

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

public class FormRenderer extends BaseRenderer<UIForm> implements GroupRenderer<UIForm> {

    protected View createBaseView(Context viewContext, RenderingEnv env, UIForm component) {
        // TODO: provide different layout implementors
        View view = renderLinearLayout(viewContext, component);
        return view;
    }

    @Override
    protected void setupView(View baseView, RenderingEnv env, UIForm component) {

    }

    private View renderLinearLayout(Context context, UIForm form) {
        return View.inflate(context,
                R.layout.activity_form_edit_view_handler, null);
    }

    @Override
    public void initGroup(Context viewContext, RenderingEnv env, UIForm component, View root) {
    }

    @Override
    public void addViews(Context viewContext, RenderingEnv env, UIForm component, View root, View[] views) {
        LinearLayout layout = root.findViewById(R.id.fields_linear_layout);
        for (View view : views) {
            layout.addView(view);
        }
    }

    @Override
    public void endGroup(Context viewContext, RenderingEnv env, UIForm component, View root) {
        Button saveBtn = renderSaveButton(viewContext, env, component, ((ViewGroup) root));
        Button resetBtn = renderCancelButton(viewContext, env, component, ((ViewGroup) root));
        // add buttons to the toolbar
        LinearLayout layout = root.findViewById(R.id.form_edit_footer);
        layout.addView(resetBtn);
        if(!component.isReadOnly()){
            layout.addView(saveBtn);
        }
    }

    private Button renderSaveButton(Context context, RenderingEnv env, UIForm component, ViewGroup parent) {
        Button button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(context, component, ActionType.SAVE));
                }
            }
        });
        button.setText("Save");
        return button;
    }

    private Button renderCancelButton(Context context, RenderingEnv env, UIForm component, ViewGroup parent) {
        Button button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(context, component, ActionType.CANCEL));
                }
            }
        });
        button.setText("Cancel");
        return button;
    }
}