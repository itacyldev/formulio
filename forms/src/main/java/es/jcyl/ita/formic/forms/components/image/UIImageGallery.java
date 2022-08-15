package es.jcyl.ita.formic.forms.components.image;

import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.repo.Repository;

public class UIImageGallery extends UIGroupComponent {

    private Repository repo;

    public UIImageGallery() {
        rendererType ="imagegallery";
        renderChildren = true;
    }

}
