package es.jcyl.ita.formic.forms.components.image;

import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.repo.Repository;

public class UIImageGallery extends UIGroupComponent {

    private Repository repo;

    private int numItems;

    public UIImageGallery() {
        rendererType ="imagegallery";
        renderChildren = true;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }
}
