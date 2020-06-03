package es.jcyl.ita.frmdrd.converters;

import org.mini2Dx.collections.BidiMap;
import org.mini2Dx.collections.bidimap.DualHashBidiMap;

public class ConverterMap {

    private static ConverterMap _instance;

    private static BidiMap _converters = new DualHashBidiMap();

    public static ConverterMap getInstance() {
        if (_instance == null) {
            _instance = new ConverterMap();
        }
        return _instance;
    }

    private ConverterMap() {
        registerConverter("integer", Integer.class);
        registerConverter("short", Short.class);
        registerConverter("long", Long.class);

        registerConverter("float", Float.class);
        registerConverter("double", Double.class);

        registerConverter("string", String.class);
    }

    public void registerConverter(String type, Class converterClass) {
        _converters.put(type, converterClass);
    }


    public static String getConverter(Class type) {
        return (String) _converters.getKey(type);
    }

    public static Class getConverter(String type) {
        return (Class) _converters.get(type);
    }
}
