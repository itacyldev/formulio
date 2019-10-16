package es.jcyl.ita.frmdrd.context.impl;

import org.apache.commons.jexl3.JexlExpression;

import es.jcyl.ita.frmdrd.context.AbstractContext;


public class BasicContext extends AbstractContext {

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
        // intentamos obtener el valor directamente con la clave
        Object obj = super.get(key);
        if (obj != null) {
            return obj;
        } else {
            if (!hasExlExpressison((String) key)) {
                return super.get(key);
            } else {
                // comprobar si existe la propiedad sobre la que se quiere
                // obtener la expresión
                String prp = getPropertyForExpression((String) key);
                if (prp == null || !this.containsKey(prp)) {
                    // la propiedad no existe, devolver null directamente
                    return null;
                }
                JexlExpression e = jexl.createExpression((String) key);
                return e.evaluate(this);
            }
        }
    }

}
