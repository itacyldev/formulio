package es.jcyl.ita.frmdrd.ui.components;

import es.jcyl.ita.frmdrd.validation.Validator;

import static es.jcyl.ita.frmdrd.ui.components.UIField.TYPE.TEXT;

public class UIField extends UIComponent {

    public enum TYPE {
        TEXT, DATE, BOOLEAN, // SIGN
    }

    private TYPE type = TEXT;
    private int inputType = 1;
    private String defaultValue;

    private static final Validator[] EMTPY_VALIDATOR = new Validator[0];
    private Validator[] validators = EMTPY_VALIDATOR;

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public void addValidator(Validator validator) {
        if (validator == null) {
            throw new NullPointerException();
        }
        int size = (validators == null) ? 1 : validators.length + 1;
        Validator[] newArray = new Validator[size];
        if (validators != null) {
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