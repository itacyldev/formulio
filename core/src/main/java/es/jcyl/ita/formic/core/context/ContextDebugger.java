package es.jcyl.ita.formic.core.context;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ContextDebugger {

    public static final String PRINT_SEPARATOR = "===============================================";

    public static List<String> getPrintable(Context ctx) {
        if (ctx instanceof CompositeContext) {
            return getPrintableComposite((CompositeContext) ctx);
        } else {
            return getPrintableSimple(ctx);
        }
    }

    /**
     * Including linebreaks
     *
     * @param ctx
     * @return
     */
    public static String getPrintableStr(Context ctx) {
        StringBuilder stb = new StringBuilder();
        List<String> ptr = getPrintable(ctx);
        for (String s : ptr) {
            stb.append(s + "\n");
        }
        return stb.toString();
    }

    public static List<String> getPrintableSimple(Context ctx) {
        List<String> printable = new ArrayList<String>();
        Map m = new TreeMap(String.CASE_INSENSITIVE_ORDER);

        // get a map with the key list and their origin
        Map<String, String> keysOrigin = new HashMap<>();
        getContextKeyDescription(ctx, keysOrigin);

        // Store the properties in a Treemap to show them ordered
        for (String key : keysOrigin.keySet()) {
            m.put(key, ctx.getValue(key));
        }
        printable.add(PRINT_SEPARATOR);
        for (Object ks : m.keySet()) {
            printable.add(printPropertyLine(m, keysOrigin, (String) ks));
        }
        printable.add(PRINT_SEPARATOR);
        return printable;
    }

    public static List<String> getPrintableComposite(CompositeContext ctx) {
        List<String> printable = new ArrayList<String>();
        Map m = new TreeMap(String.CASE_INSENSITIVE_ORDER);

        // get a map with the key list and their origin
        Map<String, String> keysOrigin = getKeyDescription(ctx);

        // Store the properties in a Treemap to show them ordered
        for (String key : keysOrigin.keySet()) {
            m.put(key, ctx.getValue(key));
        }

        printable.add(PRINT_SEPARATOR);
        printable.add(ctx.getClass().getName());
        printable.add("========= Loaded contexts ============");
        Collection<Context> collection = getPlainContextList(ctx);

        for (Context context : collection) {
            String line = String.format("\t%s - %s",
                    context.getClass().getSimpleName(), context.getPrefix());
            printable.add(line);
        }
        printable.add(PRINT_SEPARATOR);
        for (Object ks : m.keySet()) {
            printable.add(printPropertyLine(m, keysOrigin, (String) ks));
        }
        printable.add(PRINT_SEPARATOR);
        return printable;
    }


    private static String printPropertyLine(Map m, Map keyDesc, String ks) {
        int PADDING = 35;
        String propValue = String.format("\t%s = %s", ks, m.get(ks));
        int dif = PADDING - propValue.length();
        String pad = (dif <= 0) ? ""
                : new String(new char[dif]).replace('\0', ' ');
        return propValue + pad + " -> " + keyDesc.get(ks);
    }

    private static Map<String, String> getKeyDescription(CompositeContext ctx) {
        Map<String, String> map = new HashMap<String, String>();
        for (final Context context : getPlainContextList(ctx)) {
            getContextKeyDescription(context, map);
        }
        return map;
    }

    private static void getContextKeyDescription(Context context, Map<String, String> map) {
        String key;
        final Iterator<String> e = context.keySet().iterator();
        String confDesc = getConfDescription(context);
        for (; e.hasNext(); ) {
            key = e.next();
            map.put(context.getPrefix() + "." + key, confDesc);
        }
    }

    /**
     * Returns all nested contexts as a plain collection. (Depth-first walk of the context tree).
     *
     * @return
     */
    private static Collection<Context> getPlainContextList(CompositeContext ctx) {
        Collection<Context> configOrig, configDest = null;

        /*
         * Go over the list, copying the contexts, if a composite context is found,
         * replace it with its children contexts
         */
        boolean hayConfigCompuestas = true;
        configOrig = ctx.getContexts();

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

    private static boolean isComposite(Context context) {
        return (context instanceof CompositeContext);
    }


    private static String getConfDescription(Context conf) {
        return conf.getClass().getSimpleName() + " - " + conf.getPrefix();
    }
}
