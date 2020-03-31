package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class DAGGenerator {

    // Stores all the components of the form mapped by its ID
    private Map<String, UIComponent> components = new HashMap<>();

    // Stores a DAGNode per component of the form mapped by its ID
    private Map<String, DAGNode> nodes = new HashMap<>();
    private UIView viewRoot;

    /**
     * Creates a DAG per component of the view if it has dependencies
     *
     * @param viewRoot
     * @return
     */
    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> generateDags(UIView viewRoot) {
        this.viewRoot = viewRoot;
        // Stores all the DAGs of the view
        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

        // Gets all the components in the view
        storeComponents(viewRoot);

        // Builds the dag for each component
        for (String componentId : components.keySet()) {
            if (!dags.containsKey(componentId)) {
                buildComponentDag(componentId, dags);
            }
        }

        return dags;
    }

    /**
     * Stores the component and all its descendants on a Map
     *
     * @param component
     */
    private void storeComponents(UIComponent component) {
        UIComponent parent = component.getParent();
        String componentId = null;
        if (parent != null) {
            componentId = parent.getId() + "." + component.getId();
        } else {
            componentId = component.getId();
        }

        components.put(componentId, component);

        List<UIComponent> children = component.getChildren();
        if (children != null) {
            for (UIComponent child : component.getChildren()) {
                storeComponents(child);
            }
        }
    }

    /**
     * If the component depends on other components, a DAG for
     * that component is created and the connection edges between nodes are
     * added
     *
     * @param componentId
     */
    private void buildComponentDag(String componentId,
                                   Map<String,
                                           DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        UIComponent component = components.get(componentId);
        ValueBindingExpression valueExpression = component.getValueExpression();

        // The DAG is created only if the component depends on other components
        List<String> dependingVariables = null;
        if (valueExpression != null) {
            dependingVariables = component.getValueExpression().getDependingVariables();
        }

        if (dependingVariables != null && dependingVariables.size() > 0) {
            DAGNode componentNode = getComponentNode(componentId);

            for (String dependingComponentId : dependingVariables) {
                DAGNode dependingNode = getComponentNode(dependingComponentId);

                DirectedAcyclicGraph dag = null;
                if (dags.containsKey(componentId)) {
                    dag = dags.get(componentId);
                    dag.addVertex(dependingNode);
                    dag.addEdge(dependingNode, componentNode);
                    dags.put(dependingComponentId, dag);
                } else {
                    dag = getDagComponent(dependingComponentId, dags);
                    dag.addVertex(componentNode);
                    dag.addEdge(dependingNode, componentNode);
                    dags.put(componentId, dag);
                }

                buildComponentDag(dependingComponentId, dags);
            }

        }

    }

    /**
     * Returns the DAGNode of the component if exists. If not, the node
     * is created
     *
     * @param componentId
     * @return
     */
    private DAGNode getComponentNode(String componentId) {
        DAGNode node = null;
        if (nodes.containsKey(componentId)) {
            node = nodes.get(componentId);
        } else {
            node = new DAGNode();
            node.setId(componentId);
            nodes.put(componentId, node);
        }

        return node;
    }

    /**
     * Returns the DAG that contains the component if exists. If not, the DAG
     * is created
     *
     * @param dagId
     * @return
     */
    private DirectedAcyclicGraph getDagComponent(String dagId,
                                                 Map<String,
                                                         DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = null;
        if (dags.containsKey(dagId)) {
            dag = dags.get(dagId);
        } else {
            dag = new DirectedAcyclicGraph(DefaultEdge.class);
            dag.addVertex(nodes.get(dagId));
            dags.put(dagId, dag);
        }

        return dag;
    }
}




