package es.jcyl.ita.formic.core.context;


import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;

import java.util.Date;
import java.util.HashMap;


public abstract class AbstractMapContext extends HashMap<String, Object>
        implements Context, JexlContext {
    private static final long serialVersionUID = -5770693858652716339L;

    protected static final JexlEngine jexl = new JexlBuilder().cache(512)
            .strict(true).silent(false).create();

    private String prefix;

    public AbstractMapContext() {
    }

    public AbstractMapContext(String prefix) {
        setPrefix(prefix);
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        if (prefix == null) {
            this.prefix = "";
        } else {
            this.prefix = prefix;
        }
    }

    @Override
    public String getString(String key) {
        Object o = this.get(key);
        return (o == null) ? null : o.toString();
    }

    @Override
    public Object getValue(String key) {
        return super.get(key);
    }

    /***** JEXL CONTEXT IMPL **/

    @Override
    public Object get(String name) {
        return super.get(name);
    }

    @Override
    public void set(String name, Object value) {
        this.put(name, value);
    }

    @Override
    public boolean has(String name) {
        return containsKey(name);
    }
}
