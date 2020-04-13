package es.jcyl.ita.frmdrd.ui.components;

import android.text.InputType;

import es.jcyl.ita.frmdrd.validation.Validator;

import static es.jcyl.ita.frmdrd.ui.components.UIField.TYPE.TEXT;

public class UIField extends UIInputComponent {
    public enum TYPE {
        TEXT, DATE, BOOLEAN // SIGN
    }

    private TYPE type = TEXT;
    private int inputType = InputType.TYPE_CLASS_TEXT;


    @Override
    public String getRendererType() {
        switch (type) {
            case TEXT:
                return "textfield";
            case DATE:
                return "date";
            case BOOLEAN:
                return "checkbox";
            default:
                throw new UnsupportedOperationException(type.toString());
        }
    }

    public String getType() {
        return type.name();
    }

    public void setType(final TYPE type) {
        this.type = type;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(final int inputType) {
        this.inputType = inputType;
    }



}