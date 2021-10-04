package es.jcyl.ita.formic.forms.components;
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

import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.validation.RequiredValidator;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverterFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIInputComponent extends AbstractUIComponent {
    private static final ViewValueConverterFactory viewConverterFactory = ViewValueConverterFactory.getInstance();

    private String valueConverter;
    private Integer inputType = null;
    protected boolean hasDeleteButton = true;
    protected boolean hasTodayButton = true;
    protected String hint;

    private static final Validator[] EMPTY_VALIDATOR = new Validator[0];
    private Validator[] validators = EMPTY_VALIDATOR;

    protected ImageView resetButton;
    protected ImageView infoButton;

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

    public void addValidator(Validator... lstValidator) {
        Validator[] newValidators;
        if (validators == null) {
            newValidators = Arrays.copyOf(lstValidator, lstValidator.length);
        } else {
            newValidators = Arrays.copyOf(this.validators, this.validators.length + lstValidator.length);
            System.arraycopy(lstValidator, 0, newValidators, this.validators.length, lstValidator.length);
        }
        this.validators = newValidators;
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
        return String.format("[%s]: %s/%s", this.getClass().getSimpleName(), this.id, this.getLabel());
    }

    public boolean isNestedProperty() {
        List<String> dependingVariables = this.getValueExpression().getDependingVariables();
        // entity.nestedProperty.prop
        return (dependingVariables.size() != 1) ? false : dependingVariables.get(0).split("\\.").length > 2;
    }

    /**
     * Returns true if the component has a two-way binding with and entity field. This is
     * perform through value binding expressions, if there's no binding expression or is read-only
     * the component is not bound.
     *
     * @return
     */
    public boolean isBound() {
        return (getValueExpression() == null) ? false : !getValueExpression().isReadonly();
    }

    public String getValueConverter() {
        return valueConverter;
    }

    public ViewValueConverter getConverter() {
        return viewConverterFactory.get(this.getValueConverter());
    }

    public void setValueConverter(String valueConverter) {
        this.valueConverter = valueConverter;
    }

    public boolean isMandatory() {
        if (validators == EMPTY_VALIDATOR) {
            return false;
        }
        for (Validator validator : validators) {
            if (validator instanceof RequiredValidator) {
                return true;
            }
        }
        return false;
    }


    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(final Integer inputType) {
        this.inputType = inputType;
    }

    public boolean hasDeleteButton() {
        return hasDeleteButton;
    }

    public void setHasDeleteButton(boolean hasDeleteButton) {
        this.hasDeleteButton = hasDeleteButton;
    }

    public boolean hasTodayButton() {
        return hasTodayButton;
    }

    public void setHasTodayButton(boolean hasTodayButton) {
        this.hasTodayButton = hasTodayButton;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public ImageView getResetButton() {
        return resetButton;
    }

    public void setResetButton(ImageView resetButton) {
        this.resetButton = resetButton;
    }

    public ImageView getInfoButton() {
        return infoButton;
    }

    public void setInfoButton(ImageView infoButton) {
        this.infoButton = infoButton;
    }
}
