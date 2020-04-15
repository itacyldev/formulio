package es.jcyl.ita.frmdrd.view.dag;

import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    /**
     * Creates a DAG with the node passed as parameter and all its descendants
     *
     * @param nodeId
     * @return
     */
    public DirectedAcyclicGraph<DAGNode, DefaultEdge> getDAG(String nodeId) {
        DirectedAcyclicGraph<DAGNode, DefaultEdge> subDAG;
        for (DirectedAcyclicGraph<DAGNode, DefaultEdge> dag : dags.values()) {
            Iterator<DAGNode> it = dag.iterator();
            DAGNode node = null;
            while (it.hasNext()) {
                node = it.next();
                if (node.getId().equals(nodeId)) {
                    Set<DAGNode> vertexSet = new HashSet<>();
                    vertexSet.add(node);
                    Set<DAGNode> descendants = dag.getDescendants(node);
                    if (descendants != null) {
                        vertexSet.addAll(descendants);
                    }

                    // subgraph with all the descendants
                    AsSubgraph<DAGNode, DefaultEdge> subgraph = new AsSubgraph<>(dag, vertexSet);

                    // Need to create a directed graph
                    subDAG = new DirectedAcyclicGraph(DefaultEdge.class);
                    // Vertices ara added
                    for (DAGNode vertex : subgraph.vertexSet()) {
                        subDAG.addVertex(vertex);
                    }

                    // Edges are added
                    for (DefaultEdge edge : subgraph.edgeSet()) {
                        subDAG.addEdge(subDAG.getEdgeSource(edge), subDAG.getEdgeTarget(edge));
                    }

                    return subDAG;
                }
            }

        }

        return null;
    }

    public void reset() {
        dags.clear();
        dags = null;
    }

}
