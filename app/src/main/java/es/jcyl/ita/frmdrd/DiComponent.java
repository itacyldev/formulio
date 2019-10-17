package es.jcyl.ita.frmdrd;

import dagger.Component;
import es.jcyl.ita.frmdrd.render.AbstractFieldRenderer;

@Component
public interface DiComponent {

    void inject(AbstractFieldRenderer abstractFieldRenderer);
}
