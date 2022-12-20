/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point
    // private final Comparator<Point> BY_SLOPE = new _slopeOrder_(); // fails memory test

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y)  return Double.NEGATIVE_INFINITY;
        if (this.x == that.x)                      return Double.POSITIVE_INFINITY;
        if (this.y == that.y)                      return 0.0;

        double dx = (that.x - this.x);
        double dy = (that.y - this.y);
        return dy / dx;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (this.y <  that.y)                     return -1;
        if (this.y == that.y && this.x <  that.x) return -1;
        if (this.y == that.y && this.x == that.x) return 0;
        return 1;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new _slopeOrder_();
    }

    private class _slopeOrder_ implements Comparator<Point> {

        public int compare(Point q1, Point q2) {
            double slope1 = slopeTo(q1);
            double slope2 = slopeTo(q2);
            if (slope1 == Double.NEGATIVE_INFINITY && slope2 == Double.NEGATIVE_INFINITY)
                return 0; //throw new java.util.NoSuchElementException("Slope Order Sanity check: we compare a point with itself");
            if (slope1 == Double.POSITIVE_INFINITY && slope2 == Double.POSITIVE_INFINITY)
                return 0;
            if (java.lang.Math.abs(slope1 - slope2) < 1e-6 ) return 0;
            if (slope1 < slope2) return -1;
            return 1;
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provided for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        Point point1 = new Point(1,1);
        Point point2 = new Point(1,2);
        Point point3 = new Point(2,1);
        Point point4 = new Point(2,2);

        Point point5 = new Point(-1,1);
        Point point6 = new Point(1,-1);
        Point[] x = {point1, point2, point3, point4, point5, point6};
        java.util.Arrays.sort(x, point2.slopeOrder());
        for (int i = 0; i < 6; ++i)
            System.out.println(x[i].toString());

        // LineSegment segment  = new LineSegment(point5, point4);
        // LineSegment segment2 = new LineSegment(point1, point2);
        // segment.draw();
        // segment2.draw();
        // System.out.println(segment.toString());
        // System.out.println(segment2.toString());
    }
}
