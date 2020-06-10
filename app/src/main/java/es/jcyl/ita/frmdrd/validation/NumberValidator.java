package es.jcyl.ita.frmdrd.validation;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Gives access to and Android view element and its descendant values using a context interface.
 */

import org.apache.commons.validator.routines.AbstractNumberValidator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class NumberValidator extends AbstractNumberValidator implements Validator {

    private Class<? extends Number> clazz;


    public NumberValidator(Class<? extends Number> clazz) {
        super(true, AbstractNumberValidator.STANDARD_FORMAT, clazz == Double.class || clazz == Float.class);
        this.clazz = clazz;
    }

    @Override
    protected Number processParsedValue(Object value, Format formatter) {
        Method method = null;
        Number result = null;

        if (isAllowFractions()) {
            double doubleValue = ((Number) value).doubleValue();
            double minValue;
            double maxValue;
            try {
                Field minValueField = clazz.getDeclaredField("MIN_VALUE");
                minValue = minValueField.getDouble(null);
                Field maxValueField = clazz.getDeclaredField("MAX_VALUE");
                maxValue = maxValueField.getDouble(null);
                if (doubleValue < minValue || doubleValue > maxValue) {
                    return null;
                }
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        } else {
            long longValue = ((Number) value).longValue();
            long minValue;
            long maxValue;
            try {
                Field minValueField = clazz.getDeclaredField("MIN_VALUE");
                minValue = minValueField.getLong(null);
                Field maxValueField = clazz.getDeclaredField("MAX_VALUE");
                maxValue = maxValueField.getLong(null);
                if (longValue < minValue || longValue > maxValue) {
                    return null;
                }
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }

        }


        try {
            method = clazz.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {

        }

        try {
            result = (Number) method.invoke(null, value.toString());
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }

        return result;

    }


    @Override
    public void validate(Context ctx, UIComponent component, String value) {
        Boolean valid = isValid(value);
        String msg;
        if (!valid) {
            throw new ValidatorException("Error");
        }
    }
}
