package es.jcyl.ita.formic.forms.components.datalist;
/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.controllers.widget.GroupWidgetController;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.selection.EntitySelectorWidget;
import es.jcyl.ita.formic.forms.view.selection.SelectionManager;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.EntityListProviderWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class DatalistWidget extends Widget<UIDatalist> implements EntityListProviderWidget,
        EntitySelectorWidget, ControllableWidget {

    private Repository repo;
    private RenderingEnv renderingEnv;
    private List<Entity> entities = new ArrayList<>();
    private GroupWidgetController controller;

    // view filtering criteria
    private Filter filter;

    private AndViewContext thisViewCtx = new AndViewContext(this);
    private LinearLayout contentView;
    private SelectionManager selectionMgr;
    private List<DatalistItemWidget> items;

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
        this.repo = component.getRepo();
    }

    public DatalistWidget(Context context) {
        super(context);
    }

    public DatalistWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatalistWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void load(RenderingEnv env) {
        this.renderingEnv = env;
        // set filter to repo using current view data
        this.entities.clear();
        CompositeContext ctx = setupThisContext(env);
        this.filter = setupFilter(ctx, this.getComponent().getFilter());
        // read first page to render data
        loadNextPage();
    }

    public void setContentView(LinearLayout contentView) {
        this.contentView = contentView;
    }

    public LinearLayout getContentView() {
        return this.contentView;
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
        if (defFilter != null) {
            FilterHelper.evaluateFilter(context, defFilter, f);
        }
        return f;
    }

    @Override
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public WidgetController getController() {
        return this.controller;
    }

    public void setController(GroupWidgetController controller) {
        this.controller = controller;
    }

    @Override
    public void setSelectionManager(SelectionManager manager) {
        this.selectionMgr = manager;
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public Repository getRepository() {
        return this.component.getRepo();
    }

    public void setItems(List<DatalistItemWidget> widgets) {
        this.items = widgets;
    }

    public List<DatalistItemWidget> getItems() {
        return items;
    }
}
