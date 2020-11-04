package es.jcyl.ita.formic.forms.view.render;
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Iterator;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.DynamicComponent;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.dag.DAGNode;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Intermediate class to encapsulate rendering to facilitate testing
 */
public class ViewRenderHelper {

    public View render(RenderingEnv env, UIComponent root) {
        return render(env, root, true);
    }

    private View render(RenderingEnv env, UIComponent component, boolean checkDeferred) {
        String rendererType = component.getRendererType();
        Renderer renderer = this.getRenderer(rendererType);
        // enrich the execution environment with current form's context
        setupFormContext(component, env);

        View componentView;
        if (checkDeferred && hasDeferredExpression(component, env)) {
            // insert a delegated view component as placeholder to render later
            componentView = createDeferredView(env.getViewContext(), component, env);
        } else {
            componentView = renderer.render(env, component);
        }
        if (component instanceof UIForm) {
            // configure viewContext
            ((UIForm) component).getContext().setView(componentView);
            env.setFormContext(((UIForm) component).getContext());
        } else {
            if(env.getFormContext() != null){
                env.getFormContext().getViewContext().registerComponentView(component,componentView);
           }
        }
        // if current view is not visible, don't render children
        if (!ViewHelper.isVisible(componentView)) {
            return componentView;
        }

        // render children if needed
        if (renderer instanceof GroupRenderer) {
            GroupRenderer gRenderer = (GroupRenderer) renderer;
            if (component.isRenderChildren() && component.hasChildren()) {
                // recursively render children components
                Widget groupView = (Widget) componentView;
                gRenderer.initGroup(env, groupView);
                UIComponent[] kids = component.getChildren();
                int numKids = kids.length;
                View[] views = new View[numKids];
                for (int i = 0; i < numKids; i++) {
                    views[i] = render(env, kids[i]);
                }
                gRenderer.addViews(env, groupView, views);
                gRenderer.endGroup(env, groupView);
            }
        }
        if (checkDeferred && component.getParent() == null) {
            // last step in the tree walk, process delegates when we're back on the view root
            processDeferredViews(env);
        }
        return componentView;
    }

    private void setupFormContext(UIComponent root, RenderingEnv env) {
        if (root instanceof UIForm) {
            env.setFormContext(((UIForm) root).getContext());
        }
    }

    private View createDeferredView(Context viewContext, UIComponent root, RenderingEnv env) {
        DeferredView view = new DeferredView(viewContext, root);
        env.addDeferred(root.getAbsoluteId(), view);
        return view;
    }

    protected Renderer getRenderer(String rendererType) {
        Renderer renderer = RendererFactory.getInstance().getRenderer(rendererType);
        return renderer;
    }

    public void replaceView(View view1, View view2) {
        ViewGroup parent = (ViewGroup) view1.getParent();
        int index = parent.indexOfChild(view1);
        parent.removeView(view1);
        parent.addView(view2, index);
    }


    private boolean hasDeferredExpression(UIComponent root, RenderingEnv env) {
        // TODO improve this
        return ((root.getValueExpression() != null && root.getValueExpression().toString().contains("view")) ||
                (root.getRenderExpression() != null && root.getRenderExpression().toString().contains("view")));
    }

    /**
     * Replaces the delegate views inserted in the tree with their proper elements.
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
        for (DirectedAcyclicGraph<DAGNode, DefaultEdge> dag : viewDAG.getDags().values()) {
            // follow dag evaluating expressions and rendering views
            for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
                DAGNode node = it.next();
                // find deferredView for this component
                View defView = deferredViews.get(node.getComponent().getAbsoluteId());
                if (defView != null) {
                    // remove from deferred elements
                    deferredViews.remove(defView);
                    // render the view and replace deferred element
                    View newView = this.render(env, node.getComponent(), false);
                    replaceView(defView, newView);
                }
            }
        }
    }

    /**
     * Given an element in current view, renders all the dependant elements
     */
    public void renderDeps(RenderingEnv env, UIComponent component) {
        // get element dags
        ViewDAG viewDAG = env.getViewDAG();
        if (viewDAG == null || viewDAG.getDags().size() == 0) {
            return;
        }
        DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = viewDAG.getDAG(component.getAbsoluteId());
        if (dag == null) {
            return;// no dependant components
        }
        // get current Android view
        View rootView = env.getViewRoot();
        // walk the tree in topological order to follow the dependencies from the current element
        System.out.println(">>>> DAG ENCONTRADO: " + dag.toString());
        // sets the rendering starting point, when given element is found in the DAG
        boolean found = false;

        for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
            DAGNode node = it.next();
            System.out.println("Renderizando..... " + node.getId() + " " + node.getComponent());
            if (!found) {
                if (node.getComponent().getId().equals(component.getId())) {
                    found = true; // start rendering in next element
                    System.out.println("===========EMPEZAMOS=================");
                }
            } else {
                // find view element to update
                UIComponent cNode = node.getComponent();
                View view = ViewHelper.findComponentView(rootView, cNode);
                if (component instanceof UIForm) {
                    env.setFormContext(((UIForm) component).getContext());
                } else {
                    env.setFormContext(component.getParentForm().getContext());
                }

                if (view instanceof DynamicComponent) {
                    ((DynamicComponent) view).load(env);
                } else {
                    // re-render and replace view
                    View newView = this.render(env, node.getComponent(), false);
                    replaceView(view, newView);
                }

            }
        }
    }
}
