package es.jcyl.ita.formic.forms.validation;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.IBANValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.mini2Dx.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.converters.ConverterMap;

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

        staticValidators.put("integer", new NumberValidator(Integer.class));
        staticValidators.put("short", new NumberValidator(Short.class));
        staticValidators.put("long", new NumberValidator(Long.class));

        staticValidators.put("decimal", new NumberValidator(Double.class));
        staticValidators.put("double", new NumberValidator(Double.class));
        staticValidators.put("float", new NumberValidator(Float.class));

        staticValidators.put("email", new CommonsValidatorWrapper(EmailValidator.getInstance()));

        staticValidators.put("iban", new CommonsValidatorWrapper(IBANValidator.getInstance()));
    }

    public ValidatorFactory() {
        // register validators
        registerStaticValidators();
    }

    public Validator getValidator(String type) {
        return getValidator(type, null);
    }

    public Validator getValidator(String type, Map<String, String> params) {
        Validator validator = null;

        if (staticValidators.containsKey(type) && MapUtils.isEmpty(params)) {
            validator = staticValidators.get(type);
        } else {
            switch (type) {
                case "regex": {
                    validator = getRegexValidator(params);
                    break;
                }
                case "number": {
                    validator = getNumberValidator(params);
                    break;
                }
            }
        }
        return validator;
    }


    public Validator getNumberValidator(Map<String, String> params) {
        Validator validator = null;
        String numberType = params.get("numberType");

        Class numberClass = null;
        if (StringUtils.isNotEmpty(numberType)) {
            numberClass = ConverterMap.getConverter(numberType.toLowerCase());
        } else {
            //default class
            numberClass = Integer.class;
        }

        validator = new NumberValidator(numberClass);

        return validator;
    }


    /**
     * Creates a Regex validator with
     *
     * @param params
     * @return
     */
    public Validator getRegexValidator(Map<String, String> params) {
        Validator validator = null;
        String pattern = params.get("pattern");
        if (StringUtils.isEmpty(pattern)) {
            throw new ConfigurationException("Regex validator must have a pattern");
        }

        boolean caseSensitive = false;
        if (params.containsKey("caseSensitive")) {
            String caseSensitiveStr = params.get("caseSensitive");
            caseSensitive = Boolean.parseBoolean(caseSensitiveStr);
        }
        try {
            RegexValidator commonsValidator = new RegexValidator(pattern, caseSensitive);
            validator = new CommonsValidatorWrapper(commonsValidator);

        } catch (PatternSyntaxException ex) {
            String msg = "Validator pattern " + pattern + " is not valid";
            DevConsole.error(msg);
            throw new ConfigurationException(msg);
        }
        return validator;
    }
}

