package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.context.AbstractContext;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;
import es.jcyl.ita.frmdrd.util.DataUtils;

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

public class FormContext extends AbstractContext {

    public FormContext(String prefix) {
        super(prefix);
    }

    private UIForm root;

    @Override
    public Object get(Object key) {
        Object value = null;

        UIField field = (UIField) super.get(key);

        if (field != null) {
            LinearLayout layout = (LinearLayout)DataBindings.getView(key.toString());

            String fieldType = field.getType();
            switch (fieldType) {
                case "TEXT":
                    value = getTextValue(layout);
                    break;
                case "DATE":
                    value = getDateValue(layout);
                    break;
                case "BOOLEAN":
                    value = getBooleanValue(layout);
                    break;
            }
        }

        return value;
    }

    @Override
    public Object put(String key, Object value) {
        UIField field = null;
        if (value instanceof UIField) {
            field = (UIField) value;
            super.put(key, value);

        } else {

            field = this.getFieldConfig(key);
            if (field != null) {
                LinearLayout layout =
                        (LinearLayout) DataBindings.getView(key.toString());

                String fieldType = field.getType();
                switch (fieldType) {
                    case "TEXT":
                        setTextValue(layout, value);
                        break;
                    case "DATE":
                        setDateValue(layout, value);
                        break;
                    case "BOOLEAN":
                        setBooleanValue(layout, value);
                        break;
                }
            }
        }

        return field;
    }

    public UIField getFieldConfig(Object key) {
        UIField field = (UIField) super.get(key);
        return field;
    }

    private String getTextValue(ViewGroup viewGroup) {
        String value = null;
        View view = this.getInputView(viewGroup);
        if (view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        }
        return value;
    }

    private void setTextValue(ViewGroup viewGroup, Object value) {
        View view = this.getInputView(viewGroup);
        if (view instanceof EditText) {
            ((EditText) view).setText(value.toString());
        }
    }

    private Boolean getBooleanValue(ViewGroup viewGroup) {
        Boolean value = null;
        View view = this.getInputView(viewGroup);
        if (view instanceof Switch) {
            if (((Switch) view).isChecked()) {
                value = Boolean.TRUE;
            } else {
                value = Boolean.FALSE;
            }
        }

        return value;
    }

    private void setBooleanValue(ViewGroup viewGroup, Object value) {
        Boolean booleanValue = Boolean.parseBoolean(value.toString());
        View view = this.getInputView(viewGroup);
        if (view instanceof Switch) {

            ((Switch) view).setChecked(booleanValue);
        }
    }


    private Date getDateValue(ViewGroup viewGroup) {
        Date value = null;
        View view = this.getInputView(viewGroup);
        if (view instanceof TextView) {
            try {
                value = DataUtils.DATE_FORMAT.parse(
                        ((Button) view).getText()
                                .toString());
            } catch (ParseException e) {

            }
        }

        return value;
    }

    private void setDateValue(ViewGroup viewGroup, Object value) {
        View view = this.getInputView(viewGroup);
        if (view instanceof TextView) {

        }
    }

    private View getInputView(ViewGroup viewGroup) {
        View inputView = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getTag().equals("input")) {
                inputView = view;
            }
        }

        return inputView;
    }

    public UIForm getRoot() {
        return root;
    }

    public void setRoot(UIForm root) {
        this.root = root;
    }
}
