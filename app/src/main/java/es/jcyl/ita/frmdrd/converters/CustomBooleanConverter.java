package es.jcyl.ita.frmdrd.converters;
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

import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.beanutils.converters.AbstractConverter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CustomBooleanConverter extends AbstractConverter {
// ----------------------------------------------------------- Constructors


    /**
     * Create a {@link org.mini2Dx.beanutils.Converter} that will throw a
     * {@link org.mini2Dx.beanutils.ConversionException}
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     */
    public CustomBooleanConverter() {
        super();
    }


    /**
     * Create a {@link org.mini2Dx.beanutils.Converter} that will return the specified default value
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     *
     * @param defaultValue The default value to be returned if the value
     *                     being converted is not recognised. This value may be null, in which
     *                     case null will be returned on conversion failure. When non-null, it is
     *                     expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *                     The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *                     in which case this constructor acts like the no-argument one.
     */
    public CustomBooleanConverter(Object defaultValue) {
        super();
        if (defaultValue != NO_DEFAULT) {
            setDefaultValue(defaultValue);
        }
    }

    /**
     * Create a {@link org.mini2Dx.beanutils.Converter} that will throw a
     * {@link org.mini2Dx.beanutils.ConversionException}
     * if a conversion error occurs, ie the string value being converted is
     * not one of the known true strings, nor one of the known false strings.
     * <p>
     * The provided string arrays are copied, so that changes to the elements
     * of the array after this call is made do not affect this object.
     *
     * @param trueStrings  is the set of strings which should convert to the
     *                     value Boolean.TRUE. The value null must not be present. Case is
     *                     ignored.
     * @param falseStrings is the set of strings which should convert to the
     *                     value Boolean.TRUE. The value null must not be present. Case is
     *                     ignored.
     * @since 1.8.0
     */
    public CustomBooleanConverter(String[] trueStrings, String[] falseStrings) {
        super();
        this.trueStrings = copyStrings(trueStrings);
        this.falseStrings = copyStrings(falseStrings);
    }

    /**
     * Create a {@link org.mini2Dx.beanutils.Converter} that will return
     * the specified default value if a conversion error occurs.
     * <p>
     * The provided string arrays are copied, so that changes to the elements
     * of the array after this call is made do not affect this object.
     *
     * @param trueStrings  is the set of strings which should convert to the
     *                     value Boolean.TRUE. The value null must not be present. Case is
     *                     ignored.
     * @param falseStrings is the set of strings which should convert to the
     *                     value Boolean.TRUE. The value null must not be present. Case is
     *                     ignored.
     * @param defaultValue The default value to be returned if the value
     *                     being converted is not recognised. This value may be null, in which
     *                     case null will be returned on conversion failure. When non-null, it is
     *                     expected that this value will be either Boolean.TRUE or Boolean.FALSE.
     *                     The special value BooleanConverter.NO_DEFAULT can also be passed here,
     *                     in which case an exception will be thrown on conversion failure.
     * @since 1.8.0
     */
    public CustomBooleanConverter(String[] trueStrings, String[] falseStrings,
                                  Object defaultValue) {
        super();
        this.trueStrings = copyStrings(trueStrings);
        this.falseStrings = copyStrings(falseStrings);
        if (defaultValue != NO_DEFAULT) {
            setDefaultValue(defaultValue);
        }
    }


    // ----------------------------------------------------- Static Variables


    /**
     * This is a special reference that can be passed as the "default object"
     * to the constructor to indicate that no default is desired. Note that
     * the value 'null' cannot be used for this purpose, as the caller may
     * want a null to be returned as the default.
     *
     * @deprecated Use constructors without default value.
     */
    @Deprecated
    public static final Object NO_DEFAULT = new Object();


    // ----------------------------------------------------- Instance Variables

    /**
     * The set of strings that are known to map to Boolean.TRUE.
     */
    private String[] trueStrings = {"true", "yes", "y", "on", "1"};

    /**
     * The set of strings that are known to map to Boolean.FALSE.
     */
    private String[] falseStrings = {"false", "no", "n", "off", "0"};

    // --------------------------------------------------------- Protected Methods

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class<Boolean> getDefaultType() {
        return Boolean.class;
    }

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param <T>   Target type of the conversion.
     * @param type  is the type to which this value should be converted. In the
     *              case of this BooleanConverter class, this value is ignored.
     * @param value is the input value to be converted. The toString method
     *              shall be invoked on this object, and the result compared (ignoring
     *              case) against the known "true" and "false" string values.
     * @return Boolean.TRUE if the value was a recognised "true" value,
     * Boolean.FALSE if the value was a recognised "false" value, or
     * the default value if the value was not recognised and the constructor
     * was provided with a default value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 1.8.0
     */
    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {

        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            if (isNumeric(value)) {
                Float fValue = (Float) ConvertUtils.convert(value, Float.class);
                return (T) Boolean.valueOf(fValue > 0);
            } else {
                // All the values in the trueStrings and falseStrings arrays are
                // guaranteed to be lower-case. By converting the input value
                // to lowercase too, we can use the efficient String.equals method
                // instead of the less-efficient String.equalsIgnoreCase method.
                String stringValue = value.toString().toLowerCase();
                return (T) toBoolean(type, stringValue);
            }
        }
        throw conversionException(type, value);
    }

    private boolean isNumeric(Object value) {
        return (value instanceof Short || value instanceof Integer || value instanceof Long
                || value instanceof Float || value instanceof Double);
    }

    private Boolean toBoolean(Class type, String stringValue) {
        for (int i = 0; i < trueStrings.length; ++i) {
            if (trueStrings[i].equals(stringValue)) {
                return true;
            }
        }
        for (int i = 0; i < falseStrings.length; ++i) {
            if (falseStrings[i].equals(stringValue)) {
                return false;
            }
        }
        throw conversionException(type, stringValue);
    }

    /**
     * This method creates a copy of the provided array, and ensures that
     * all the strings in the newly created array contain only lower-case
     * letters.
     * <p>
     * Using this method to copy string arrays means that changes to the
     * src array do not modify the dst array.
     */
    private static String[] copyStrings(String[] src) {
        String[] dst = new String[src.length];
        for (int i = 0; i < src.length; ++i) {
            dst[i] = src[i].toLowerCase();
        }
        return dst;
    }
}
