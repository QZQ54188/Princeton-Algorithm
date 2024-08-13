import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private static final int[] DIR = new int[] { -1, 0, 1 };
    private int height;
    private int width;
    private Picture picture;
    private double[][] energy;
    private static final int BORADER_ENGERGY = 1000;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("The input picture cannot be empty.");
        }
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
        this.energy = new double[width][height];
    }


    // current picture
    public Picture picture() {
        Picture newPicture = new Picture(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newPicture.setRGB(i, j, picture.getRGB(i, j));
            }
        }
        return newPicture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }


    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("The coordinates queried must be within the range.");
        }
        if (energy[x][y] != 0.0) {
            return energy[x][y];
        }
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            energy[x][y] = BORADER_ENGERGY;
        }
        else {
            energy[x][y] = Math.sqrt(gradient(picture.get(x - 1, y), picture.get(x + 1, y))
                                             + gradient(picture.get(x, y - 1),
                                                        picture.get(x, y + 1)));
        }
        return energy[x][y];
    }


    private double gradient(Color a, Color b) {
        return Math.pow(a.getRed() - b.getRed(), 2)
                + Math.pow(a.getGreen() - b.getGreen(), 2)
                + Math.pow(a.getBlue() - b.getBlue(), 2);
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam = new int[width];
        double minDis = Double.POSITIVE_INFINITY;
        double[][] disTo = new double[width][height];
        for (double[] col : disTo) {
            Arrays.fill(col, Double.POSITIVE_INFINITY);
        }
        Arrays.fill(disTo[0], 0);

        int[][] edges = new int[width][height];
        int endRow = 0;

        for (int col = 1; col < width; col++) {
            for (int row = 0; row < height; row++) {
                for (int dir : DIR) {
                    int lastRow = row + dir;
                    if (lastRow >= 0 && lastRow < height) {
                        if (disTo[col][row] > disTo[col - 1][lastRow] + energy(col, row)) {
                            disTo[col][row] = disTo[col - 1][lastRow] + energy(col, row);
                            edges[col][row] = lastRow;
                        }
                    }
                }
                if (col == width - 1 && disTo[col][row] < minDis) {
                    minDis = disTo[col][row];
                    endRow = row;
                }
            }
        }

        for (int col = width - 1, row = endRow; col >= 0; row = edges[col][row], col--) {
            seam[col] = row;
        }
        return seam;
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height];
        double minDis = Double.POSITIVE_INFINITY;
        double[][] disTo = new double[height][width];
        for (double[] row : disTo) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        Arrays.fill(disTo[0], 0);

        int[][] edges = new int[height][width];
        int endCol = 0;

        for (int row = 1; row < height; row++) {
            for (int col = 0; col < width; col++) {
                for (int dir : DIR) {
                    int lastCol = col + dir;
                    if (lastCol >= 0 && lastCol < width) {
                        if (disTo[row][col] > disTo[row - 1][lastCol] + energy(col, row)) {
                            disTo[row][col] = disTo[row - 1][lastCol] + energy(col, row);
                            edges[row][col] = lastCol;
                        }
                    }
                }
                if (row == height - 1 && disTo[row][col] < minDis) {
                    minDis = disTo[row][col];
                    endCol = col;
                }
            }
        }

        for (int row = height - 1, col = endCol; row >= 0; col = edges[row][col], row--) {
            seam[row] = col;
        }
        return seam;
    }


    private void validateSeam(int[] seam, int length, int valueLimit) {
        if (seam == null || seam.length != length || valueLimit <= 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= valueLimit) {
                throw new IllegalArgumentException();
            }
        }
        for (int i = 0, j = 1; j < seam.length; i++, j++) {
            if (Math.abs(seam[i] - seam[j]) > 1)
                throw new IllegalArgumentException();
        }
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height - 1; row++) {
                if (row >= seam[col]) {
                    picture.setRGB(col, row, picture.getRGB(col, row + 1));
                    energy[col][row] = energy[col][row + 1];
                }
            }
        }

        for (int col = 0; col < width; col++) {
            for (int dir : DIR) {
                int row = seam[col] + dir;
                if (row >= 0 && row < height) {
                    energy[col][row] = 0;
                }
            }
        }
        height--;
    }


    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                if (col >= seam[row]) {
                    picture.setRGB(col, row, picture.getRGB(col + 1, row));
                    energy[col][row] = energy[col + 1][row];
                }
            }
        }

        for (int row = 0; row < height; row++) {
            for (int dir : DIR) {
                int col = seam[row] + dir;
                if (col >= 0 && col < width) {
                    energy[col][row] = 0;
                }
            }
        }

        width--;
    }


    //  unit testing (optional)
    public static void main(String[] args) {

    }
}
