package es.jcyl.ita.frmdrd.dao.persister.jdbc;

import es.jcyl.ita.frmdrd.dao.loader.EntityLoader;
import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.persister.EntityPersister;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class SQLEntityPersiser implements EntityLoader, EntityPersister {

    @Override
    public void save(Entity entity) {

   }

    @Override
    public void delete(Object[] id) {

    }

    @Override
    public Entity byId(Object id) {
        return null;
    }

    @Override
    public boolean exists(Object id) {
        return false;
    }
}
