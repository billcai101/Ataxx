package graph;

/* See restrictions in Graph.java. */

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author Bill Cai
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    @Override
    protected abstract double getWeight(int u, int v);

    @Override
    public double getWeight(int v) {
        int index = -1;
        for (int i = 0; i < vertices.length; i += 1) {
            if ((int) vertices[i][0] == v) {
                index = i;
            }
        }
        if (index == -1) {
            return infinity;
        }
        return (double) vertices[index][1];
    }

    @Override
    protected void setWeight(int v, double w) {
        int index = -1;
        for (int i = 0; i < vertices.length; i += 1) {
            if ((int) vertices[i][0] == v) {
                index = i;
            }
        }
        if (index == -1) {
            return;
        }
        vertices[index][1] = w;

    }

    @Override
    public int getPredecessor(int v) {
        int index = -1;
        for (int i = 0; i < vertices.length; i += 1) {
            if ((int) vertices[i][0] == v) {
                index = i;
            }
        }
        return (int) vertices[index][2];
    }

    @Override
    protected void setPredecessor(int v, int u) {
        int index = -1;
        for (int i = 0; i < vertices.length; i += 1) {
            if ((int) vertices[i][0] == v) {
                index = i;
            }
        }
        vertices[index][2] = u;
    }





}
