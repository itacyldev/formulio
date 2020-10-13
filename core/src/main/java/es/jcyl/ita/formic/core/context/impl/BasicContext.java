package es.jcyl.ita.formic.core.context.impl;

import org.apache.commons.jexl3.JexlExpression;

import es.jcyl.ita.formic.core.context.AbstractMapContext;


/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class BasicContext extends AbstractMapContext {

    private static final long serialVersionUID = 4993728250907939173L;

    public BasicContext() {
        super();
    }

    public BasicContext(String prefix) {
        super(prefix);
    }

    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append(String.format("[%s]: ", this.getPrefix()));
        stb.append(super.toString());
        return stb.toString();
    }

    @Override
    public Object get(Object key) {
        Object obj = super.get(key);
        if (obj != null) {
            return obj;
        } else {
            if (!hasExlExpressison((String) key)) {
                return super.get(key);
            } else {
                String prp = getPropertyForExpression((String) key);
                if (prp == null || !this.containsKey(prp)) {
                    return null;
                }
                JexlExpression e = jexl.createExpression((String) key);
                return e.evaluate(this);
            }
        }
    }

}
