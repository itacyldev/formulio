package es.jcyl.ita.formic.forms.view.dag;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.ViewConfigException;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

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
    private Map<String, ViewDAG> viewDags = new HashMap<>();


    // Stores a DAGNode per component of the form mapped by its ID
    private Map<String, DAGNode> nodes = new HashMap<>();

    public static DAGManager getInstance() {
        if (_instance == null) {
            _instance = new DAGManager();
        }
        return _instance;
    }

    /**
     * Creates a DAG per component of the view if it has dependencies
     *
     * @param viewRoot
     * @return
     */
    public void generateDags(UIView viewRoot) {
        if (viewRoot != null) {
            // Gets all the components in the view
            Map<String, UIComponent> components = getViewComponents(viewRoot);

            Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

            // Builds the dag for each component
            for (String componentId : components.keySet()) {
                if (!dags.containsKey(componentId)) {
                    buildComponentDag(componentId, dags, components);
                }
            }

            ViewDAG viewDAG = new ViewDAG();
            for (DirectedAcyclicGraph dag : dags.values()) {
                viewDAG.addDAG(dag);
            }

            if (viewDags == null) {
                viewDags = new HashMap<>();
            }

            viewDags.put(viewRoot.getId(), viewDAG);
            DevConsole.debug("DAG from " + viewRoot.getId() + ":");
            DevConsole.debug(viewDAG.print().toString());
        }
    }


    /**
     * Gets all the components of
     *
     * @param view
     * @return
     */
    private Map<String, UIComponent> getViewComponents(UIView view) {
        Map<String, UIComponent> components = new HashMap<>();
        storeComponents(view, components);
        return components;
    }


    /**
     * Stores the component and all its descendants on a Map
     *
     * @param component
     */
    private void storeComponents(UIComponent component, Map<String, UIComponent> components) {
        // store absolute and relative references for current component
        components.put(component.getAbsoluteId(), component);

        if (component.hasChildren()) {
            for (UIComponent child : component.getChildren()) {
                storeComponents(child, components);
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
    private void buildComponentDag(String componentId, Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags,
                                   Map<String, UIComponent> components) {
        UIComponent component = components.get(componentId);

        // The DAG is created only if the component depends on other components
        if (component.getValueBindingExpressions() == null) {
            return;
        }
        for (ValueBindingExpression ve : component.getValueBindingExpressions()) {
            if (ve == null || ve.isLiteral()) {
                // no expression or the expression doesn't includes dependencies
                continue;
            }
            DAGNode componentNode = getComponentNode(component);
            List<String> dependingVariables = ve.getDependingVariables();

            for (String depString : dependingVariables) {
                if (depString.startsWith("entity")|| depString.startsWith("param")) {
                    // entity properties mapping are skipped
                    continue;
                }
                String dependingComponentId = createAbsoluteReference(component, depString);
                if (dependingComponentId == null || dependingComponentId.equals(componentId)) {
                    // skip current component or if depending component is not found
                    continue;
                }
                UIComponent dependingComponent = components.get(dependingComponentId);
                if (dependingComponent == null) {
                    error(String.format("Error found in dependency definition: component [%s] " +
                                    "depends on component [%s], but the latter is not found, ej: form1.input1 or view.input1" +
                                    "This dependency won't work!",
                            component.getId(), dependingComponentId));
                    error("Available components in this view are: " + components.keySet());
                    continue;
                }
                DAGNode dependingNode = getComponentNode(dependingComponent);
                DirectedAcyclicGraph dag;

                if (dags.containsKey(dependingComponentId)) {
                    dag = dags.get(dependingComponentId);
                } else if (dags.containsKey(componentId)) {
                    dag = dags.get(componentId);
                } else {
                    dag = getDagComponent(dependingComponentId, dags);
                }

                if (!dag.containsVertex(dependingNode)) {
                    dag.addVertex(dependingNode);
                    dags.put(dependingComponentId, dag);
                }

                if (!dag.containsVertex(componentNode)) {
                    dag.addVertex(componentNode);
                    dags.put(componentId, dag);
                }

                if (!dag.containsEdge(dependingNode, componentNode)) {
                    dag.addEdge(dependingNode, componentNode);
                }
                buildComponentDag(dependingComponentId, dags, components);
            }
        }
    }

    /**
     * Given an variable reference given in an expression like view.f1, entity.f1, form1.view.f2, etc.
     * returns the absolute id of the referenced element: "form1.f1" if it's nested inside a form or
     * "f1" if it is not.
     *
     * @param component:    dependant component
     * @param varReference: reference to the component origin of the dependence
     * @return
     */
    private String createAbsoluteReference(UIComponent component, String varReference) {
        // is form relative reference
        if (varReference.startsWith("view")) {
            // relative reference uses current component form to find the component
            UIForm form = component.getParentForm();
            if (form == null) {
                throw new ViewConfigException(String.format("Invalid variable reference. " +
                        "Relative references can be used just inside a form. " +
                        "Wrap element [%s] inside a form.", component.getId()));
            }
            String childId = varReference.replace("view.", "");
            UIComponent child = UIComponentHelper.findChild(form, childId);
            if (child == null) {
                error(String.format("Invalid relative reference: [%s]. No children component " +
                                "found within form [%s] with id [%s] in file ${file}.",
                        varReference, form.getId(), childId));
                return null;
            }
            return child.getAbsoluteId();
        } else if (varReference.startsWith("entity")) {
            // keep reference as entity property link
            return varReference;
        } else {
            // check if the reference is correct
            UIComponent referenced = UIComponentHelper.findByAbsoluteId(component.getRoot(), varReference);
            if (referenced == null) {
                error(String.format("Invalid absolute reference, not children component " +
                        "found under with absolute reference [%s].", varReference));
                return null;
            }
            return referenced.getAbsoluteId();
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
        if (nodes == null) {
            nodes = new HashMap<>();
        }

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

    /**
     * @param viewId
     * @return
     */
    public ViewDAG getViewDAG(String viewId) {
        return viewDags.get(viewId);
    }

    /**
     * @return
     */
    public Map<String, ViewDAG> getAllDAGs() {
        return viewDags;
    }

    /**
     * @param viewId
     * @return
     */
    public StringBuffer printViewDAG(String viewId) {
        StringBuffer sb = null;
        ViewDAG viewDAG = viewDags.get(viewId);
        if (viewDAG != null) {
            sb = viewDAG.print();
        }
        return sb;
    }

    /**
     * @param viewId
     * @return
     */
    public StringBuffer printViewDAG(String viewId, String nodeId) {
        StringBuffer sb = null;
        ViewDAG viewDAG = viewDags.get(viewId);
        if (viewDAG != null) {
            sb = viewDAG.print(nodeId);
        }
        return sb;
    }

    public void flush() {
        if (nodes != null) {
            nodes.clear();
            nodes = null;
        }
        if (viewDags != null) {
            for (ViewDAG viewDag : viewDags.values()) {
                viewDag.reset();
            }
            viewDags.clear();
            viewDags = null;
        }
    }
}




