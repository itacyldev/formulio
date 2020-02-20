package es.jcyl.ita.frmdrd.dao.sources;

import java.util.List;

import es.jcyl.ita.frmdrd.dao.persister.Entity;

public interface SourceDescriptor {

    List<Entity> getEntities();
}
