package es.jcyl.ita.frmdrd.ui.components.datalist;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

public class UIDatalist extends UIComponent {

    Repository repo;

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

}
