package es.jcyl.ita.formic.jayjobs.utils;

import com.android.volley.Request;

import java.util.Arrays;

public class VolleyUtils {
    private static final String[] HTTP_METHODS = {"GET", "POST", "DELETE", "PUT", "PATCH", "HEAD", "OPTIONS", "TRACE"};
    private static final int[] VOLLEY_VALUE = {Request.Method.GET, Request.Method.POST, Request.Method.DELETE,
            Request.Method.PUT, Request.Method.PATCH, Request.Method.HEAD, Request.Method.OPTIONS,
            Request.Method.TRACE};

    public static String getMethodName(int method) {
        for (int i = 0; i < VOLLEY_VALUE.length; i++) {
            if (VOLLEY_VALUE[i] == method) {
                return HTTP_METHODS[i];
            }
        }
        throw new IllegalArgumentException("Invalid http Volley method: " + method + " allowed :" + Arrays.asList(VOLLEY_VALUE));
    }

    /**
     * Translantes the method name to Volley method constant
     *
     * @param methodName
     * @return
     */
    public static int getMethodFromString(String methodName) {
        String uppMethodName = methodName.toUpperCase();
        for (int i = 0; i < HTTP_METHODS.length; i++) {
            if (HTTP_METHODS[i].equals(uppMethodName)) {
                return VOLLEY_VALUE[i];
            }
        }
        throw new IllegalArgumentException("Invalid http method: " + methodName + " allowed :" + Arrays.asList(HTTP_METHODS));
    }
}
