package es.jcyl.ita.frmdrd.view;
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

import es.jcyl.ita.frmdrd.reactivity.DAGNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.render.DeferredView;
import es.jcyl.ita.frmdrd.view.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;
import es.jcyl.ita.frmdrd.view.render.Renderer;
import es.jcyl.ita.frmdrd.view.render.RendererFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Intermediate class to encapsulate rendering to facilitate testing
 */
public class ViewRenderHelper {

    public View render(Context viewContext, ExecEnvironment env, UIComponent root) {
        return render(viewContext, env, root, true);
    }

    private View render(Context viewContext, ExecEnvironment env, UIComponent root, boolean checkDeferred) {
        String rendererType = root.getRendererType();
        Renderer renderer = this.getRenderer(rendererType);
        // enrich the execution environment with current form's context
        setupFormContext(root, env);

        View rootView;
        if (checkDeferred && hasDeferredExpression(root, env)) {
            // insert a delegated view component to render later
            rootView = createDeferredView(viewContext, root, env);
        } else {
            rootView = renderer.render(viewContext, env, root);
            if (root instanceof UIForm) {
                // configure viewContext
                ((UIForm) root).getContext().setView(rootView);
            }
        }
        // if current view is not visible, don't render children
        if (!ViewHelper.isVisible(rootView)) {
            return rootView;
        }

        // render children if needed
        if (renderer instanceof GroupRenderer) {
            GroupRenderer gRenderer = (GroupRenderer) renderer;
            if (root.isRenderChildren()) {
                // recursively render children components
                gRenderer.initGroup(viewContext, env, root, rootView);
                int numKids = root.getChildren().size();
                View[] views = new View[numKids];
                for (int i = 0; i < numKids; i++) {
                    views[i] = render(viewContext, env, root.getChildren().get(i));
                }
                gRenderer.addViews(viewContext, env, root, rootView, views);
                gRenderer.endGroup(viewContext, env, root, rootView);
            }
        }
        if (checkDeferred && root.getParent() == null) {
            System.out.println("Star processing deferred elements");
            // last step in the tree walk, process delegates when we're back in the view root
            processDeferredViews(viewContext, env);
        }
        return rootView;
    }

    private void setupFormContext(UIComponent root, ExecEnvironment env) {
        if (root instanceof UIForm) {
            env.setFormContext(((UIForm) root).getContext());
        } else {
            if (root.getParentForm() != null) {
                env.setFormContext(((UIForm) root.getParentForm()).getContext());
            }
        }
    }

    private View createDeferredView(Context viewContext, UIComponent root, ExecEnvironment env) {
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


    private boolean hasDeferredExpression(UIComponent root, ExecEnvironment env) {
        // TODO improve this
        return ((root.getValueExpression() != null && root.getValueExpression().toString().contains("view")) ||
                (root.getRenderExpression() != null && root.getRenderExpression().toString().contains("view")));
    }

    /**
     * Replaces the delegate views inserted in the tree with their proper elements.
     * It uses a DAG tree to evaluate the expressions following their dependencies so the final
     * result is correct.
     *
     * @param viewContext
     * @param env
     */
    private void processDeferredViews(Context viewContext, ExecEnvironment env) {
        // use dag to walk the tree just one time per node
//        DummyTreeNode root = env.getViewDeferredRoot();

        Map<String, DeferredView> deferredViews = env.getDeferredViews();

        if (env.getDags() == null || deferredViews == null) {
            return;
        }
        for (DirectedAcyclicGraph<DAGNode, DefaultEdge> dag : env.getDags()) {
            // follow dag evaluating expressions and rendering views
            for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
                DAGNode node = it.next();
                // find deferredView for this component
                View defView = deferredViews.get(node.getComponent().getAbsoluteId());
                if (defView != null) {
                    // remove from deferred elements
                    deferredViews.remove(defView);
                    // render the view and replace deferred element
                    View newView = this.render(viewContext, env, node.getComponent(), false);
                    replaceView(defView, newView);
                }
            }
        }
    }

}
