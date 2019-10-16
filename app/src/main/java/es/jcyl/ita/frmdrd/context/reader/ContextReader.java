package es.jcyl.ita.frmdrd.context.reader;


import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.dao.sources.SourceDescriptor;

public interface ContextReader {

    public void init(SourceDescriptor descriptor);

    public void populate(Context context);
}
