package es.jcyl.ita.frmdrd.ui.validation;
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

import org.apache.commons.validator.routines.AbstractFormatValidator;

import java.lang.reflect.Method;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CommonsValidatorWrapper implements es.jcyl.ita.frmdrd.ui.validation.Validator {


    private AbstractFormatValidator abstractDelegate;
    private Object delegate;
    private Method method;

    public CommonsValidatorWrapper(Object validator) {
        if (validator instanceof AbstractFormatValidator) {
            this.abstractDelegate = (AbstractFormatValidator) validator;
        } else {
            // call by reflection
            try {
                delegate = validator;
                method = validator.getClass().getMethod("isValid", String.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("The provided object doesn't implement the method " +
                        "isValid(String value)." + validator.getClass().getName());
            }
        }
    }


    @Override
    public void validate(Context ctx, UIComponent component, String value) {
        Boolean valid;
        if (abstractDelegate != null) {
            valid = abstractDelegate.isValid(value);
        } else {
            try {
                valid = (Boolean) method.invoke(delegate, value);
            } catch (Exception e) {
                throw new RuntimeException(String.format("An error occurred trying to validate the " +
                        "field [%s] with value [%s].", component.getId(), value));
            }
        }
        String msg = "Error during validation";
        if (!valid) {
            throw new ValidatorException(msg);
        }
    }
}
