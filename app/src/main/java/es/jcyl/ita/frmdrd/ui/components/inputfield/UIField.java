package es.jcyl.ita.frmdrd.ui.components.inputfield;

import android.text.InputType;

import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;

import static es.jcyl.ita.frmdrd.ui.components.inputfield.UIField.TYPE.TEXT;

public class UIField extends UIInputComponent {
    public enum TYPE {
        TEXT, DATE, SWITCHER // SIGN
    }

    private TYPE type = TEXT;
    private int inputType = InputType.TYPE_CLASS_TEXT;

    @Override
    public String getRendererType() {
        return type.name().toLowerCase();
    }

    @Override
    public String getValueConverter() {
        return type.name().toLowerCase();
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

    public void setTypeStr(String type) {
        this.type = TYPE.valueOf(type.toUpperCase());
    }


}