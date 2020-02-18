package es.jcyl.ita.frmdrd.dao.collection;

import java.util.ArrayList;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 * @since 15/10/2019.
 */

public class PaginatedList<E> extends ArrayList<E> implements PaginationInfo {

    private static final long serialVersionUID = 6084693971462480222L;

    Integer first;
    Integer pageSize;
    Integer totalResult;

    public void setPaginationInfo(Integer first, Integer pageSize, Integer totalResult) {
        this.totalResult = totalResult;
        this.first = first;
        this.pageSize = pageSize;
        if(this.first == null) {
            this.first = 0;
        }
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    @Override
    public int currentPage() {
        if(this.first >= this.totalResult() || this.first < 0){
            // pedimos un datos fuera de rango la pagina devuelta es -1
            return -1;
        } else {
            return this.first / this.pageSize;
        }
    }

    @Override
    public int currentPageSize() {
        return this.size();
    }

    @Override
    public int pageSize() {
        return this.pageSize;
    }

    public int first() {
        return this.first;
    }

    @Override
    public int totalResult() {
        return this.totalResult;
    }

}
