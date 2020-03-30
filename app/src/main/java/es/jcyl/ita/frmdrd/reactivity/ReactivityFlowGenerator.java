package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ReactivityFlowGenerator {

    private Map<String, Flowable<String>> flows = new HashMap<>();

    public Flowable<String> generateReactivityFlow(DirectedAcyclicGraph<DAGNode, DefaultEdge> dag,
                                                   String componentId) {
        Flowable<String> flow = null;
        if (flows.containsKey(componentId)) {
            flow = flows.get(componentId);
        } else {
            flow = createFlow(dag, componentId);
        }
        return flow;
    }

    private Flowable<String> createFlow(DirectedAcyclicGraph dag, String componentId) {
        Flowable<String> flow = null;
        for (Iterator<DAGNode> it = dag.iterator(); it.hasNext(); ) {
            DAGNode node = it.next();
            if (node.getId().equals(componentId)) {
                if (dag.inDegreeOf(node) == 0) {
                    flow = createObservableNode(node, dag);

                }
            }
        }
        return flow;
    }

    private Flowable<String> createObservableNode(DAGNode node,
                                                  DirectedAcyclicGraph<DAGNode, DefaultEdge> dag) {
        Flowable<String> flowable =
                Flowable.fromCallable(() -> {
                    Thread.sleep(500);
                    return "node " + node.getId() + " completed";
                });
        Set<DefaultEdge> edges = dag.outgoingEdgesOf(node);

        for (DefaultEdge edge : edges) {
            List<Flowable<String>> flowableList = new ArrayList<>();
            DAGNode child = dag.getEdgeTarget(edge);

            dag.getDescendants(child);

            Flowable<String> flowableChild = null;
            if (flows.containsKey(child.getId())) {
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
}
