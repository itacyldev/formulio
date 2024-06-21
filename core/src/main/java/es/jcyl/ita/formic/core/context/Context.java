package es.jcyl.ita.formic.core.context;


import java.util.Map;

public interface Context extends Map<String, Object> {

    /**
     * Prefix for a
     *
     * @return
     */
    void setPrefix(String prefix);

    String getPrefix();

    /**
     * Returns object referred by the key as String.
     *
     * @param key
     * @return
     */
    String getString(String key);

    Object getValue(String key);

}
