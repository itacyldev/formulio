package es.jcyl.ita.formic.forms.components.placeholders;

import es.jcyl.ita.formic.forms.components.UIComponent;

public class UIDivisor extends UIComponent {

    private Integer color;
    private Integer strokeWidth;

    public UIDivisor() {
        setRendererType("divisor");
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getStrokeWidth() {
        return strokeWidth;
    }

    public void setstrokeWidth(Integer strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

}