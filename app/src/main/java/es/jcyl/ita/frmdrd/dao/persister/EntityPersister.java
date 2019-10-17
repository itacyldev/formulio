package es.jcyl.ita.frmdrd.dao.persister;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public interface EntityPersister {

    void save(Entity entity);

    void delete(Object[] id) ;

}
