package graph;

/* See restrictions in Graph.java. */

import java.util.TreeSet;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** The shortest paths through an edge-weighted graph.
 *  By overrriding methods getWeight, setWeight, getPredecessor, and
 *  setPredecessor, the client can determine how to represent the weighting
 *  and the search results.  By overriding estimatedDistance, clients
 *  can search for paths to specific destinations using A* search.
 *  @author Bill Cai
 *  My tests for graph are in testing/PathTest.java and testing/GraphTest.java
 */
public abstract class ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;

    }

    /** Initialize the shortest paths.  Must be called before using
     *  getWeight, getPredecessor, and pathTo. */
    public void setPaths() {
        vertices = new Object[_G.vertexSize() + 1][3];
        for (int i = 0; i < _G.vertexSize() + 1; i += 1) {
            vertices[i][0] = i;
            vertices[i][1] = infinity;
            vertices[i][2] = 0;
        }
        vertices[_source][1] = 0.0;
        vertices[_source][2] = 0;
        for (int j = _source; j < _G.vertexSize() + 1; j += 1) {
            shortestpaths.add(j);
        }
        pathTo();
    }

    /** Returns the starting vertex. */
    public int getSource() {
        return _source;
    }

    /** Returns the target vertex, or 0 if there is none. */
    public int getDest() {
        return _dest;
    }

    /** Returns the current weight of vertex V in the graph.  If V is
     *  not in the graph, returns positive infinity. */
    public abstract double getWeight(int v);

    /** Set getWeight(V) to W. Assumes V is in the graph. */
    protected abstract void setWeight(int v, double w);

    /** Returns the current predecessor vertex of vertex V in the graph, or 0 if
     *  V is not in the graph or has no predecessor. */
    public abstract int getPredecessor(int v);

    /** Set getPredecessor(V) to U. */
    protected abstract void setPredecessor(int v, int u);

    /** Returns an estimated heuristic weight of the shortest path from vertex
     *  V to the destination vertex (if any).  This is assumed to be less
     *  than the actual weight, and is 0 by default. */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    protected abstract double getWeight(int u, int v);

    /** Returns a list of vertices starting at _source and ending
     *  at V that represents a shortest path to V.  Invalid if there is a
     *  destination vertex other than V. */
    public List<Integer> pathTo(int v) {
        outerloop:
        while (!shortestpaths.isEmpty()) {
            int vertex = shortestpaths.pollFirst();
            for (int k : _G.successors(vertex)) {
                if ((getWeight(vertex) + getWeight(vertex, k)) < getWeight(k)) {
                    setWeight(k, getWeight(vertex) + getWeight(vertex, k));
                    shortestpaths.remove(k);
                    shortestpaths.add(k);
                    setPredecessor(k, vertex);
                }
                if (k == _dest) {
                    break outerloop;
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        int a = v;
        while (a != 0 && a != _source) {
            result.add(a);
            a = getPredecessor(a);
        }
        result.add(a);
        Collections.reverse(result);
        return result;
    }

    /** Returns a list of vertices starting at the source and ending at the
     *  destination vertex. Invalid if the destination is not specified. */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }


    /** The graph being searched. */
    protected final Graph _G;
    /** The starting vertex. */
    private final int _source;
    /** The target vertex. */
    private final int _dest;
    /** Infinite. */
    protected double infinity = Double.MAX_VALUE;
    /** Comparator for my Treeset. */
    private Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (getWeight(o2) + estimatedDistance(o2)
                    < getWeight(o1) + estimatedDistance(o1)) {
                return 1;
            }
            if (getWeight(o2) + estimatedDistance(o2)
                    > getWeight(o1) + estimatedDistance(o1)) {
                return -1;
            }
            return 0;
        }
    };
    /** Treeset for priorit queue. */
    private TreeSet<Integer> shortestpaths = new TreeSet<>(comparator);
    /** Array of array to represent vertices. */
    protected Object[][] vertices;



}
