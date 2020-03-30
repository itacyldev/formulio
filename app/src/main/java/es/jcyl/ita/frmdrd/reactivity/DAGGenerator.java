package es.jcyl.ita.frmdrd.reactivity;

import org.apache.commons.lang.StringUtils;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;



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

    /**
     * Creates a DAG per component of the form
     *
     * @param formConfig
     * @return
     */
    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> createDags(UIForm formConfig) {

        processComponent(formConfig);

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

        for (UIComponent component : components.values()) {
            if (!dags.containsKey(component.getId())) {
                buildComponentDag(component, dags);
            }
        }

        return dags;
    }

    /**
     * For a UIComponent, DAGNode is created, added to the structure. If the
     * component has children the operation is repeated with each one of them
     *
     * @param component
     */
    private void processComponent(UIComponent component) {
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
     * If the component has other components thar depend on it, a DAG for
     * that component is created and the connection edges between nodes are
     * added
     *
     * @param component
     */
    private void buildComponentDag(UIComponent component, Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        String[] split;

        String rerenderStr = component.getReRender();
        if (StringUtils.isNotEmpty(rerenderStr)) {
            DirectedAcyclicGraph dag = getDagComponent(component, dags);
            DAGNode componetNode = nodes.get(component.getId());

            if (rerenderStr.contains(";")) {
                rerenderStr = rerenderStr.replace(";", ",");
            }

            split = rerenderStr.split(",");

            for (String rerenderComponentId : split) {
                DAGNode rerenderNode = nodes.get(rerenderComponentId);
                dag.addVertex(rerenderNode);
                dag.addEdge(componetNode, rerenderNode);
                dags.put(rerenderComponentId, dag);

                UIComponent rerenderComponent = components.get(rerenderComponentId);
                buildComponentDag(rerenderComponent, dags);
            }
        }

        if(component instanceof UIField){
            UIField field = (UIField) component;
        }


    }

    /**
     * Returns the DAG that contains the component if exists. If not, the DAG
     * is created
     *
     * @param component
     * @return
     */
    private DirectedAcyclicGraph getDagComponent(UIComponent component,
                                                 Map<String,
                                                         DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags) {
        DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = null;
        if (dags.containsKey(component.getId())) {
            dag = dags.get(component.getId());
        } else {
            dag = new DirectedAcyclicGraph(DefaultEdge.class);
            dag.addVertex(nodes.get(component.getId()));
            dags.put(component.getId(), dag);
        }

        return dag;
    }
}




