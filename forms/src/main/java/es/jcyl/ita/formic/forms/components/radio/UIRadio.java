package es.jcyl.ita.formic.forms.components.radio;

import android.widget.RadioGroup;

import es.jcyl.ita.formic.forms.components.select.UISelect;

public class UIRadio extends UISelect {
    private final static String RADIO_TYPE = "radio";
    private String orientation = "vertical";

    @Override
    public String getRendererType() {
        return RADIO_TYPE;
    }

    @Override
    public String getValueConverter() {
        return RADIO_TYPE;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getOrientationType() {
        return (this.orientation.equalsIgnoreCase("horizontal")
                ? RadioGroup.HORIZONTAL : RadioGroup.VERTICAL);
    }
}
