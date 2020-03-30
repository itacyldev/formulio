package es.jcyl.ita.frmdrd.ui.components.inputfield;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.validation.Validator;

import static es.jcyl.ita.frmdrd.ui.components.inputfield.UIField.TYPE.TEXT;

public class UIField extends UIComponent {

    public enum TYPE {
        TEXT, DATE, BOOLEAN, // COMBO, SEPARATOR, INFO, DROPDOWN, SIGN
    }

    private String hint;
    private String extendedHint;
    private TYPE type = TEXT;
    private int inputType = 1;
    private String persistedField;
    private String regexp;
    private String defaultValue;
    private boolean editable = true;
    private boolean deletable = true;
    private boolean required = false;

    private Validator[] validators;

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

    private Object source;

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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }


    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }


    public void addValidator(Validator validator) {
        if (validator == null) {
            throw new NullPointerException();
        }
        int size = (validators == null) ? 1 : validators.length + 1;
        Validator[] newArray = new Validator[size];
        if(validators !=null){
            System.arraycopy(validators, 0, newArray, 0, validators.length);
        }
        newArray[newArray.length - 1] = validator;
        validators = newArray;
    }

    /**
     * <p>Return the set of registered {@link Validator}s for this
     * {@link UIField} instance.  If there are no registered validators,
     * a zero-length array is returned.</p>
     */
    public Validator[] getValidators() {
        return this.validators;
    }


    @Override
    public String toString() {
        return String.format("[%s]: %s/%s", this.type, this.id, this.getLabel());
    }
}