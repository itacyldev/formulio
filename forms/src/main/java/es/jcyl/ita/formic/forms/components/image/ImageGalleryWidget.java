package es.jcyl.ita.formic.forms.components.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.selection.EntitySelectorWidget;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.EntityListProviderWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;

public class ImageGalleryWidget extends Widget<UIImageGallery> implements EntityListProviderWidget,
        EntitySelectorWidget, ControllableWidget {

    private ViewValueConverter converter;

    private AndViewContext thisViewCtx = new AndViewContext(this);
    GridView gridView;
    private Repository repo;
    private List<Entity> entities = new ArrayList<>();

    // view filtering criteria
    private Filter filter;


    public ImageGalleryWidget(Context context) {
        super(context);
    }

    public ImageGalleryWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGalleryWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
        this.repo = component.getRepo();
//        LinearLayout contentView = findViewById(R.id.datalist_content_layout);
//        setContentView(contentView);
        load(env);

    }

    public void load(RenderingEnv env) {
        // set filter to repo using current view data
        this.entities.clear();
        CompositeContext ctx = setupThisContext(env);
        this.filter = setupFilter(ctx, this.getComponent().getFilter());
        // read first page to render data
        loadNextPage();
    }

    private void loadNextPage() {
        this.entities.clear();
        addData();
    }

    private void addData() {
        List list = this.repo.find(this.filter);
        this.entities.addAll(list);
    }

    private CompositeContext setupThisContext(RenderingEnv env) {
        thisViewCtx.setPrefix("this");
        // use default widgetContext for initialRendering
        WidgetContext widgetContext = (this.getWidgetContext() != null)
                ? this.getWidgetContext() : env.getWidgetContext();
        CompositeContext ctx = ContextUtils.combine(widgetContext, thisViewCtx);
        return ctx;
    }

    /**
     * Get the definition filter from the dataList and construct an effective filter using the
     * context information.
     *
     * @param context
     * @return
     */
    private Filter setupFilter(CompositeContext context, Filter defFilter) {
        Filter f = FilterRepoUtils.createInstance(repo);
        if (defFilter != null && f != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        return f;
    }

    @Override
    public void setSelectionManager(SelectionManager manager) {

    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public WidgetController getController() {
        return null;
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public Repository getRepository() {
        return this.component.getRepo();
    }

    public ViewValueConverter getConverter() {
        return converter;
    }

    public void setConverter(ViewValueConverter converter) {
        this.converter = converter;
    }

    public void setAdapter(ImageListAdapter adapter) {
        this.gridView.setAdapter(adapter);
    }
}
