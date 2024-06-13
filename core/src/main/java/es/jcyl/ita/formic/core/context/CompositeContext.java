package es.jcyl.ita.formic.core.context;

import java.util.Collection;

/**
 * Interface to allow creation of hierarchical contexts
 *
 * @author ita-riobrigu
 */
public interface CompositeContext extends Context {

    /**
     * Add new context to the object {@link Context}.
     *
     * @param context
     * @return CompositeContext.
     */
    void addContext(Context context);

    void addAllContext(Collection<Context> contexts);

    void removeContext(Context context);

    void removeContext(String contextId);

    void removeAllContexts();

    boolean hasContext(String key);

    Context getContext(String key);

    /**
     * Lists added contexts {@link Context}.
     *
     * @return
     */
    Collection<Context> getContexts();
}
