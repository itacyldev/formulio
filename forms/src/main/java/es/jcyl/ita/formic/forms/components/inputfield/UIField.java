package es.jcyl.ita.formic.forms.components.inputfield;

import es.jcyl.ita.formic.forms.components.UIInputComponent;

import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXT;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXTAREA;

public class UIField extends UIInputComponent {
    public enum TYPE {
        TEXT, DATE, SWITCHER, TEXTAREA
    }

    private TYPE type = TEXT;
    private Integer lines;
    protected String hint;

    @Override
    public String getRendererType() {
        return type.name().toLowerCase();
    }

    @Override
    public String getValueConverter() {
        if (type == TEXT || type == TEXTAREA) {
            return "text";
        } else {
            return type.name().toLowerCase();
        }
    }

    public String getType() {
        return type.name();
    }

    public void setType(final TYPE type) {
        this.type = type;
    }

    public String getHint() { return hint; }

    public void setHint(String hint) { this.hint = hint; }

    public void setTypeStr(String type) {
        this.type = TYPE.valueOf(type.toUpperCase());
    }

    public Integer getLines() {
        return lines;
    }

    public void setLines(Integer lines) {
        this.lines = lines;
    }

}