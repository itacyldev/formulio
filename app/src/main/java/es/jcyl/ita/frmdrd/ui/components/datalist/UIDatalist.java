package es.jcyl.ita.frmdrd.ui.components.datalist;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class UIDatalist extends UIComponent  implements FilterableComponent {

    Repository repo;

    public Repository getRepo() {
        return repo;
    }

    @Override
    public void setFilter(Filter filter) {

    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public String[] getMandatoryFilters() {
        return new String[0];
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFields) {

    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

}
