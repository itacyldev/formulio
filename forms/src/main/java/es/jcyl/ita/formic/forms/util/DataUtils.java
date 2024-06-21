package es.jcyl.ita.formic.forms.util;

public class DataUtils {
    public static final String NULL_VALUE = "<vacío>";

    public static String nullFormat(final String value) {
        if (value == null || value.length() == 0) {
            return NULL_VALUE;
        }
        return value;
    }

}
