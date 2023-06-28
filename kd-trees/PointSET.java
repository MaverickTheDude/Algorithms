import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;

public class PointSET {

    private SET<Point2D> m_pointSet;

    // private static class Node {
    //     private Point2D p;      // the point
    //     private RectHV rect;    // the axis-aligned rectangle corresponding to this node
    //     private Node lb;        // the left/bottom subtree
    //     private Node rt;        // the right/top subtree

    //     public Node(Point2D p, RectHV rect, Node lb, Node, rt) {
    //         this.p = p;
    //         this.rect = rect;
    //         this.lb = lb;
    //         this.rt = rt;
    //     }
    //  }

    public PointSET() {
        m_pointSet = new SET<Point2D>();
    }

    public boolean isEmpty() { return m_pointSet.isEmpty(); }
    
    public int size() { return m_pointSet.size(); }

    public void insert(Point2D point) { m_pointSet.add(point); }

    public boolean contains(Point2D point) { return m_pointSet.contains(point); }

    public void draw() {
        for (Point2D point : m_pointSet) {
            StdDraw.point(point.x(), point.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect) {

        java.util.ArrayList<Point2D> vect = new java.util.ArrayList<Point2D>();

        for (Point2D point : m_pointSet) {
            if ( point.x() > rect.xmin()  &&
                 point.y() > rect.ymin()  &&
                 point.x() < rect.xmax()  &&
                 point.y() < rect.ymax() )  
                    vect.add(point);
        }
        return vect;
    }

    public Point2D nearest(Point2D p)  {
        java.util.Iterator<Point2D> iter = m_pointSet.iterator();
        Point2D champion = iter.next();

        while(iter.hasNext()) {
            Point2D nextElement = iter.next();
            if (p.distanceSquaredTo(nextElement) < p.distanceSquaredTo(champion))
                champion = nextElement;
        }
        return champion;
    }

    public static void main(String[] args)  {}
}