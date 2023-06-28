import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;

public class KdTree {

    private Node m_root;
    private int m_size;

    private static class Node {
        private Point2D point;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        private Node(Point2D point, RectHV rect) {
            this.point = point;
            this.rect = rect;
            lb = null;
            rt = null;
        }
    }

    public KdTree() {
        m_root = null;
        m_size = 0;
    }

    public boolean isEmpty() {
        if (m_size == 0) {
            assert m_root == null; // do zweryfikowania
            return true;
        }
        return false;
    }
    
    public int size() { return m_size; }

    public void insert(Point2D point) {
        
        /* initial case: root node */
        if (m_root == null) {
            m_root = new Node(point, new RectHV(0.0, 0.0, 1.0, 1.0));
            ++m_size;
            return;
        }

        Node currentNode = m_root;
        
        int level = 1;
        while (true) {
            /* test if it's vertical or horizontal key comparison */
            double compOurs, compTheirs;
            boolean odd1 = (level++ % 2 == 1); // first (root) or any second branch of the BST
            if (odd1) {
                compOurs = currentNode.point.x();
                compTheirs = point.x();
            }
            else {
                compOurs = currentNode.point.y();
                compTheirs = point.y();
            }

            /* check if go left or right in the BST */
            if (compOurs < compTheirs) {
                /* if we moved to non-null node --> continue */
                if (currentNode.lb != null) {
                    currentNode = currentNode.lb;
                    continue;
                }
                double rect_xmax, rect_ymax;
                double rect_xmin = currentNode.rect.xmin(); // EXTRA WORK TO DO HERE
                double rect_ymin = currentNode.rect.ymin();
                if (odd1) {
                    rect_ymax = currentNode.point.y();
                    rect_xmax = currentNode.rect.xmax();
                }
                else {
                    rect_ymax = currentNode.rect.ymax();
                    rect_xmax = currentNode.point.x();
                }
                currentNode.lb = new Node(point, new RectHV(rect_xmin, rect_ymin, rect_xmax, rect_ymax));
                ++m_size;
                return;
            }
            else if (compOurs > compTheirs) {
                /* if we moved to non-null node --> continue */
                if (currentNode.rt != null) {
                    currentNode = currentNode.rt;
                    continue;
                }
                double rect_xmin, rect_ymin;
                double rect_xmax = currentNode.rect.xmax();
                double rect_ymax = currentNode.rect.ymax();
                if (odd1) {
                    rect_ymin = currentNode.point.y();
                    rect_xmin = currentNode.rect.xmin();
                }
                else {
                    rect_ymin = currentNode.rect.ymin();
                    rect_xmin = currentNode.point.x();
                }
                currentNode.rt = new Node(point, new RectHV(rect_xmin, rect_ymin, rect_xmax, rect_ymax));
                ++m_size;
                return;
            }
            else throw new IllegalArgumentException("KdTree::Insert: degeneracy!\n");
        }
    }

    public boolean contains(Point2D point) {
        if (m_root == null) return false;

        Node currentNode = m_root;
        
        int level = 1;
        while (currentNode != null) {

            if (currentNode.point.equals(point))
                return true;

            double compOurs, compTheirs;
            boolean odd1 = (level++ % 2 == 1);

            StdOut.printf("level = %d, odd number:  %b\n", level-1, odd1);

            if (odd1) {
                compOurs = currentNode.point.x();
                compTheirs = point.x();
            }
            else {
                compOurs = currentNode.point.y();
                compTheirs = point.y();
            }

            /* check if go left or right in the BST */
            if (compOurs < compTheirs) {
                currentNode = currentNode.lb;
            }
            else if (compOurs > compTheirs) {
                currentNode = currentNode.rt;
            }
            else throw new IllegalArgumentException("KdTree::Insert: degeneracy!\n");
        }

        return false;
    }

    // public void draw() {
    //     for (Point2D point : m_pointSet) {
    //         StdDraw.point(point.x(), point.y());
    //     }
    // }

    private void iterateOverNode_RANGE(Node currentNode, RectHV querryRect, java.util.ArrayList<Point2D> vect) {
        if (currentNode == null)
            return;
        
        /* prune if querry rectangle doesn't intersect node rectangle */
        if (!querryRect.intersects(currentNode.rect))
            return;
        
        /* return if no child nodes */
        // if (currentNode.lb == null && currentNode.rt == null)
            // return;
        /* check right branch of the subree */
        // if (currentNode.lb == null)
            // iterateOverNode_RANGE(currentNode.rt, querryRect, vect);
        
        if (querryRect.contains(currentNode.point))
            vect.add(currentNode.point);

        iterateOverNode_RANGE(currentNode.rt, querryRect, vect);
        iterateOverNode_RANGE(currentNode.lb, querryRect, vect);
    }

    public Iterable<Point2D> range(RectHV rect) {

        java.util.ArrayList<Point2D> vect = new java.util.ArrayList<Point2D>();

        iterateOverNode_RANGE(m_root, rect, vect);

        return vect;
    }

    // public Point2D nearest(Point2D p)  {
    //     java.util.Iterator<Point2D> iter = m_pointSet.iterator();
    //     Point2D champion = iter.next();

    //     while(iter.hasNext()) {
    //         Point2D nextElement = iter.next();
    //         if (p.distanceSquaredTo(nextElement) < p.distanceSquaredTo(champion))
    //             champion = nextElement;
    //     }
    //     return champion;
    // }

    public static void main(String[] args)  {
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        // Point2D p1 = new Point2D(0.455495, 0.191372);
        // Point2D p2 = new Point2D(0.463559, 0.187845);
        Point2D p3 = new Point2D(0.792202, 0.762825);
        // Point2D p4 = new Point2D(0.4, 0.1);
        
        // StdOut.printf("contains point  %b\n", kdtree.contains(p1));
        // StdOut.printf("contains point  %b\n", kdtree.contains(p2));
        StdOut.printf("contains point  %b\n", kdtree.contains(p3));
        // StdOut.printf("contains point  %b\n", kdtree.contains(p4));

    }
}