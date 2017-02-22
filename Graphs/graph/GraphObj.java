package graph;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Iterator;
/* See restrictions in Graph.java. */

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  For EdgeId I used a cantor pairing function
 *  as suggested by someone on StackExchange.
 *
 *  @author Bill Cai
 */
abstract class GraphObj extends Graph {

    /** A new, empty Graph. */
    GraphObj() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        addvertex = new PriorityQueue<>();
    }

    @Override
    public int vertexSize() {
        int count = 0;
        for (int a : vertices) {
            if (a != 0) {
                count += 1;
            }
        }
        return count;
    }

    @Override
    public int maxVertex() {
        int max = 0;
        for (int a : vertices) {
            if (a > max) {
                max = a;
            }
        }
        return max;
    }

    @Override
    public int edgeSize() {
        if (isDirected()) {
            return edges.size();
        }
        return edges.size() / 2;
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        int degree = 0;
        for (int[] a : edges) {
            if (a[0] == v) {
                degree += 1;
            }
        }
        return degree;
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        for (int i : vertices) {
            if (i == u) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(int u, int v) {
        for (int[] a : edges) {
            if (a[0] == u && a[1] == v) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int add() {
        if (!addvertex.isEmpty()) {
            int result = addvertex.remove();
            vertices.add(result);
            return result;
        }
        vertices.add(vertices.size() + 1);
        return vertices.size();
    }

    @Override
    public int add(int u, int v) {
        if (!contains(u) || !contains(v)) {
            return u;
        }
        if (contains(u, v)) {
            return u;
        }
        int[] temp = new int[2];
        int[] temp2 = new int[2];
        temp[0] = u;
        temp[1] = v;
        edges.add(temp);
        if (!isDirected()) {
            temp2[0] = v;
            temp2[1] = u;
            edges.add(temp2);
        }
        return edgeId(u, v);


    }

    @Override
    public void remove(int v) {
        int index = -1;
        for (int i = 0; i < vertices.size(); i += 1) {
            if (vertices.get(i) == v) {
                index = i;
            }
        }
        if (index != -1) {
            vertices.remove(index);
            addvertex.add(v);
        }
        ArrayList<int[]> temp = new ArrayList<>();
        for (int j = 0; j < edges.size(); j += 1) {
            if ((edges.get(j)[0] != v) && (edges.get(j)[1] != v)) {
                temp.add(edges.get(j));
            }
        }
        edges = temp;
    }

    @Override
    public void remove(int u, int v) {
        for (int[] a : edges) {
            if (a[0] == u && a[1] == v) {
                edges.remove(a);
                return;
            }
        }
    }

    @Override
    public Iteration<Integer> vertices() {
        Iterator<Integer> vertex = vertices.iterator();
        return Iteration.iteration(vertex);
    }

    @Override
    public int successor(int v, int k) {
        if (k < 0) {
            return 0;
        }
        Iteration<Integer> successors = successors(v);
        while (k > 0) {
            if (!successors.hasNext()) {
                return 0;
            }
            successors.next();
            k -= 1;
        }
        return successors.next();
    }

    @Override
    public abstract int predecessor(int v, int k);

    @Override
    public Iteration<Integer> successors(int v) {
        ArrayList<Integer> successors = new ArrayList<>();
        for (int[] a : edges) {
            if (a[0] == v && !successors.contains(a[1])) {
                successors.add(a[1]);
            }
        }
        Iterator<Integer> temp = successors.iterator();
        return Iteration.iteration(temp);
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        Iterator<int[]> temp = edges.iterator();
        return Iteration.iteration(temp);
    }

    @Override
    protected void checkMyVertex(int v) {
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("vertex not from Graph");
        }

    }

    @Override
    protected int edgeId(int u, int v) {
        return ((u + v) * (u + v + 1) / 2) + v;
    }


    /** Edges. */
    private ArrayList<int[]> edges;
    /** Vertices. */
    private ArrayList<Integer> vertices;
    /** Priority Queue for adding. */
    private PriorityQueue<Integer> addvertex;





}
