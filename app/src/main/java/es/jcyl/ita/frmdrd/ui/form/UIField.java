package es.jcyl.ita.frmdrd.ui.form;

public class UIField extends UIComponent {
    public enum TYPE {
        TEXT, COMBO, DATE, BOOLEAN, SEPARATOR, INFO, DROPDOWN, SIGN
    }

    private String hint;
    private String extendedHint;
    private TYPE type = UIField.TYPE.TEXT;
    private int inputType = 1;
    private String persistedField;
    private String regexp;
    private String defaultValue;
    private boolean editable = true;
    private boolean deletable = true;
    private boolean required = false;

    public String getHint() {
        return hint;
    }

    public void setHint(final String hint) {
        this.hint = hint;
    }

    public String getExtendedHint() {
        return extendedHint;
    }

    public void setExtendedHint(final String extendedHint) {
        this.extendedHint = extendedHint;
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

    public String getPersistedField() {
        return persistedField;
    }

    public void setPersistedField(final String persistedField) {
        this.persistedField = persistedField;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public boolean isRequired() {return required;}

    public void setRequired(final boolean required){
        this.required = required;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

}