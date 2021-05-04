package es.jcyl.ita.formic.forms.view.dag;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ViewDAG {

    private Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags;

    public Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> getDags() {
        return (dags == null) ? Collections.emptyMap() : dags;
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
                    Set<DAGNode> descendants = getDescendants(node, dag);
                    if (descendants.size() == 0) {
                        return null;
                    }

                    Set<DAGNode> vertexSet = new HashSet<>();
                    // Add the root node
                    vertexSet.add(node);
                    // Add the descendants
                    vertexSet.addAll(descendants);


                    subDAG = new DirectedAcyclicGraph(DefaultEdge.class);
                    // Vertices ara added
                    for (DAGNode vertex : vertexSet) {
                        subDAG.addVertex(vertex);
                    }

                    // Edges are added
                    addEdgesToSubDag(vertexSet, dag, subDAG);

                    return subDAG;
                }
            }
        }
        return null;
    }

    public void reset() {
        if (dags != null) {
            dags.clear();
            dags = null;
        }
    }

    /**
     * @param node
     * @param dag
     * @return
     */
    private Set<DAGNode> getDescendants(DAGNode node, DirectedAcyclicGraph<DAGNode, DefaultEdge> dag) {
        Iterator<DAGNode> iterator = new DepthFirstIterator<DAGNode, DefaultEdge>(dag, node);
        Set<DAGNode> descendants = new HashSet<>();

        // Do not add start vertex to result.
        if (iterator.hasNext()) {
            iterator.next();
        }

        while (iterator.hasNext()) {
            DAGNode descendant = iterator.next();
            descendants.add(descendant);
        }
        return descendants;
    }

    /**
     * @param vertices
     * @param dag
     * @param subDag
     */
    private void addEdgesToSubDag(Set<DAGNode> vertices, DirectedAcyclicGraph dag, DirectedAcyclicGraph subDag) {
        for (DAGNode vertex : vertices) {
            Set<DefaultEdge> edges = dag.outgoingEdgesOf(vertex);
            for (DefaultEdge edge : edges) {
                subDag.addEdge(dag.getEdgeSource(edge), dag.getEdgeTarget(edge));
            }
        }
    }

    public StringBuffer print() {
        StringBuffer sb = new StringBuffer();
        if (dags == null) {
            return sb;
        }
        
        for (String key : dags.keySet()) {
            DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = dags.get(key);
            DAGNode rootNode = dag.iterator().next();
            sb.append("\n");
            printNode(dag, rootNode, sb, 1);
        }
        return sb;
    }

    public StringBuffer print(String nodeId) {
        DirectedAcyclicGraph<DAGNode, DefaultEdge> dag = this.getDAG(nodeId);
        StringBuffer sb = new StringBuffer();
        DAGNode rootNode = dag.iterator().next();
        printNode(dag, rootNode, sb, 1);

        return sb;
    }

    private void printNode(DirectedAcyclicGraph<DAGNode, DefaultEdge> dag, DAGNode node, StringBuffer sb, int level) {
        sb.append(node.getId());
        sb.append("\n");
        Set<DefaultEdge> outgoingEdges = dag.outgoingEdgesOf(node);
        for (DefaultEdge edge : outgoingEdges) {
            DAGNode child = dag.getEdgeTarget(edge);
            for (int i = 0; i < level; i++) {
                sb.append("\t");
            }
            sb.append("|_ ");
            printNode(dag, child, sb, level + 1);
        }
    }
}
