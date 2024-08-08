import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF ufForFull;

    private boolean[][] Open;

    private int gridWidth;
    private boolean isPercolation;
    private int openSites;
    private int[] dx = { -1, 0, 1, 0 };
    private int[] dy = { 0, 1, 0, -1 };

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n-by-n grid's n must be positive.");
        }
        uf = new WeightedQuickUnionUF(n * n + 2);
        ufForFull = new WeightedQuickUnionUF(n * n + 1);
        gridWidth = n;
        openSites = 0;
        isPercolation = false;
        Open = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Open[i][j] = false;
            }
        }
    }

    private boolean validateArgument(int row, int col) {
        if (row <= 0 || row > gridWidth || col <= 0 || col > gridWidth) {
            return false;
        }
        return true;
    }

    // Calculate the subscript corresponding to the coordinate
    private int calcIndex(int row, int col) {
        return (row - 1) * gridWidth + col;
    }


    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!validateArgument(row, col)) {
            throw new IllegalArgumentException(
                    "The number of rows and columns entered must be within the range.");
        }
        int index = calcIndex(row, col);
        if (!Open[row - 1][col - 1]) {
            Open[row - 1][col - 1] = true;
            if (row == 1) {
                uf.union(0, index);
                ufForFull.union(0, index);
            }
            else if (row == gridWidth) {
                uf.union(gridWidth * gridWidth + 1, index);
            }
            openSites++;
            connectNeighbors(row, col);
        }
    }

    private void connectNeighbors(int row, int col) {
        int newRow, newCol;
        int curIndex = calcIndex(row, col);
        for (int i = 0; i < 4; i++) {
            newRow = row + dx[i];
            newCol = col + dy[i];
            if (validateArgument(newRow, newCol) && isOpen(newRow, newCol)) {
                int index = calcIndex(newRow, newCol);
                uf.union(index, curIndex);
                ufForFull.union(index, curIndex);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!validateArgument(row, col)) {
            throw new IllegalArgumentException(
                    "The number of rows and columns entered must be within the range.");
        }
        return Open[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!validateArgument(row, col)) {
            throw new IllegalArgumentException(
                    "The number of rows and columns entered must be within the range.");
        }
        return ufForFull.find(calcIndex(row, col)) == ufForFull.find(0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.find(0) == uf.find(gridWidth * gridWidth + 1);
    }

}
