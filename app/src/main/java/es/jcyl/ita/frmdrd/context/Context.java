package es.jcyl.ita.frmdrd.context;


import java.util.Date;
import java.util.Map;

public abstract interface Context extends Map<String, Object> {

    /**
     * Prefix for a
     *
     * @return
     */
    public void setPrefix(String prefix);

    public String getPrefix();


    /**
     * Context creation datetime
     *
     * @return
     */
    public void setCreationDate(Date date);

    public Date getCreationDate();

    /**
     * Returns object referred by the key as String.
     *
     * @param key
     * @return
     */
    public String getString(String key);


    public Object getValue(String key);

}
