package es.jcyl.ita.frmdrd.validation;
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

import org.apache.commons.validator.routines.DoubleValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.IntegerValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ValidatorFactory {
    private static final Map<String, Validator> staticValidators = new HashMap<>();
    public static ValidatorFactory _instance;

    public static ValidatorFactory getInstance() {
        if (_instance == null) {
            _instance = new ValidatorFactory();
        }
        return _instance;
    }


    /**
     * Validatos with no parameters
     */
    private void registerStaticValidators() {
        staticValidators.put("required", new RequiredValidator());
        staticValidators.put("integer", new CommonsValidatorWrapper(IntegerValidator.getInstance()));
        staticValidators.put("decimal", new CommonsValidatorWrapper(DoubleValidator.getInstance()));
        staticValidators.put("email", new CommonsValidatorWrapper(EmailValidator.getInstance()));
    }

    public ValidatorFactory() {
        // register validators
        registerStaticValidators();
    }

    public Validator getValidator(String type) {
        return staticValidators.get(type.toLowerCase());
    }

    public Validator getValidator(String type, Map<String, String> params) {
        throw new UnsupportedOperationException("not implemented yet!");
    }
}
