package graph;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for the Graph class.
 *  @author Bill Cai
 *
 *  My tests for graph are located in
 *  testing/PathTest.java and testing/GraphTest.java
 */
public class GraphTesting {


    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void graph3V() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        assertEquals("Graph vertices", 3, g.vertexSize());
        assertEquals("Graph edges", 2, g.edgeSize());
    }

}
