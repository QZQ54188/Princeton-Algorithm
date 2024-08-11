import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class KdTree {

    private Node root;
    private int size;

    private static class Node {
        private Point2D point;
        private Node left, right;
        private RectHV rect;

        Node(Point2D p, RectHV rect) {
            this.rect = rect;
            this.point = p;
            this.left = null;
            this.right = null;
        }
    }

    public KdTree() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        else if (contains(point)) {
            return;
        }
        size++;
        root = put(point, root, true, 0, 0, 1, 1);
    }

    public boolean contains(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        return get(root, point, true) != null;
    }

    private Point2D get(Node root, Point2D point, boolean isXPartition) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        else if (root == null) {
            return null;
        }
        else if (root.point.compareTo(point) == 0) {
            return root.point;
        }

        int cmp = isXPartition ? Double.compare(point.x(), root.point.x()) :
                  Double.compare(point.y(), root.point.y());
        if (cmp < 0) {
            return get(root.left, point, !isXPartition);
        }
        else {
            return get(root.right, point, !isXPartition);
        }
    }

    private Node put(Point2D point, Node root, boolean isXPartition, double xmin, double ymin,
                     double xmax, double ymax) {
        if (root == null) {
            return new Node(point, new RectHV(xmin, ymin, xmax, ymax));
        }
        int cmp = isXPartition ? Double.compare(point.x(), root.point.x()) :
                  Double.compare(point.y(), root.point.y());
        if (cmp < 0) {
            if (isXPartition) {
                xmax = root.point.x();
            }
            else {
                ymax = root.point.y();
            }
            root.left = put(point, root.left, !isXPartition, xmin, ymin, xmax, ymax);
        }
        else {
            if (isXPartition) {
                xmin = root.point.x();
            }
            else {
                ymin = root.point.y();
            }
            root.right = put(point, root.right, !isXPartition, xmin, ymin, xmax, ymax);
        }
        return root;
    }


    public void draw() {
        draw(root, true);
    }

    private void draw(Node root, boolean isXPartition) {
        if (root == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(root.point.x(), root.point.y());
        StdDraw.setPenRadius();
        if (isXPartition) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(root.point.x(), root.rect.ymin(), root.point.x(), root.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(root.rect.xmin(), root.point.y(), root.rect.xmax(), root.point.y());
        }
        draw(root.left, !isXPartition);
        draw(root.right, !isXPartition);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<Point2D> res = new ArrayList<>();
        if (!isEmpty()) {
            range(rect, root, res);
        }
        return res;
    }


    private void range(RectHV rect, Node root, ArrayList<Point2D> arr) {
        if (rect.contains(root.point)) {
            arr.add(root.point);
        }
        if (root.left != null && rect.intersects(root.left.rect)) {
            range(rect, root.left, arr);
        }
        if (root.right != null && rect.intersects(root.right.rect)) {
            range(rect, root.right, arr);
        }
    }


    public Point2D nearest(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        Champion champion = new Champion(root.point, Double.POSITIVE_INFINITY);
        nearest(root, point, true, champion);
        return champion.point;
    }


    private static class Champion {
        Point2D point;
        double dis;

        Champion(Point2D point, double dis) {
            this.point = point;
            this.dis = dis;
        }
    }


    private void nearest(Node root, Point2D point, boolean isXPartition, Champion champion) {
        double curDis = point.distanceSquaredTo(root.point);
        if (curDis < champion.dis) {
            champion.dis = curDis;
            champion.point = root.point;
        }

        int cmp = isXPartition ? Double.compare(point.x(), root.point.x()) :
                  Double.compare(point.y(), root.point.y());
        boolean isLeftFirst = cmp < 0;
        if (isLeftFirst) {
            if (root.left != null && champion.dis > root.left.rect.distanceSquaredTo(point)) {
                nearest(root.left, point, !isXPartition, champion);
            }
            if (root.right != null && champion.dis > root.right.rect.distanceSquaredTo(point)) {
                nearest(root.right, point, !isXPartition, champion);
            }
        }
        else {
            if (root.right != null && champion.dis > root.right.rect.distanceSquaredTo(point)) {
                nearest(root.right, point, !isXPartition, champion);
            }
            if (root.left != null && champion.dis > root.left.rect.distanceSquaredTo(point)) {
                nearest(root.left, point, !isXPartition, champion);
            }
        }
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        kdtree.draw();
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.point(0.81, 0.30);
        Point2D nearest = kdtree.nearest(new Point2D(0.81, 0.30));
        StdDraw.point(nearest.x(), nearest.y());
        StdOut.println(nearest);
    }


}
