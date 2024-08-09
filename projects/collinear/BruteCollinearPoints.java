import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {

    private List<LineSegment> collinearLine = new ArrayList<>();

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        // checking
        if (points == null) {
            throw new IllegalArgumentException("argument to constructor is null.");
        }
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException("one point is null.");
            }
        }

        int len = points.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("repeated points.");
                }
            }
        }
        if (len < 4) {
            throw new IllegalArgumentException("Too few points.");
        }
        Point[] temp = Arrays.copyOf(points, len);
        Arrays.sort(temp);
        // Brute force search, high time complexity
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                for (int k = j + 1; k < len; k++) {
                    for (int h = k + 1; h < len; h++) {
                        double s1 = temp[i].slopeTo(temp[j]);
                        double s2 = temp[i].slopeTo(temp[k]);
                        double s3 = temp[i].slopeTo(temp[h]);
                        if (s1 == s2 && s1 == s3) {
                            collinearLine.add(new LineSegment(temp[i], temp[h]));
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return collinearLine.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] retValue = new LineSegment[numberOfSegments()];
        int i = 0;
        for (LineSegment x : collinearLine) {
            retValue[i++] = x;
        }
        return retValue;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
