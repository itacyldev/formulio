package es.jcyl.ita.formic.forms.view.render.renderer;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.view.View;
import android.view.ViewGroup;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.mini2Dx.collections.MapUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.DynamicComponent;
import es.jcyl.ita.formic.forms.components.EntityHolder;
import es.jcyl.ita.formic.forms.components.EntityListProvider;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.datalist.UIDataListItemProxy;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.dag.DAGNode;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.DeferredView;
import es.jcyl.ita.formic.forms.view.render.GroupRenderer;
import es.jcyl.ita.formic.forms.view.render.Renderer;
import es.jcyl.ita.formic.forms.view.render.RendererFactory;
import es.jcyl.ita.formic.forms.view.render.ViewRendererEventHandler;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;

/**
 * Creates Android view elements from UIComponents definitions
 * <p>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewRenderer {

    private ViewRendererEventHandler eventHandler = new NoOpHandler();

    public Widget render(RenderingEnv env, UIComponent component) {
        return render(env, component, true);
    }

    private Widget render(RenderingEnv env, UIComponent component, boolean checkDeferred) {
        String rendererType = component.getRendererType();
        Renderer renderer = this.getRenderer(rendererType);

        eventHandler.onBeforeRenderComponent(component);
        // setup entity in context
        if (component instanceof EntityHolder) {
            // enrich the execution environment with current form's context
            Entity entity = ((EntityHolder) component).getEntity();
            env.setEntity(entity);
            eventHandler.onEntityContextChanged(entity);
        }
        // render android view
        Widget widget;
        if (checkDeferred && hasDeferredExpression(component, env)) {
            // insert a delegated view component as placeholder to render later
            widget = createDeferredView(env, component);
        } else {
            widget = renderer.render(env, component);
        }
        // link root widget to current widget
        if (widget instanceof ViewWidget) {
            env.setRootWidget((ViewWidget) widget);
        }
        registerWidget(env, widget);
        eventHandler.onAfterRenderComponent(widget);

        // if current view is not visible or is pending of evaluation, don't render children
        if (!ViewHelper.isVisible(widget) || (widget instanceof DeferredView)) {
            return widget;
        }

        // render children if needed
        if (renderer instanceof GroupRenderer) {
            GroupRenderer gRenderer = (GroupRenderer) renderer;
            if (component.isRenderChildren() && component.hasChildren()) {
                // recursively render children components
                Widget groupView = (Widget) widget;
                gRenderer.initGroup(env, groupView);

                List<View> viewList = new ArrayList<>();
                if (groupView instanceof EntityListProvider) {
                    // save the old entityContext
                    Entity oldEntity = env.getEntity();

                    List<Entity> entities = ((EntityListProvider) groupView).getEntities();
                    int iter = 0;
                    // TODO: FORMIC-249 Refactorizar viewRenderer
                    for (Entity entity : entities) {
                        // create an EntityContext to render each entity
//                        env.setEntity(entity);
                        eventHandler.onEntityContextChanged(entity);
                        View view = render(env, proxify(iter, component.getChildren()[0], entity));
                        viewList.add(view);
                        iter++;
                    }
                    // restore entity context
                    env.getWidgetContext().setEntity(oldEntity);
                    eventHandler.onEntityContextChanged(oldEntity);
                } else {
                    UIComponent[] kids = component.getChildren();
                    int numKids = kids.length;
                    for (int i = 0; i < numKids; i++) {
                        View view = render(env, kids[i]);
                        viewList.add(view);
                    }
                }
                gRenderer.addViews(env, groupView, viewList.toArray(new View[viewList.size()]));
                gRenderer.endGroup(env, groupView);
            }
        }
        if (checkDeferred && component.getParent() == null) {
            // last step in the tree walk, process delegates when we're back on the view root
            processDeferredViews(env);
        }
        return widget;
    }

    /**
     * Creates a component proxy for curren elemnt
     *
     * @param id
     * @param component
     * @param entity
     * @return
     */
    private UIComponent proxify(int id, UIComponent component, Entity entity) {
        if (component instanceof UIDatalistItem) {
            String cId = component.getId();
            // set component id to item-1,item-2, starting with 1
            return new UIDataListItemProxy(cId + "-" + (id + 1), component, entity);
        } else {
            return component;
        }
    }

    /**
     * Links the created widget with the widgetContext and the root ViewWidget
     *
     * @param env
     * @param widget
     */
    private void registerWidget(RenderingEnv env, Widget widget) {
        if (widget instanceof WidgetContextHolder) {
            // create widgetContext and set to current widget
            WidgetContext wCtx = new WidgetContext((WidgetContextHolder) widget);
            // set the entity used to render this widget in its context
            wCtx.setEntity(env.getEntity());
            // add global context
            wCtx.addContext(env.getContext());
            widget.setWidgetContext(wCtx);
            // set as current WidgetContext so nested elements will use it
            env.setWidgetContext(wCtx);
            eventHandler.onWidgetContextChange(wCtx);
            // register current widget in view
            if (env.getRootWidget() != null) {
                env.getRootWidget().registerContextHolder((WidgetContextHolder) widget);
            }
        } else {
            widget.setWidgetContext(env.getWidgetContext());
            if (env.getWidgetContext() != null) {
                env.getWidgetContext().registerWidget(widget);
            }
        }
        widget.setRootWidget(env.getRootWidget());
    }

    private Widget createDeferredView(RenderingEnv env, UIComponent component) {
        DeferredView view = new DeferredView(env.getAndroidContext(), component);
        env.addDeferred(component.getAbsoluteId(), view);
        return view;
    }

    protected Renderer getRenderer(String rendererType) {
        Renderer renderer = RendererFactory.getInstance().getRenderer(rendererType);
        return renderer;
    }

    public void replaceView(Widget widget1, Widget widget2) {
        ViewGroup parent = (ViewGroup) widget1.getParent();
        int index = parent.indexOfChild(widget1);
        if (widget1.getLayoutParams() != null) {
            widget2.setLayoutParams(widget1.getLayoutParams());
        }
        widget2.setWidgetContext(widget1.getWidgetContext());
        parent.removeView(widget1);
        parent.addView(widget2, index);
    }


    /**
     * Checks if the component has a bindingExpression that depends on view components.
     *
     * @param component
     * @param env
     * @return
     */
    private boolean hasDeferredExpression(UIComponent component, RenderingEnv env) {
        if (component.getValueBindingExpressions() != null) {
            for (ValueBindingExpression expr : component.getValueBindingExpressions()) {
                if (expr.toString().contains("view.")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Replaces the deferred views inserted in the tree with their proper elements.
     * It uses a DAG tree to evaluate the expressions following their dependencies so the final
     * result is correct.
     *
     * @param env: rendering environment
     */
    private void processDeferredViews(RenderingEnv env) {
        // use dag to walk the tree just one time per node
        ViewDAG viewDAG = env.getViewDAG();
        Map<String, DeferredView> deferredViews = env.getDeferredViews();
        if (viewDAG == null || deferredViews == null) {
            return;
        }
        // Until all deferred views has been processed
        while (MapUtils.isNotEmpty(deferredViews)) {
            for (DirectedAcyclicGraph<DAGNode, DefaultEdge> dag : viewDAG.getDags().values()) {
                // follow dag evaluating expressions and rendering views
                for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
                    DAGNode node = it.next();
                    // find deferredView for this component
                    Widget defView = deferredViews.remove(node.getComponent().getAbsoluteId());
                    if (defView != null) {
                        // render the view and replace deferred element
                        RenderingEnv widgetRendEnv = RenderingEnv.clone(env);
                        widgetRendEnv.setWidgetContext(defView.getWidgetContext());
                        Widget newWidget = this.render(widgetRendEnv, node.getComponent(), false);
                        registerWidget(env, newWidget);
                        replaceView(defView, newWidget);
                    }
                }
            }
        }
    }

    /**
     * Given an element in current view, renders all the dependant elements
     */
    public void renderDeps(RenderingEnv env, Widget widget) {
        // get element dags
        UIComponent component = widget.getComponent();
        ViewDAG viewDAG = env.getViewDAG();
        if (viewDAG == null || viewDAG.getDags().size() == 0) {
            return;
        }
        DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = viewDAG.getDAG(component.getAbsoluteId());
        if (dag == null) {
            return;// no dependant components
        }
        // Walk the tree in topological order to follow the dependencies from the current element
        // sets the rendering starting point, when given element is found in the DAG
        boolean found = false;

        for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
            DAGNode node = it.next();
            if (!found) {
                if (node.getComponent().getId().equals(component.getId())) {
                    found = true; // start rendering in next element
                }
            } else {
                // find object locally to widgetContext
                Widget dependantWidget = ViewHelper.findComponentWidget(
                        widget.getWidgetContext().getHolder().getWidget(), node.getComponent());
                if (dependantWidget == null) {
                    // look from top of the view
                    dependantWidget = ViewHelper.findComponentWidget(env.getRootWidget(), node.getComponent());
                }
                if (dependantWidget instanceof DynamicComponent) {
                    // update widget content internally
                    ((DynamicComponent) dependantWidget).load(env);
                } else {
                    // update widget content using render flow
                    RenderingEnv widgetRendEnv = RenderingEnv.clone(env);
                    if (dependantWidget != null) {
                        widgetRendEnv.setWidgetContext(dependantWidget.getWidgetContext());
                        Widget newWidget = this.render(widgetRendEnv, node.getComponent(), false);
                        registerWidget(env, newWidget);
                        replaceView(dependantWidget, newWidget);
                    }
                }
            }
        }
    }

    public void setEventHandler(ViewRendererEventHandler handler) {
        this.eventHandler = handler;
    }

    private class NoOpHandler implements ViewRendererEventHandler {
        @Override
        public void onEntityContextChanged(Entity entity) {
        }

        @Override
        public void onWidgetContextChange(WidgetContext context) {
        }

        @Override
        public void onBeforeRenderComponent(UIComponent component) {
        }

        @Override
        public void onAfterRenderComponent(Widget widget) {
        }
    }
}
