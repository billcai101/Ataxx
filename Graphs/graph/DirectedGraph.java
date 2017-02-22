package graph;

/* See restrictions in Graph.java. */
import java.util.ArrayList;
import java.util.Iterator;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Bill Cai
 *  Tests for graph package can be found
 *  in testing/PathTest and testing/GraphTest
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        int degree = 0;
        for (int[] a : edges()) {
            if (a[1] == v) {
                degree += 1;
            }
        }
        return degree;
    }

    @Override
    public int predecessor(int v, int k) {
        Iteration<Integer> predecessors = predecessors(v);
        while (k > 0) {
            if (!predecessors.hasNext()) {
                return 0;
            }
        }
        return predecessors.next();
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        ArrayList<Integer> successors = new ArrayList<>();
        for (int[] a : edges()) {
            if (a[1] == v) {
                successors.add(a[0]);
            }
        }
        Iterator<Integer> temp = successors.iterator();
        return Iteration.iteration(temp);
    }
}
