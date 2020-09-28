package es.jcyl.ita.frmdrd.ui.components.datalist;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.EntitySelector;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.widget.Widget;

public class DatalistWidget extends Widget<UIDatalist> implements DynamicComponent,
        EntitySelector {

    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();

    private List<Entity> selectedEntities = new ArrayList<>();

    // view filtering criteria
    private Filter filter;

    private AndViewContext thisViewCtx = new AndViewContext(this);

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
        this.renderingEnv = env;

        // set filter to repo using current view data
        this.entities.clear();

        CompositeContext ctx = setupThisContext(env);
        this.filter = setupFilter(ctx, this.getComponent().getFilter());
        // read first page to render data
        loadNextPage();
    }

    @Override
    public List<Entity> getSelectedEntities() {
        return selectedEntities;
    }

    public void selectEntity(Entity entity) {
        this.selectedEntities.add(entity);
    }

    public void clearSelection() {
        this.selectedEntities.clear();
    }

    public void setContentLayout(LinearLayout datalistContentLayout) {

    }

    private void loadNextPage() {
        //this.entities.clear();
        addData();
    }

    private void reloadData() {
        this.entities.clear();
        addData();
    }

    private void addData() {
        this.entities.addAll(this.repo.find(this.filter));


    }

    private CompositeContext setupThisContext(RenderingEnv env) {
        thisViewCtx.setPrefix("this");
        CompositeContext ctx = ContextUtils.combine(env.getContext(), thisViewCtx);
        return ctx;
    }

    /**
     * Get the definition filter from the dataTable and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private Filter setupFilter(CompositeContext context, Filter defFilter) {
        Filter f = FilterHelper.createInstance(repo);
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        return f;
    }
}
