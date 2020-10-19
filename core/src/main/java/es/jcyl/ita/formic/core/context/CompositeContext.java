package es.jcyl.ita.formic.core.context;

import java.util.Collection;

/**
 * Interface to allow creation of hierarchical contexts
 * 
 * @author ita-riobrigu
 *
 */
public interface CompositeContext extends Context {

	/**
	 * Add new context to the object {@link Context}.
	 * 
	 * @param context
	 * @return CompositeContext.
	 */
	public void addContext(Context context);

	public void addAllContext(Collection<Context> contexts);

	public void removeContext(Context context);

	public void removeContext(String contextId);

	public void removeAllContexts();

	public boolean hasContext(String key);

	public Context getContext(String key);

	/**
	 * Lists added contexts {@link Context}.
	 * 
	 * @return
	 */
	public Collection<Context> getContexts();
}
