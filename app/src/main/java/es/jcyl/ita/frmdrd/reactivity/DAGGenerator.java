package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Creates a DAG per component of the view
     *
     * @param viewRoot
     * @return
     */
    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> generateDags(UIView viewRoot) {
        this.viewRoot = viewRoot;
        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

        List<UIForm> forms = viewRoot.getForms();
        for (UIForm form : forms) {
            addFormDags(form, dags);
        }

        return dags;
    }

    /**
     * @param form
     * @return
     */
    private void addFormDags(UIForm form, Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        processComponent(form);
        for (UIComponent component : components.values()) {
            String dagId = form.getId() + "." + component.getId();
            if (!dags.containsKey(dagId)) {
                buildComponentDag(form.getId(), component, dags);
            }
        }
    }

    /**
     * For a UIComponent, DAGNode is created, added to the structure. If the
     * component has children the operation is repeated with each one of them
     *
     * @param component
     */
    private void processComponent(UIComponent component) {
        components.clear();
        nodes.clear();

        components.put(component.getId(), component);

        DAGNode node = new DAGNode();
        node.setId(component.getId());
        nodes.put(node.getId(), node);

        List<UIComponent> children = component.getChildren();

        if (children != null) {
            for (UIComponent child : component.getChildren()) {
                processComponent(child);
            }
        }
    }


    /**
     * If the component depends on other components, a DAG for
     * that component is created and the connection edges between nodes are
     * added
     *
     * @param component
     */
    private void buildComponentDag(String formId, UIComponent component,
                                   Map<String,
                                           DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        /*String[] split;
        String rerenderStr = component.getReRender();
        if (StringUtils.isNotEmpty(rerenderStr)) {
            String dagId = formId + "." + component.getId();
            DirectedAcyclicGraph dag = getDagComponent(dagId, dags);
            DAGNode componetNode = nodes.get(component.getId());

            if (rerenderStr.contains(";")) {
                rerenderStr = rerenderStr.replace(";", ",");
            }

            split = rerenderStr.split(",");

            for (String rerenderComponentId : split) {
                DAGNode rerenderNode = nodes.get(rerenderComponentId);
                dag.addVertex(rerenderNode);
                dag.addEdge(componetNode, rerenderNode);
                dags.put(formId + "." + rerenderComponentId, dag);

                UIComponent rerenderComponent = components.get(rerenderComponentId);
                buildComponentDag(formId, rerenderComponent, dags);
            }
        }*/

        List<String> dependingVariables =
                component.getValueExpression().getDependingVariables();

        if (dependingVariables != null && dependingVariables.size() > 0) {
            String dagId = formId + "." + component.getId();
            DirectedAcyclicGraph dag = getDagComponent(dagId, dags);
            DAGNode componetNode = nodes.get(component.getId());

            for (String componentId : dependingVariables) {
                UIComponent dependingComponent =
                        viewRoot.findFormElement(componentId);
                DAGNode dependingNode = nodes.get(dependingComponent);
                dag.addVertex(dependingNode);
                dag.addEdge(dependingNode, componetNode);
                dags.put(formId + "." + dependingComponent.getId(), dag);
            }
        }
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




