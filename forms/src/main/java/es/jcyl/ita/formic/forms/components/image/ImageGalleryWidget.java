package es.jcyl.ita.formic.forms.components.image;

import android.content.Context;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
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

    GridView gridView;
    private Repository repo;
    private List<Entity> entities = new ArrayList<>();

    // view filtering criteria
    private Filter filter;

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
        this.repo = component.getRepo();
    }

    public ImageGalleryWidget(Context context) {
        super(context);
    }

    public void setGridView(GridView gridView){
        this.gridView = gridView;
    }

    public void initialize(RenderingEnv env, Widget<UIAutoComplete> widget, ImageView arrowDropDown) {

    }

    private void loadNextPage() {
        this.entities.clear();
        addData();
    }

    private void addData() {
        List list = this.repo.find(this.filter);



        this.entities.addAll(list);
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
        if (defFilter != null) {
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
}
