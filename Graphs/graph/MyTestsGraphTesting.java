package graph;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by billcai on 12/5/16.
 *
 * This has all my tests for the Graph package. I borrowed some functions from the grader tests like the Traverse2 function to help me test.
 */
public class MyTestsGraphTesting {



    /** Returns String.format(FORMAT, ARGS). */
    private static String fmt(String format, Object... args) {
        return String.format(format, args);
    }


    private class Traverse2 extends BreadthFirstTraversal {

        Traverse2(Graph G) {
            super(G);
        }

        private ArrayList<Integer> _trail = new ArrayList<>();

        @Override
        protected boolean visit(int v) {
            assertFalse(fmt("BFT: %d visited multiple time", v),
                    _trail.contains(v));
            _trail.add(v);
            return true;
        }

        ArrayList<Integer> trail() {
            return new ArrayList<>(_trail);
        }

    }
    private static class Traverse1 extends DepthFirstTraversal {

        Traverse1(Graph G) {
            super(G);
        }

        private ArrayList<Integer> _trail = new ArrayList<>();

        @Override
        protected boolean visit(int v) {
            _trail.add(v);
            return true;
        }

        @Override
        protected boolean postVisit(int v) {
            _trail.add(-v);
            return true;
        }

        ArrayList<Integer> trail() {
            return new ArrayList<>(_trail);
        }

    }



    @Test
    public void AddVertex() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        assertEquals(true, g.contains(3));
    }

    @Test
    public void removeVertex() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.remove(2);
        assertEquals(true, g.contains(3));
        assertEquals(false, g.contains(2));
    }

    @Test
    public void AddEdge() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        assertEquals(true, g.contains(2, 3));
        assertEquals(false, g.contains(3, 2));
    }

    @Test
    public void removeEdge() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.remove(1, 2);
        assertEquals(0, g.edgeSize());
    }

    @Test
    public void testSuccessors() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        assertEquals(2, g.outDegree(1));
    }

    @Test
    public void maxVertex() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        assertEquals(3, g.maxVertex());
    }

    @Test
    public void Undirected1() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        assertEquals(3, g.edgeSize());
        assertEquals(true, g.contains(2, 1));
        assertEquals(true, g.contains(1, 2));
        assertEquals(4, g.maxVertex());
    }

    @Test
    public void Undirected2() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(5, 5);
        g.add(1, 2);
        g.add(2, 3);
        g.add(1, 3);
        g.add(1, 4);
        g.remove(1);
        g.add();
        assertEquals(1, g.edgeSize());
        assertEquals(4, g.maxVertex());
        assertEquals(false, g.contains(1, 3));
    }

    @Test
    public void DepthFirstTraversal() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(3, 4);
        g.add(1, 2);
        g.add(2, 4);
        g.add(5, 4);
        Traverse1 traverse1 = new Traverse1(g);
        traverse1.traverse(1);
        ArrayList<Integer> trail = new ArrayList<>();
        trail.add(1);
        trail.add(2);
        trail.add(4);
        trail.add(-4);
        trail.add(-2);
        trail.add(3);
        trail.add(-3);
        trail.add(-1);
        assertEquals(trail, traverse1.trail());
    }

    @Test
    public void BreadthFirstTraversal() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(1, 3);
        g.add(2, 4);
        g.add(3, 4);
        g.add(5, 4);
        Traverse2 traverse2 = new Traverse2(g);
        traverse2.traverse(1);
        ArrayList<Integer> trail = new ArrayList<>();
        trail.add(1);
        trail.add(2);
        trail.add(3);
        trail.add(4);
        assertEquals(trail, traverse2.trail());
    }

    @Test
    public void UndirectedDepthFirstTraversal() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(3, 4);
        g.add(1, 2);
        g.add(2, 4);
        g.add(5, 4);
        Traverse1 traverse1 = new Traverse1(g);
        traverse1.traverse(1);
        ArrayList<Integer> trail = new ArrayList<>();
        trail.add(1);
        trail.add(2);
        trail.add(4);
        trail.add(5);
        trail.add(-5);
        trail.add(3);
        trail.add(-3);
        trail.add(-4);
        trail.add(-2);
        trail.add(-1);
        assertEquals(trail, traverse1.trail());
    }

    @Test
    public void UndirectedBreadthFirstTraversal() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 3);
        g.add(3, 4);
        g.add(1, 2);
        g.add(2, 4);
        g.add(5, 4);
        Traverse2 traverse2 = new Traverse2(g);
        traverse2.traverse(1);
        ArrayList<Integer> trail = new ArrayList<>();
        trail.add(1);
        trail.add(3);
        trail.add(2);
        trail.add(4);
        trail.add(5);
        assertEquals(trail, traverse2.trail());
    }

    @Test
    public void ShortestPaths() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();

    }






}
