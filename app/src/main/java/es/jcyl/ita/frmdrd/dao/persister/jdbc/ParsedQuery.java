package es.jcyl.ita.frmdrd.dao.persister.jdbc;

import es.jcyl.ita.frmdrd.dao.persister.PersisterException;

/**
 * @author Gustavo R�o (gustavo.rio@itacyl.es)
 */

public interface ParsedQuery {

    String getQueryString();

    String[] getReturnTypes() throws PersisterException;

    String[] getNamedParameters() throws PersisterException;

}