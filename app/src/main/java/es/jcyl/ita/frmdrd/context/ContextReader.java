package es.jcyl.ita.frmdrd.context;


import es.jcyl.ita.frmdrd.datasource.SourceDescriptor;

public interface ContextReader {

    public void init(SourceDescriptor descriptor);

    public void populate(Context context);
}
