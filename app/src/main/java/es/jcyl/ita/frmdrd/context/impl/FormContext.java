package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        return super.get(key);
    }

    @Override
    public Object getValue(String key) {
        Object value = null;

        UIField field = (UIField) super.get(key);

        if (field != null) {
            View view = DataBindings.getView(key.toString());

            String fieldType = field.getType();
            switch (fieldType) {
                case "TEXT":
                    value = getTextValue(view);
                    break;
                case "DATE":
                    value = getDateValue(view);
                    break;
                case "BOOLEAN":
                    value = getBooleanValue(view);
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
            field = (UIField) get(key);
            if (field != null) {
                View view = DataBindings.getView(key.toString());

                String fieldType = field.getType();
                switch (fieldType) {
                    case "TEXT":
                        setTextValue(view, value);
                        break;
                    case "DATE":
                        setDateValue(view, value);
                        break;
                    case "BOOLEAN":
                        setBooleanValue(view, value);
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

    private String getTextValue(View view) {
        String value = null;
        if (view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        }

        return value;
    }

    private void setTextValue(View view, Object value) {
        if (view instanceof EditText) {
            ((EditText) view).setText(value.toString());
        }
    }

    private Boolean getBooleanValue(View view) {
        Boolean value = null;
        if (view instanceof Switch) {
            if (((Switch) view).isChecked()) {
                value = Boolean.TRUE;
            } else {
                value = Boolean.FALSE;
            }
        }

        return value;
    }

    private void setBooleanValue(View view, Object value) {
        Boolean booleanValue = Boolean.parseBoolean(value.toString());
        if (view instanceof Switch) {

            ((Switch) view).setChecked(booleanValue);
        }
    }


    private Date getDateValue(View view) {
        Date value = null;
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

    private void setDateValue(View view, Object value) {
        if (view instanceof TextView) {

        }
    }

    public UIForm getRoot() {
        return root;
    }

    public void setRoot(UIForm root) {
        this.root = root;
    }

}
