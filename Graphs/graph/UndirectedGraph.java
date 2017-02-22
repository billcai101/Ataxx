package graph;

/* See restrictions in Graph.java. */

/** Represents an undirected graph.  Out edges and in edges are not
 *  distinguished.  Likewise for successors and predecessors.
 *
 *
 *
 *
 *  @author Bill Cai
 */
public class UndirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public int inDegree(int v) {
        return outDegree(v);
    }

    @Override
    public int predecessor(int v, int k) {
        return successor(v, k);
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        return successors(v);
    }

    @Override
    public int outDegree(int v) {
        int degree = 0;
        int adjustment = 0;
        for (int[] a : edges()) {
            if (a[1] == v) {
                degree += 1;
                if (a[0] == a[1]) {
                    adjustment += 1;
                }
            }

        }
        return degree - adjustment / 2;
    }

    @Override
    protected int edgeId(int u, int v) {
        return Math.min(((u + v) * (u + v + 1) / 2) + v,
                ((u + v) * (u + v + 1) / 2) + u);
    }



}
