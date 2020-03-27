package es.jcyl.ita.frmdrd.context;


import android.view.View;

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

    private Date creationDate;
    private String prefix;

    public AbstractMapContext() {
        this.creationDate = new Date();
    }

    public AbstractMapContext(String prefix) {
        this.creationDate = new Date();
        setPrefix(prefix);
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    protected boolean hasExlExpressison(String str) {
        return str.contains("[") || str.contains("(") || str.contains(".");
    }

    protected String getPropertyForExpression(String str) {
        int dotPos = str.indexOf('.');
        dotPos = (dotPos < 0) ? Integer.MAX_VALUE : dotPos;
        int bracketPos = str.indexOf('[');
        bracketPos = (bracketPos < 0) ? Integer.MAX_VALUE : bracketPos;
        int parenthesesPos = str.indexOf('[');
        parenthesesPos = (parenthesesPos < 0) ? Integer.MAX_VALUE
                : parenthesesPos;

        int min = Math.min(dotPos, Math.min(bracketPos, parenthesesPos));
        if (min < 0) {
            return null;
        } else {
            return str.substring(0, min);
        }
    }

    @Override
    public String getString(String key) {
        Object o = this.get(key);
        if (o == null) {
            return null;
        } else {
            return o.toString();
        }
    }

    @Override
    public Object getValue(String key) {
        Object o = this.get(key);
        return o;
    }

    /***** JEXL CONTEXT IMPL **/

    @Override
    public Object get(String name) {
        return this.getValue(name);
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
