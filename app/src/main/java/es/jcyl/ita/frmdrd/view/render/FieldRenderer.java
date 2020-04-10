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
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.view.InputFieldView;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class FieldRenderer extends BaseRenderer<UIField> {

    protected static final Object EMPTY_STRING = "";


    /**
     * Calculates the String to tag the view component that stores the user input
     *
     * @return
     */
    protected String getInputTag(UIField c) {
        return getBaseViewTag(c) + ">input";
    }


    protected InputFieldView createInputFieldView(Context viewContext, View view, UIField component) {
        InputFieldView fieldView = (InputFieldView) View.inflate(viewContext,
                R.layout.input_field_view, null);
        fieldView.setConverter(convFactory.get(component));
        fieldView.setTag(getBaseViewTag(component));
        fieldView.addView(view);
        if (component.getParentForm() != null) {
            fieldView.setFormId(component.getParentForm().getId());
        }
        fieldView.setFieldId(component.getId());

        return fieldView;
    }
}
