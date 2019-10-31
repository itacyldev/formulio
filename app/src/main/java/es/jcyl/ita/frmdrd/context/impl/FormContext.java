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

public class FormContext extends AbstractContext {

    public FormContext(String prefix) {
        super(prefix);
    }

    private UIForm root;

    @Override
    public Object get(Object key) {
        Object value = null;

        UIField field = (UIField) super.get(key);

        if (field == null) {
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

    private String getTextValue(View view) {
        String value = null;
        if (view instanceof EditText) {
            value = ((EditText) view).getText().toString();
        }

        return value;
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

    public UIForm getRoot() {
        return root;
    }

    public void setRoot(UIForm root) {
        this.root = root;
    }
}
