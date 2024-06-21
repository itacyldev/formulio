package es.jcyl.ita.formic.forms.view.dag;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

public class ViewDAGTest {

    @Test
    public void getDag() {
        ViewDAG viewDAG = new ViewDAG();

        DirectedAcyclicGraph dag = new DirectedAcyclicGraph(DefaultEdge.class);

        DAGNode node1 = new DAGNode();
        node1.setId("node1");
        DAGNode node2 = new DAGNode();
        node2.setId("node2");
        DAGNode node3 = new DAGNode();
        node3.setId("node3");
        DAGNode node4 = new DAGNode();
        node4.setId("node4");
        DAGNode node5 = new DAGNode();
        node5.setId("node5");
        DAGNode node6 = new DAGNode();
        node6.setId("node6");
        DAGNode node7 = new DAGNode();
        node7.setId("node7");

        dag.addVertex(node1);
        dag.addVertex(node2);
        dag.addVertex(node3);
        dag.addVertex(node4);
        dag.addVertex(node5);
        dag.addVertex(node6);
        dag.addVertex(node7);

        dag.addEdge(node1, node2);
        dag.addEdge(node2, node3);
        dag.addEdge(node2, node4);
        dag.addEdge(node4, node7);
        dag.addEdge(node5, node4);
        dag.addEdge(node5, node6);

        viewDAG.addDAG(dag);

        DirectedAcyclicGraph dag1 = viewDAG.getDAG("node1");
        Assert.assertEquals(5, dag1.vertexSet().size());
        Assert.assertTrue(dag1.containsVertex(node1));
        Assert.assertTrue(dag1.containsVertex(node2));
        Assert.assertTrue(dag1.containsVertex(node3));
        Assert.assertTrue(dag1.containsVertex(node4));
        Assert.assertTrue(dag1.containsVertex(node7));


        DirectedAcyclicGraph dag2 = viewDAG.getDAG("node2");
        Assert.assertEquals(4, dag2.vertexSet().size());
        Assert.assertTrue(dag2.containsVertex(node2));
        Assert.assertTrue(dag2.containsVertex(node3));
        Assert.assertTrue(dag2.containsVertex(node4));
        Assert.assertTrue(dag2.containsVertex(node7));

        DirectedAcyclicGraph dag3 = viewDAG.getDAG("node3");
        Assert.assertNull(dag3);

        DirectedAcyclicGraph dag4 = viewDAG.getDAG("node4");
        Assert.assertEquals(2, dag4.vertexSet().size());
        Assert.assertTrue(dag4.containsVertex(node4));
        Assert.assertTrue(dag4.containsVertex(node7));

        DirectedAcyclicGraph dag5 = viewDAG.getDAG("node5");
        Assert.assertEquals(4, dag5.vertexSet().size());
        Assert.assertTrue(dag5.containsVertex(node5));
        Assert.assertTrue(dag5.containsVertex(node6));
        Assert.assertTrue(dag5.containsVertex(node4));
        Assert.assertTrue(dag5.containsVertex(node7));

        DirectedAcyclicGraph dag6 = viewDAG.getDAG("node6");
        Assert.assertNull(dag6);

        DirectedAcyclicGraph dag7 = viewDAG.getDAG("node7");
        Assert.assertNull(dag7);
    }
}
