import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("The input digraph cannot be empty.");
        }
        digraph = new Digraph(G);
    }

    private class Helper {
        private int length;
        private int ancestor;

        Helper() {
            length = -1;
            ancestor = -1;
        }

        private void findSAP(int v, int w) {
            if (v < 0 || v >= digraph.V() || w < 0 || w >= digraph.V()) {
                throw new IllegalArgumentException("Querying two nodes of SAP is illegal.");
            }
            else if (v == w) {
                length = 0;
                ancestor = w;
                return;
            }
            int[] dis1 = new int[digraph.V()];
            int[] dis2 = new int[digraph.V()];
            Arrays.fill(dis1, -1);
            Arrays.fill(dis2, -1);
            // run BFS from v
            Queue<Integer> queue = new Queue<>();
            queue.enqueue(v);
            dis1[v] = 0;
            int min = Integer.MAX_VALUE;
            while (!queue.isEmpty()) {
                int cur = queue.dequeue();
                for (int neighbor : digraph.adj(cur)) {
                    if (dis1[neighbor] < 0) {
                        dis1[neighbor] = dis1[cur] + 1;
                        queue.enqueue(neighbor);
                    }
                }
            }
            // run BFS from w
            dis2[w] = 0;
            queue.enqueue(w);
            while (!queue.isEmpty()) {
                int cur = queue.dequeue();
                if (dis1[cur] >= 0 && dis1[cur] + dis2[cur] < min) {
                    min = dis1[cur] + dis2[cur];
                    ancestor = cur;
                }
                for (int neighbor : digraph.adj(cur)) {
                    if (dis2[neighbor] < 0) {
                        dis2[neighbor] = dis2[cur] + 1;
                        queue.enqueue(neighbor);
                    }
                }
            }

            length = ancestor == -1 ? -1 : min;
        }

        private void findSAP(Iterable<Integer> v, Iterable<Integer> w) {
            int[] dis1 = new int[digraph.V()];
            int[] dis2 = new int[digraph.V()];
            int min = Integer.MAX_VALUE;
            Arrays.fill(dis1, -1);
            Arrays.fill(dis2, -1);
            Queue<Integer> queue = new Queue<>();
            for (int i : v) {
                dis1[i] = 0;
                queue.enqueue(i);
            }
            while (!queue.isEmpty()) {
                int cur = queue.dequeue();
                for (int neighbor : digraph.adj(cur)) {
                    if (dis1[neighbor] < 0) {
                        dis1[neighbor] = dis1[cur] + 1;
                        queue.enqueue(neighbor);
                    }
                }
            }

            for (int i : w) {
                dis2[i] = 0;
                queue.enqueue(i);
            }
            while (!queue.isEmpty()) {
                int cur = queue.dequeue();
                if (dis1[cur] >= 0 && dis1[cur] + dis2[cur] < min) {
                    min = dis1[cur] + dis2[cur];
                    ancestor = cur;
                }
                for (int neighbor : digraph.adj(cur)) {
                    if (dis2[neighbor] < 0) {
                        dis2[neighbor] = dis2[cur] + 1;
                        queue.enqueue(neighbor);
                    }
                }
            }

            length = ancestor == -1 ? -1 : min;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Helper helper = new Helper();
        helper.findSAP(v, w);
        return helper.length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Helper helper = new Helper();
        helper.findSAP(v, w);
        return helper.ancestor;
    }


    private void validate(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        for (Integer i : v)
            if (i == null || i < 0 || i >= digraph.V()) throw new IllegalArgumentException();
        for (Integer i : w)
            if (i == null || i < 0 || i >= digraph.V()) throw new IllegalArgumentException();
    }


    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v, w);
        Helper helper = new Helper();
        helper.findSAP(v, w);
        return helper.length;
    }


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v, w);
        Helper helper = new Helper();
        helper.findSAP(v, w);
        return helper.ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

}
