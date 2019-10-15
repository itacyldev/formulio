package es.jcyl.ita.frmdrd;

import dagger.Component;
import es.jcyl.ita.frmdrd.renderer.AbstractFieldRenderer;

@Component
public interface DiComponent {

    void inject(AbstractFieldRenderer abstractFieldRenderer);
}
