/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private final boolean isSolvable;

    private int moves;

    private final Node goal;

    private class Node implements Comparable<Node> {
        final Board board;
        final int moves;
        final int costs;
        final Node prev;

        Node(Board board, Node prev, int moves) {
            this.board = board;
            this.prev = prev;
            this.moves = moves;
            this.costs = board.manhattan() + moves;
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.costs, o.costs);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        MinPQ<Node> minPQ = new MinPQ<>();
        MinPQ<Node> minPQforTwin = new MinPQ<>();
        minPQ.insert(new Node(initial, null, 0));
        minPQforTwin.insert(new Node(initial.twin(), null, 0));
        MinPQ<Node> executor = minPQ;
        Node node = null;
        while (!executor.isEmpty()) {
            node = executor.delMin();
            if (node.board.isGoal()) {
                break;
            }
            Iterable<Board> neighbors = node.board.neighbors();
            for (Board board : neighbors) {
                if (node.prev == null || !board.equals(node.prev.board)) {
                    executor.insert(new Node(board, node, node.moves + 1));
                }
            }
            executor = executor == minPQ ? minPQforTwin : minPQ;
        }
        isSolvable = executor == minPQ;
        assert node != null;
        moves = node.moves;
        goal = node;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable) {
            return -1;
        }
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable) {
            return null;
        }
        Stack<Board> stack = new Stack<>();
        Node node = goal;
        while (node != null) {
            stack.push(node.board);
            node = node.prev;
        }
        return stack;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
