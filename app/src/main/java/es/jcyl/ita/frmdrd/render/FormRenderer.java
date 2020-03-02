package es.jcyl.ita.frmdrd.render;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.processors.GroovyProcessor;
import es.jcyl.ita.frmdrd.processors.JexlProcessor;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;
import es.jcyl.ita.frmdrd.ui.form.UITab;

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

public class FormRenderer {

    private Lifecycle lifecycle;

    public FormRenderer(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public View render(Context context, UIForm form) {
        View view = null;

        String formType = "LinearLayOut";

        switch (formType) {
            case "LinearLayOut":
                view = renderLinearLayout(context, form);
        }

        view.setVisibility(View.VISIBLE);
        view.setTag("form");
        DataBindings.registerView(form.getId(), view);

        return view;
    }

    public void render(Context context, List<UIField> fields) {
        RendererFactory rendererFact = new RendererFactory();
        for (UIField field : fields) {
            UIFieldRenderer fieldRenderer =
                    rendererFact.getComponentRenderer(context, field,
                            lifecycle);
            fieldRenderer.update(field);
        }
    }

    private View renderLinearLayout(Context context, UIForm form) {
        LinearLayout layout = ((Activity) context).findViewById(R.id.fields_linear_layout);

        Map<String, UITab> tabs = form.getTabs();

        for (UITab tab : tabs.values()) {
            renderFields(context, tab.getFields(), layout);
        }

        //renderTestButtons(context, layout);

        renderSaveButoon(context, layout);

        return layout;
    }

    private void renderSaveButoon(Context context, ViewGroup parent) {
        Button saveButton = new Button(context);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                es.jcyl.ita.frmdrd.context.Context formContext =
                        lifecycle.getMainContext().getContext("form");
                //formContext.
            }
        });
        saveButton.setText("Save");
        parent.addView(saveButton);
    }

    private void renderTestButtons(final Context context, ViewGroup parent) {
        Button groovyButton = new Button(context);
        groovyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroovyProcessor processor =
                        new GroovyProcessor(context.getDir(
                                "dynclasses", 0), context.getClassLoader());

                BufferedReader reader = null;
                StringBuffer sb = new StringBuffer();
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(context.getAssets().open(
                                    "fibonacci.groovy")));

                    // do reading, usually loop until end of file reading
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }


                GroovyProcessor.EvalResult result =
                        (GroovyProcessor.EvalResult) processor.evaluate(sb.toString(), "fibonacci_groovy",
                                lifecycle.getMainContext());

                long init = System.nanoTime();
                fib(100);
                String execTime = (System.nanoTime() - init) / 1000000 + " ms";


                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();

            }
        });
        groovyButton.setText("test groovy");
        parent.addView(groovyButton);

        Button jexlButton = new Button(context);
        jexlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JexlProcessor processor = new JexlProcessor();
                BufferedReader reader = null;
                StringBuffer sb = new StringBuffer();
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(context.getAssets().open(
                                    "test.jexl")));

                    // do reading, usually loop until end of file reading
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
                Object result = processor.evaluate(sb.toString(), "", lifecycle.getMainContext());
                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
            }
        });
        jexlButton.setText("test jexl");
        parent.addView(jexlButton);
    }

    private void renderFields(Context context,
                              Map<String, UIField> fields,
                              ViewGroup parent) {
        RendererFactory rendererFact = new RendererFactory();

        for (UIField field : fields.values()) {
            UIFieldRenderer fieldRenderer =
                    rendererFact.getComponentRenderer(context, field,
                            lifecycle);

            View view = fieldRenderer.render(field);
            parent.addView(view);
        }
    }

    private int fib(int n) {
        int a = 0;
        int b = 1;
        int aux = 0;
        for (int i = 0; i < n; i++) {
            aux = a;
            a = b;
            b = aux + b;
        }
        return a;
    }
}