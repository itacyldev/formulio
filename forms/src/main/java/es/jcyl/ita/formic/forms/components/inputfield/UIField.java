package es.jcyl.ita.formic.forms.components.inputfield;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.ViewConfigException;

import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.DATE;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXT;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXTAREA;

public class UIField extends UIInputComponent {
    public enum TYPE {
        TEXT, DATE, DATETIME, SWITCHER, TEXTAREA
    }

    private TYPE type = TEXT;
    private Integer lines;
    protected ValueBindingExpression hint;

    private String pattern;
    private String datePattern = "yyyy-MM-dd";
    private String datetimePattern = "yyyy-MM-dd HH:mm:ss";

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


    public String getHint(Context context) {
        String sHint = null;
        if (this.hint != null) {
            Object oHint = JexlFormUtils.eval(context, this.hint);
            try {
                sHint = (String) ConvertUtils.convert(oHint, String.class);
            } catch (Exception e) {
                throw new ViewConfigException(String.format("Invalid rendering expression in " +
                                "component [%s] the resulting value couldn't be cast to String.",
                        this.getId(), this.hint, e));
            }
        }
        return StringUtils.isBlank(sHint)?null:sHint;
    }

    public void setHint(ValueBindingExpression hint) {
        this.hint = hint;
    }

    public void setTypeStr(String type) {
        this.type = TYPE.valueOf(type.toUpperCase());
    }

    public Integer getLines() {
        return lines;
    }

    public void setLines(Integer lines) {
        this.lines = lines;
    }

    public String getPattern() {
        if (pattern == null) {
            if (getType().equals(DATE.name())) {
                setPattern(getDatePattern());
            } else {
                setPattern(getDatetimePattern());
            }
        }
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getDatetimePattern() {
        return datetimePattern;
    }

    public void setDatetimePattern(String datetimePattern) {
        this.datetimePattern = datetimePattern;
    }
}