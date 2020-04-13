package es.jcyl.ita.frmdrd.ui.components;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import es.jcyl.ita.frmdrd.validation.Validator;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIInputComponent extends UIComponent {

    protected String label;
    private boolean readOnly;
    private String defaultValue;


    private static final Validator[] EMPTY_VALIDATOR = new Validator[0];
    private Validator[] validators = EMPTY_VALIDATOR;

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
        return String.format("[%s]: %s/%s", this.getClass(), this.id, this.getLabel());
    }

    public boolean isReadOnly() {
        if (this.parentForm == null) {
            return readOnly;
        } else {
            // mon knows best
            return this.parentForm.isReadOnly() || this.readOnly;
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }


}
