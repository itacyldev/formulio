package es.jcyl.ita.frmdrd.dao.loader;

import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.persister.EntityMeta;

/**
 * @author Gustavo R�o (gustavo.rio@itacyl.es)
 */

public interface EntityLoader {

    Entity byId(Object id);

    boolean exists(Object id);

}
