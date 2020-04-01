package es.jcyl.ita.frmdrd.view.render;
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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.InputFieldView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class FieldRenderer extends BaseRenderer {

    protected static final Object EMPTY_STRING = "";

    /**
     * Tries to retrieve the component value first accessing the form context and then using
     * global context
     *
     * @param component
     * @param env
     * @return
     */
    protected <T> T getValue(UIComponent component, ExecEnvironment env, Class<T> clazz) {
        Object value = component.getValue(env.getContext());
        if (value == null) {
            return handleNullValue(value);
        }
        return (T) ConvertUtils.convert(value, clazz);
    }

    protected abstract <T> T handleNullValue(Object value);

    /**
     * Calculates the String to tag the view component that stores the user input
     *
     * @return
     */
    protected String getInputTag(UIComponent c) {
        return getBaseViewTag(c) + ">input";
    }

    /**
     * Calculates the tag for the GroupView component that contains all the input, so when a partial
     * re-render is applied, the hole component can be easily found in the view.
     * The base view will be tagged as form:id and the input view below this as form:id>input
     *
     * @return
     */
    protected String getBaseViewTag(UIComponent c) {
        UIForm form = c.getParentForm();
        String formId = (form == null) ? "root" : form.getId();
        return formId + ":" + c.getId();
    }


    protected InputFieldView createInputFieldView(Context viewContext, View view, UIComponent component) {
        InputFieldView fieldView = (InputFieldView) View.inflate(viewContext,
                R.layout.input_field_view, null);

        fieldView.setConverter(convFactory.get(component));
        fieldView.setTag(getBaseViewTag(component));
        fieldView.addView(view);
        if(component.getParentForm() != null){
            fieldView.setFormId(component.getParentForm().getId());
        }
        fieldView.setFieldId(component.getId());

        return fieldView;
    }
}
