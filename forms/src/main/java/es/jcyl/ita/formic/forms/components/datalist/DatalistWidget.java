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
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.ContextUtils;
import es.jcyl.ita.formic.forms.components.DynamicComponent;
import es.jcyl.ita.formic.forms.components.EntitySelector;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

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

        this.component.setEntities(this.entities);
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
