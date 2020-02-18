package es.jcyl.ita.frmdrd.dao.persister;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public interface EntityPersister {

    void save(Entity entity);

    void delete(Object[] id) ;

}
