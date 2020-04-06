package es.jcyl.ita.frmdrd.ui.components.form;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.render.BaseRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;

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

public class FormRenderer extends BaseRenderer implements GroupRenderer {

    protected View createBaseView(Context viewContext, RenderingEnv env, UIComponent component) {
        // TODO: provide different layout implementors
        View view = renderLinearLayout(viewContext, (UIForm) component);
        return view;
    }

    @Override
    protected void setupView(View baseView, RenderingEnv env, UIComponent component) {

    }

    private View renderLinearLayout(Context context, UIForm form) {
        LinearLayout formLayout = (LinearLayout) View.inflate(context,
                R.layout.activity_form_edit_view_handler, null);
        return formLayout;
    }

//
//
//        Button jexlButton = new Button(context);
//        jexlButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                JexlProcessor processor = new JexlProcessor();
//                BufferedReader reader = null;
//                StringBuffer sb = new StringBuffer();
//                try {
//                    reader = new BufferedReader(
//                            new InputStreamReader(context.getAssets().open(
//                                    "test.jexl")));
//
//                    // do reading, usually loop until end of file reading
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line);
//                        sb.append("\n");
//                    }
//                } catch (IOException e) {
//                    //log the exception
//                } finally {
//                    if (reader != null) {
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            //log the exception
//                        }
//                    }
//                }
//                Object result = processor.evaluate(sb.toString(), "", lifecycle.getMainContext());
//                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//        jexlButton.setText("test jexl");
//        parent.addView(jexlButton);
//    }

//    private void renderFields(Context context,
//                              Map<String, UIField> fields,
//                              ViewGroup parent) {
//        RendererFactory rendererFact = new RendererFactory();
//
//        for (UIField field : fields.values()) {
//            UIFieldRenderer fieldRenderer =
//                    rendererFact.getRenderer(context, field,
//                            lifecycle);
//
//            View view = fieldRenderer.render(field);
//            parent.addView(view);
//        }
//    }

    @Override
    public void initGroup(Context viewContext, RenderingEnv env, UIComponent component, View root) {
    }

    @Override
    public void addViews(Context viewContext, RenderingEnv env, UIComponent component, View root, View[] views) {
        LinearLayout layout = root.findViewById(R.id.fields_linear_layout);
        for (View view : views) {
            layout.addView(view);
        }
    }

    @Override
    public void endGroup(Context viewContext, RenderingEnv env, UIComponent component, View root) {
        renderSaveButton(viewContext, env, component, ((ViewGroup) root));
        renderCancelButton(viewContext, env, component, ((ViewGroup) root));
    }

    private void renderSaveButton(Context context, RenderingEnv env, UIComponent component, ViewGroup parent) {
        Button saveButton = new Button(context);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(context, component, ActionType.SAVE));
                }
            }
        });
        saveButton.setText("Save");
        parent.addView(saveButton);
    }

    private void renderCancelButton(Context context, RenderingEnv env, UIComponent component, ViewGroup parent) {
        Button saveButton = new Button(context);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.doAction(new UserAction(context, component, ActionType.CANCEL));
                }
            }
        });
        saveButton.setText("Cancel");
        parent.addView(saveButton);
    }
}