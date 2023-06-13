package es.jcyl.ita.formic.forms.components.inputfield;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.components.UIInputComponent;

import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.DATE;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.DATETIME;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXT;
import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.TEXTAREA;

public class UIField extends UIInputComponent {
    public enum TYPE {
        TEXT, DATE, DATETIME, SWITCHER, TEXTAREA
    }

    private TYPE type = TEXT;
    private Integer lines;

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
        }
        else if ((type ==  DATE || type == DATETIME) && StringUtils.isNotEmpty(this.valueConverter) && this.valueConverter.equalsIgnoreCase("integer")) {
            return this.valueConverter;
        }else {
            return type.name().toLowerCase();
        }
    }

    public String getType() {
        return type.name();
    }

    public void setType(final TYPE type) {
        this.type = type;
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