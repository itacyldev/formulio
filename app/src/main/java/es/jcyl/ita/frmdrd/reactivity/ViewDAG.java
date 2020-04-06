package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewDAG {

    private final Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = new HashMap<>();

    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> getDags() {
        return dags;
    }

    public void addDAG(DirectedAcyclicGraph<DAGNode, DefaultEdge> dag) {
        DAGNode rootNode = dag.iterator().next();
        if (!dags.containsKey(rootNode.getId())) {
            dags.put(rootNode.getId(), dag);
        }
    }

}
