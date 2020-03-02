package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

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

public abstract class AbstractFieldRenderer implements UIFieldRenderer {

    Context context;
    Lifecycle lifecycle;

    //@Inject
    protected OnChangeFieldInterceptor onChangeInterceptor;


    public AbstractFieldRenderer(Context context, Lifecycle lifecycle) {
        this.context = context;
        this.lifecycle = lifecycle;
        onChangeInterceptor = new OnChangeFieldInterceptor(lifecycle);
        //DaggerDiComponent.create().inject(this);
    }

    public abstract View render(UIField field);


    @Override
    public void update(UIField field) {
        View view = DataBindings.getView(field.getId());
        String renderCondition = field.getRenderCondition();


        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition, field.getId());
        }

        view.setVisibility(render ? View.VISIBLE : View.INVISIBLE);
    }

    public void bindField(UIField field, View view) {
        DataBindings.registerView(field.getId(), view);
    }


    protected boolean validateCondition(String renderCondition,
                                        String fieldId) {
        /*GroovyProcessor processor =
                new GroovyProcessor(context.getDir(
                        "dynclasses", 0), context.getClassLoader());
        String filename = fieldId + "_renderCond";
        GroovyProcessor.EvalResult result =
                (GroovyProcessor.EvalResult) processor.evaluate(renderCondition, filename,
                        lifecycle.getMainContext());

        return Boolean.parseBoolean(result.getResult().toString());*/
        return true;
    }
}
