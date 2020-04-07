package es.jcyl.ita.frmdrd.reactivity;

import android.util.Log;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.view.dag.DAGNode;
import io.reactivex.rxjava3.core.Flowable;

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
public class ReactivityFlowManager {

    private static ReactivityFlowManager _instance = null;

    private final Map<String, Flowable<String>> flows = new HashMap<>();

    private boolean generated = false;

    public static ReactivityFlowManager getInstance() {
        if (_instance == null) {
            _instance = new ReactivityFlowManager();
        }
        return _instance;
    }

    /**
     *
     * @param componentId
     */
    public void execute(String componentId) {
        if (flows.containsKey(componentId)) {
            Flowable<String> flow = getFlow(componentId);
            flow.subscribe(ReactivityFlowManager::flowableExectuted);
        }
    }


    /**
     *
     * @param componentId
     * @return
     */
    public Flowable<String> getFlow(String componentId) {
        if (!generated) {
//            DirectedAcyclicGraph dag = DAGManager.getInstance().getViewDAGs();
//            if (dag != null) {
//                generateReactivityFlow(dag);
//            }
        }

        return flows.get(componentId);
    }

    /**
     * @param dag
     */
    private void generateReactivityFlow(DirectedAcyclicGraph dag) {
        Flowable<String> flow = null;
        for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
            DAGNode node = it.next();
            if (dag.inDegreeOf(node) == 0) {
                flow = createObservableNode(node, dag);
                flows.put(node.getId(), flow);
            }
        }
    }

    /**
     * @param node
     * @param dag
     * @return
     */
    private Flowable<String> createObservableNode(DAGNode node,
                                                  DirectedAcyclicGraph<DAGNode, DefaultEdge> dag) {
        Flowable<String> flowable =
                Flowable.fromCallable(() -> {
                    UIComponent component = node.getComponent();
                    MainController.getInstance().updateView(component, true);
                    //Thread.sleep(500);
                    return "node " + node.getId() + " updated";
                });
        Set<DefaultEdge> edges = dag.outgoingEdgesOf(node);

        for (DefaultEdge edge : edges) {
            List<Flowable<String>> flowableList = new ArrayList<>();
            DAGNode child = dag.getEdgeTarget(edge);
            dag.getDescendants(child);
            Flowable<String> flowableChild = null;
            if (flows.keySet().contains(child.getId())) {
                flowableChild = flows.get(child.getId());
            } else {
                flowableChild = createObservableNode(child, dag);
                flows.put(child.getId(), flowableChild);
            }


            flowableList.add(flowableChild);
            flowable = flowable.concatWith(Flowable.merge(flowableList));
        }

        return flowable;
    }

    public static void flowableExectuted(String msg) {
        Log.i("ReactivityFlowGenerator", msg);
    }


}
