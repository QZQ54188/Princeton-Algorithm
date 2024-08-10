/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {

    private final int[][] tiles;
    private final int hammingDistance;
    private final int manhattanDistance;
    private final int[][] DIR = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
    private final int emptyPosX;
    private final int emptyPosY;
    private final int len;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("The input initialization tiles cannot be empty.");
        }
        this.tiles = new int[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i] = Arrays.copyOf(tiles[i], tiles.length);
        }
        this.len = tiles.length;
        int px = 0, py = 0, ham = 0, man = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (tiles[i][j] == 0) {
                    px = i;
                    py = j;
                    continue;
                }

                // Calculate destination distance and Hamming distance
                int val = tiles[i][j] - 1;
                int m = Math.abs(val / len - i) + Math.abs(val % len - j);
                if (m > 0) {
                    ham++;
                }
                man += m;
            }
        }

        this.manhattanDistance = man;
        this.hammingDistance = ham;
        this.emptyPosX = px;
        this.emptyPosY = py;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(len + "\n");
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                s.append(String.format("%2d", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return len;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hammingDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        else if (y == this) {
            return true;
        }
        else if (this.getClass() != y.getClass()) {
            return false;
        }
        else {
            Board other = (Board) y;
            if (this.len != other.len) {
                return false;
            }
            return Arrays.deepEquals(this.tiles, other.tiles);
        }
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>();
        for (int[] dir : DIR) {
            int x = emptyPosX + dir[0];
            int y = emptyPosY + dir[1];
            // Check if two positions can be swapped
            if (isSwapable(x, y)) {
                int[][] temp = copyTiles();
                temp[emptyPosX][emptyPosY] = temp[x][y];
                temp[x][y] = 0;
                neighbors.add(new Board(temp));
            }
        }
        return neighbors;
    }

    private boolean isSwapable(int row, int col) {
        return row >= 0 && row < len && col >= 0 && col < len;
    }


    private int[][] copyTiles() {
        int[][] newTiles = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                newTiles[i][j] = tiles[i][j];
            }
        }
        return newTiles;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] temp = copyTiles();
        int[] indices = new int[4];
        int k = 0;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (i != emptyPosX || j != emptyPosY) {
                    indices[k++] = i;
                    indices[k++] = j;
                    if (k == indices.length) {
                        break;
                    }
                }
            }
            if (k == indices.length) {
                break;
            }
        }

        int t = temp[indices[0]][indices[1]];
        temp[indices[0]][indices[1]] = temp[indices[2]][indices[3]];
        temp[indices[2]][indices[3]] = t;
        return new Board(temp);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        Board inital2 = new Board(tiles);
        StdOut.println(initial.equals(inital2));
        StdOut.println(initial.equals(initial.twin()));

        StdOut.println(initial);
        StdOut.println("hanmming dis: " + initial.hamming());
        StdOut.println("manhattan dis: " + initial.manhattan());
        StdOut.println("Twin: ");
        StdOut.println(initial.twin());
        StdOut.println("Neighbors: ");
        ArrayList<Board> ns = (ArrayList<Board>) initial.neighbors();
        for (Board b : ns) StdOut.println(b);
    }
}
