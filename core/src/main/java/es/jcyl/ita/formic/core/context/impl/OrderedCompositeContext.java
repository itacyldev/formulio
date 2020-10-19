package es.jcyl.ita.formic.core.context.impl;


import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.jcyl.ita.formic.core.context.AbstractMapContext;
import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;

/**
 * Implements Context aggregation ordering returning keys by context insertion order.
 * <p>
 * The returning values are retrieved using reverse insertion order, so last added contexts will
 * have higher priority when looking for a key.
 */
public class OrderedCompositeContext extends AbstractMapContext
        implements CompositeContext {

    private static final long serialVersionUID = 3866386326635253930L;

    private static final String ASTERISK_CTX = "*";

    private static final Log LOGGER = LogFactory
            .getLog(OrderedCompositeContext.class);

    private static final SimpleDateFormat dtf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * Stores nested contexts
     */
    private final Map<String, Context> contexts = new LinkedHashMap<String, Context>();

    public OrderedCompositeContext() {

    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#setProperty(java.lang.String,
     * java.lang.Object)
     */
    public void setProperty(final String key, final Object value) {
        //If the key doesn't exist is added to the first configuration object
        if (!this.containsKey(key)) {
            this.first().put(key, value);
        } else {
            // Buscamos en la ultima fuente que contenga esa clave
            this.last(key).put(key, value);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#clearProperty(java.lang. String )
     */
    public void clearProperty(final String key) {
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            entry.getValue().remove(key);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#clear()
     */
    @Override
    public void clear() {
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            entry.getValue().clear();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#getValue(java.lang.String)
     */
    public Object getValue(final String prefix, final String key) {
        String prKey = getMapKey(prefix);
        if (!this.contexts.containsKey(prKey)) {
            throw new RuntimeException(String.format(
                    "No existe un contexto con el prefijo: [%s].", prefix));
        } else {
            return this.contexts.get(prKey).getValue(key);
        }
    }

    private String getMapKey(final String prefix) {
        return prefix.toLowerCase();
    }

    public void putValue(final String prefix, final String key,
                         final Object value) {
        String prKey = getMapKey(prefix);
        if (!this.contexts.containsKey(prKey)) {
            throw new RuntimeException(String.format(
                    "There's no context with the prefix [%s].", prefix));
        } else {
            this.contexts.get(prKey).put(key, value);
        }
    }

    public void putAllValues(final String prefix, Map<String, Object> values) {
        String prKey = getMapKey(prefix);
        if (!this.contexts.containsKey(prKey)) {
            throw new RuntimeException(String.format(
                    "There's no context with the prefix [%s].", prefix));
        } else {
            this.contexts.get(prKey).putAll(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#getValue(java.lang.String)
     */
    public Object getValue(final String key) {
        Object result = null;

        if (key.contains(".")) {
            // si la clave tiene prefijo, lo extraemos primero y buscamos solo
            // en ese contexto
            String[] parts = key.split("\\.");
            String prefix = parts[0];
            String new_key = parts[1];
            if (parts.length > 2) {
                // si hay mas de un punto, se ha incluido un expresion EXL para
                // acceder, se concatenan las partes
                new_key = key.substring(prefix.length() + 1);
            }
            if (ASTERISK_CTX.equals(prefix)) {
                result = getValuesByKey(new_key);
            } else {
                result = getValue(prefix, new_key);
            }
        } else {
            // // buscar y devolver un contexto
            if (this.hasContext(key)) {
                return this.contexts.get(key);
            }

            // busqueda de la clave dentro de los contextos en orden inverso
            // (�ltimo contexto a�adido tiene prioridad)
            List<String> keyList = new ArrayList<String>(
                    this.contexts.keySet());

            Collections.reverse(keyList);
            for (int i = 0; i < keyList.size(); i++) {
                String prefix = keyList.get(i);
                Context ctx = getContext(prefix);
                if (ctx.containsKey(key)) {
                    return ctx.get(key);
                }
            }
        }
        return result;
    }

    private Object getValuesByKey(String key) {
        List<Object> result = new LinkedList<Object>();
        // si hay una expresi�n posterior, hay que extraerla antes de acceder a
        // las variables
        String new_key = key;
        String expression = "";
        String separator = "";
        if (key.contains(".")) {
            new_key = key.split("\\.")[0];
            expression = key.substring(new_key.length() + 1);
            separator = ".";
        }
        if (key.contains("[")) {
            new_key = key.split("\\[")[0];
            expression = key.substring(new_key.length() + 1);
            separator = "[";
        }
        // recorrer contexto en orden inverso al que se a�aden buscando
        // propiedades con la misma clave.
        List<String> keyList = new ArrayList<String>(this.contexts.keySet());
        Collections.reverse(keyList);
        for (int i = 0; i < keyList.size(); i++) {
            String prefix = keyList.get(i);
            Context ctx = getContext(prefix);
            if (ctx.containsKey(new_key)) {
                result.add(ctx.get(new_key));
            }
        }

        if (StringUtils.isEmpty(expression)) {
            return result;
        } else {
            // aplicamos la expresion sobre la colecci�n resultante de a�adir
            // todas las variables
            JexlContext context = new MapContext();
            context.set("list", result);
            expression = "list" + separator + expression;
            JexlExpression exl = jexl.createExpression(expression);
            try {
                return exl.evaluate(context);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Se ha producido un error al intentar interpretar la expresi�n \"%s\". "
                                + "Comprueba que existen las propiedades necesarias en el contexto.",
                        key), e);
            }
        }
    }

    @Override
    public Object get(Object key) {
        return getValue((String) key);
    }


    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#containsKey(java.lang.String)
     */
    public boolean containsKey(final String key) {
        return getValue(key) != null;
    }

    @Override
    public boolean containsKey(Object key) {
        return containsKey((String) key);
    }

    public boolean containsKey(final String prefix, final String key) {
        Context context = getContext(prefix);
        if (context == null) {
            return false;
        } else {
            return context.containsKey(key);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#getKeys()
     */
    public Enumeration<String> getKeys() {
        final Set<String> allKeys = new LinkedHashSet<String>();

        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            // cada clave tiene que ir precedida del prefijo
            Set<String> contextKeys = new HashSet<String>();
            for (String s : entry.getValue().keySet()) {
                contextKeys.add(entry.getKey() + "." + s);
            }
            allKeys.addAll(contextKeys);
        }
        return Collections.enumeration(allKeys);
    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.Context#getKeys(java.lang.String)
     */
    public Enumeration<String> getKeys(final String prefix) {
        Context ctx = this.getContext(prefix);
        if (ctx == null) {
            return null;
        } else {
            return Collections.enumeration(ctx.keySet());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.CompositeContext#addContext
     * (es.jcyl.ita.fwk.Context.Context)
     */
    @Override
    public void addContext(final Context context) {
        if (context instanceof CompositeContext) {
            addAllContext(((CompositeContext) context).getContexts());
        } else {
            String prKey = getMapKey(context.getPrefix());
            this.contexts.put(prKey, context);
        }
    }

    @Override
    public boolean hasContext(String prefix) {
        return (this.getContext(prefix) != null);
    }

    public boolean hasContext(Context ctx) {
        if (ctx.getPrefix() != null) {
            return (this.getContext(ctx.getPrefix()) != null);
        } else {
            return this.contexts.containsValue(ctx);
        }
    }

    @Override
    public Context getContext(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null.");
        }
        return this.contexts.get(getMapKey(prefix));
    }

    @Override
    public void removeContext(final Context context) {
        this.contexts.remove(context.getPrefix());
    }

    @Override
    public void removeContext(String contextId) {
        this.contexts.remove(contextId);

    }

    @Override
    public void addAllContext(Collection<Context> contexts) {
        for (Context ctx : contexts) {
            this.addContext(ctx);
        }
    }

    /*
     * Copia protegida ?? clone (non-Javadoc)
     *
     * @see es.jcyl.ita.fwk.Context.CompositeContext#getContexts()
     */
    @Override
    public Collection<Context> getContexts() {
        return this.contexts.values();
    }

    /**
     * Busca la �ltima configuraci�n jer�rquicamente donde se encuetra la clave
     * buscada. Devuelve <code>null</code> si no la encuentra.
     *
     * @param key
     * @return Context
     */
    protected Context last(final String key) {
        Context result = null;

        final ListIterator<Context> iterator = this.iterator();

        while (iterator.hasPrevious()) {
            final Context context = iterator.previous();

            if (context.containsKey(key)) {
                result = context;
                break;
            }
        }

        return result;
    }

    /**
     * Iterator to go through all stored contexts.
     *
     * @return
     */
    protected ListIterator<Context> iterator() {
        final ListIterator<Context> iterator = new ArrayList<Context>(
                this.getContexts()).listIterator(this.getContexts().size());
        return iterator;
    }

    /**
     * Get the first stored context.
     *
     * @return
     */
    protected Context first() {
        for (final Context Context : this.getContexts()) {
            return Context;
        }

        return null;
    }

    public void debugContext() {
        if (LOGGER.isDebugEnabled()) {
            List<String> printable = this.getPrintable();
            for (String str : printable) {
                LOGGER.debug(str);
            }
        }
    }

    private String printPropertyLine(Map m, Map keyDesc, String ks) {
        int PADDING = 35;
        String propValue = String.format("\t%s = %s", ks, m.get(ks));
        int dif = PADDING - propValue.length();
        String pad = (dif <= 0) ? ""
                : new String(new char[dif]).replace('\0', ' ');

        return propValue + pad + " -> " + keyDesc.get(ks);
    }

    private boolean isComposite(Context context) {
        return (context instanceof CompositeContext);
    }

    /**
     * Returns all nested contexts as a plain collection. (Depth-first walk of the context tree).
     *
     * @return
     */
    private Collection<Context> getPlainContextList() {
        Collection<Context> configOrig, configDest = null;

        /*
         * Go over the list, copying the contexts, if a composite context is found,
         * replace it with its children contexts
         */
        boolean hayConfigCompuestas = true;
        configOrig = this.getContexts();

        while (hayConfigCompuestas) {
            hayConfigCompuestas = false;

            configDest = new LinkedList<Context>();
            for (Context context : configOrig) {
                if (!isComposite(context)) {
                    configDest.add(context);
                } else {
                    // a�adimos solo las hijas
                    configDest
                            .addAll(((CompositeContext) context).getContexts());
                    hayConfigCompuestas = true;
                }
            }
            if (hayConfigCompuestas) {
                // cambiamos origen-destinoa y volvemos a recorrer para
                // asegurarnos de que no hay conf. compuestas
                configOrig = configDest;
                configDest = null;
            }
        }
        return configDest;
    }

    public void print(PrintStream out) {
        try {
            for (String line : getPrintable()) {
                out.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error trying to print the content of the context.", e);
        }
    }

    public List<String> getPrintable() {
        List<String> printable = new ArrayList<String>();
        Map m = new TreeMap(String.CASE_INSENSITIVE_ORDER);

        // get a map with the key list and their origin
        Map<String, String> keysOrigin = this.getKeyDescription();

        // Store the properties in a Treemap to show them ordered
        for (String key : keysOrigin.keySet()) {
            m.put(key, this.getValue(key));
        }

        printable.add("===============================================");
        printable.add(this.getClass().getName());
        printable.add("========= Loaded contexts ============");
        Collection<Context> collection = this.getPlainContextList();

        for (Context context : collection) {
            String dateF = "";
            if (context.getCreationDate() != null) {
                dateF = dtf.format(context.getCreationDate());
            }
            String line = String.format("\t%s - %s - %s",
                    context.getClass().getSimpleName(), context.getPrefix(),
                    dateF);
            printable.add(line);
        }
        printable.add("===============================================");
        for (Object ks : m.keySet()) {
            printable.add(this.printPropertyLine(m, keysOrigin, (String) ks));
        }
        printable.add("===============================================");
        return printable;
    }

    private Map<String, String> getKeyDescription() {
        String key;
        Map<String, String> map = new HashMap<String, String>();

        for (final Context context : this.getPlainContextList()) {
            final Iterator<String> e = context.keySet().iterator();
            String confDesc = this.getConfDescription(context);
            for (; e.hasNext(); ) {
                key = e.next();
                map.put(context.getPrefix() + "." + key, confDesc);
            }
        }
        return map;
    }

    private String getConfDescription(Context conf) {
        return conf.getClass().getSimpleName() + " - " + conf.getPrefix();
    }

    @Override
    public Set<String> keySet() {
        Enumeration<String> keys = getKeys();
        return new HashSet<String>(Collections.list(keys));
    }

    @Override
    public int size() {
        int size = 0;
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.contexts.isEmpty();
    }

    @Override
    public Object put(String key, Object value) {
        if (key.contains(".")) {
            // if the key includes a prefix, extract the prefix first and look for the key
            // just in that context
            String[] parts = key.split("\\.");
            String prefix = parts[0];
            String new_key = parts[1];
            if (parts.length > 2) {
                // If there's more that one point, there's and EXL expression, concat all parts
                new_key = key.substring(prefix.length() + 1);
            }
            putValue(prefix, new_key, value);
            return value;
        } else {
            throw new UnsupportedOperationException(
                    "Cannot execute put operation on a composite context. To insert a value in a context, " +
                            "Use the nested context prefix to set the value. Ex:  \"ctx.key\".");
        }
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public Collection<Object> values() {
        List<Object> lst = new ArrayList<Object>();
        for (Entry<String, Context> entry : this.contexts.entrySet()) {
            lst.addAll(entry.getValue().values());
        }
        return lst;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {

        Set<Entry<String, Object>> globalSet = new HashSet<Entry<String, Object>>();
        String fullKey;
        for (Entry<String, Context> entry : this.contexts.entrySet()) {

            for (Entry<String, Object> entryCtx : entry.getValue()
                    .entrySet()) {
                // add new entries with children contexts prefixes
                fullKey = entry.getKey() + "." + entryCtx.getKey();
                globalSet.add(new SimpleEntry(fullKey,
                        entryCtx.getValue()));
            }
        }
        return globalSet;
    }

    @Override
    public void removeAllContexts() {
        this.contexts.clear();
    }

}
