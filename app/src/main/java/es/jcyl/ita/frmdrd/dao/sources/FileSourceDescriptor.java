package es.jcyl.ita.frmdrd.dao.sources;

import java.util.List;

import es.jcyl.ita.frmdrd.dao.persister.Entity;

public class FileSourceDescriptor implements SourceDescriptor {

    private String path;

    @Override
    public List<Entity> getEntities() {
        return null;
    }
}
