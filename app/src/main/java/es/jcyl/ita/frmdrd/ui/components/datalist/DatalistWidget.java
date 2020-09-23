package es.jcyl.ita.frmdrd.ui.components.datalist;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.EntitySelector;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.widget.Widget;

public class DatalistWidget extends Widget<UIDatalist>   implements DynamicComponent,
        EntitySelector {

    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
        this.repo = component.getRepo();

    }

    public DatalistWidget(Context context) {
        super(context);
    }

    @Override
    public void load(RenderingEnv env) {

    }

    @Override
    public List<Entity> getSelectedEntities() {
        return null;
    }
}
