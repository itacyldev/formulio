package es.jcyl.ita.frmdrd.view.dag;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;

public class ViewDAG {

    private Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags;

    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> getDags() {
        return dags;
    }

    public void addDAG(DirectedAcyclicGraph<DAGNode, DefaultEdge> dag) {
        if (dags == null) {
            dags = new HashMap<>();
        }

        DAGNode rootNode = dag.iterator().next();
        if (!dags.containsKey(rootNode.getId())) {
            dags.put(rootNode.getId(), dag);
        }
    }

    public void reset() {
        dags.clear();
        dags = null;
    }

}
