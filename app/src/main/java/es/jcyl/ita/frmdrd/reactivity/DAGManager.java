package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

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
public class DAGManager {

    private static DAGManager _instance = null;

    // Stores all the DAGs of the view
    private final Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

    // Stores all the components of the form mapped by its ID
    private Map<String, UIComponent> components = new HashMap<>();

    // Stores a DAGNode per component of the form mapped by its ID
    private Map<String, DAGNode> nodes = new HashMap<>();
    private UIComponent viewRoot;

    public static DAGManager getInstance() {
        if (_instance == null) {
            _instance = new DAGManager();
        }
        return _instance;
    }

    /**
     * Creates a DAG per component of the view if it has dependencies
     *
     * @param component
     * @return
     */
    public void generateDags(UIComponent component) {
        if (component != null) {
            this.viewRoot = component;

            // Gets all the components in the view
            storeComponents(viewRoot);

            // Builds the dag for each component
            for (String componentId : components.keySet()) {
                if (!dags.containsKey(componentId)) {
                    buildComponentDag(componentId, dags);
                }
            }
        }
    }

    /**
     * Stores the component and all its descendants on a Map
     *
     * @param component
     */
    private void storeComponents(UIComponent component) {
        components.put(component.getAbsoluteId(), component);

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

        // The DAG is created only if the component depends on other components
        if(component.getValueBindingExpressions() == null){
            return;
        }
        for (ValueBindingExpression ve : component.getValueBindingExpressions()) {
            if (ve != null && !ve.isLiteral()) {
                DAGNode componentNode = getComponentNode(component);
                List<String> dependingVariables = ve.getDependingVariables();

                String dependingComponentId;
                for (String depString : dependingVariables) {
                    dependingComponentId = createAbsoluteReference(component, depString);

                    UIComponent dependingComponent = components.get(dependingComponentId);
                    if (dependingComponent != null) {
                        DAGNode dependingNode = getComponentNode(dependingComponent);

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
        }
    }

    /**
     * Given an variable reference given in an expressión like view.f1, entity.f1, form1.view.f2, etc.
     * returns the absolute id of the referenced element: "form1.f1" if it's nested inside a form or
     * "f1" if it is not.
     *
     * @param component
     * @param varReference
     * @return
     */
    private String createAbsoluteReference(UIComponent component, String varReference) {
        // if it starts with entity of view, is a relative reference, complete with form id
        if (!varReference.startsWith("entity") && !varReference.startsWith("view")) {
            // absolute reference to context or form nested entity or view, remove "view" or "entity"
            // context reference
            // TODO: improve this
            return varReference.replace("entity.", "").replace("view.", "");
        } else {
            UIForm form = component.getParentForm();
            if (form == null) {
                throw new ViewConfigException(String.format("Invalid variable reference. " +
                        "Relative references can be used just inside a form. " +
                        "Wrap element [%s] inside a form.", component.getId()));
            }
            varReference = varReference.replace("entity.", "").replace("view.", "");
            return form.getId() + "." + varReference;
        }
    }

    /**
     * Returns the DAGNode of the component if exists. If not, the node
     * is created
     *
     * @param component
     * @return
     */
    private DAGNode getComponentNode(UIComponent component) {
        String nodeId = component.getAbsoluteId();
        DAGNode node = null;
        if (nodes.containsKey(nodeId)) {
            node = nodes.get(nodeId);
        } else {
            node = new DAGNode(nodeId, component, null);
            node.setId(nodeId);
            nodes.put(nodeId, node);
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

    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> getDags() {
        return dags;
    }
}




