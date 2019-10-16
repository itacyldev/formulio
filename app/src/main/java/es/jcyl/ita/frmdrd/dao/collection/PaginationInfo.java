package es.jcyl.ita.frmdrd.dao.collection;

/**
 *Stores pagination info.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */


public interface PaginationInfo {

    public int currentPage();

    public int pageSize();

    public int currentPageSize();

    public int totalResult();

    public int first();

}
