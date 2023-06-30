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
        private boolean comp_x; // depth level in BST: if odd (1,3,...) we compare by x axis key

        private Node(Point2D point, RectHV rect, boolean comp_x) {
            this.point = point;
            this.rect = rect;
            this.comp_x = comp_x;
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
            // assert m_root == null;
            return true;
        }
        return false;
    }
    
    public int size() { return m_size; }

    public void insert(Point2D point) {

        if (point == null) 
            throw new java.lang.IllegalArgumentException("KdTree::insert(): null argument!\n");
        
        /* initial case: root node */
        if (isEmpty()) {
            m_root = new Node(point, new RectHV(0.0, 0.0, 1.0, 1.0), true);
            ++m_size;
            return;
        }

        Node currentNode = m_root;
        
        int level = 1;
        while (true) {
            /* test if it's vertical or horizontal key comparison */
            double compCurrentPoint, compInsertPoint;
            boolean comp_x = (level++ % 2 == 1); // first (root) or any second branch of the BST, i.e. odd numbers
            if (comp_x) {
                compCurrentPoint = currentNode.point.x();
                compInsertPoint = point.x();
            }
            else {
                compCurrentPoint = currentNode.point.y();
                compInsertPoint = point.y();
            }

            /* check if go left or right in the BST */
            if (compInsertPoint > compCurrentPoint) {
                /* if we moved to non-null node --> continue */
                if (currentNode.rt != null) {
                    currentNode = currentNode.rt;
                    continue;
                }
                double rect_xmin, rect_ymin;
                double rect_xmax = currentNode.rect.xmax();
                double rect_ymax = currentNode.rect.ymax();
                if (comp_x) {
                    rect_ymin = currentNode.rect.ymin();
                    rect_xmin = currentNode.point.x();
                }
                else {
                    rect_ymin = currentNode.point.y();
                    rect_xmin = currentNode.rect.xmin();
                }

                currentNode.rt = new Node(point, new RectHV(rect_xmin, rect_ymin, rect_xmax, rect_ymax), !comp_x);
                ++m_size;
                return;
            }
            else if (compInsertPoint <= compCurrentPoint) {
                /* if we moved to non-null node --> continue */
                if (currentNode.lb != null) {
                    currentNode = currentNode.lb;
                    continue;
                }
                double rect_xmax, rect_ymax;
                double rect_xmin = currentNode.rect.xmin();
                double rect_ymin = currentNode.rect.ymin();
                if (comp_x) {
                    rect_ymax = currentNode.rect.ymax();
                    rect_xmax = currentNode.point.x();
                }
                else {
                    rect_ymax = currentNode.point.y();
                    rect_xmax = currentNode.rect.xmax();
                }

                currentNode.lb = new Node(point, new RectHV(rect_xmin, rect_ymin, rect_xmax, rect_ymax), !comp_x);
                ++m_size;
                return;
            }
        }
    }

    public boolean contains(Point2D point) {
        if (point == null) 
            throw new java.lang.IllegalArgumentException("KdTree::contains(): null argument!\n");

        if (m_root == null) return false;

        Node currentNode = m_root;
        
        int level = 1;
        while (currentNode != null) {

        // useful debug statements
        /* StdOut.printf("level = %d\n", level);
        RectHV rect = currentNode.rect;
        StdOut.printf("point inside: %f, %f\n", currentNode.point.x(), currentNode.point.y());
        StdOut.printf("x0 = %f, y0 = %f x1 = %f, y1 = %f\n", rect.xmin(), rect.ymin(), rect.xmax(), rect.ymax()); */

            if (currentNode.point.equals(point))
                return true;

            double compCurrentPoint, compInsertPoint;
            boolean comp_x = (level++ % 2 == 1);

            if (comp_x) {
                compCurrentPoint = currentNode.point.x();
                compInsertPoint = point.x();
            }
            else {
                compCurrentPoint = currentNode.point.y();
                compInsertPoint = point.y();
            }

            /* check if go left or right in the BST */
            if (compInsertPoint > compCurrentPoint) {
// StdOut.printf("Contains goes right/top\n");

                currentNode = currentNode.rt;
            }
            else if (compInsertPoint <= compCurrentPoint) {
// StdOut.printf("Contains goes left/bottom\n");

                currentNode = currentNode.lb;
            }
        }

        return false;
    }

    private void iterateOverNode_DRAW(Node currentNode) {
        if (currentNode == null)
            return;
        
        Point2D point = currentNode.point;
        StdDraw.point(point.x(), point.y());

        iterateOverNode_DRAW(currentNode.rt);
        iterateOverNode_DRAW(currentNode.lb);
    }

    public void draw() {
        iterateOverNode_DRAW(m_root);
    }

    private void iterateOverNode_RANGE(Node currentNode, RectHV querryRect, java.util.ArrayList<Point2D> vect) {
        if (currentNode == null)
            return;
        
        /* prune if querry rectangle doesn't intersect node rectangle */
        if (!querryRect.intersects(currentNode.rect))
            return;
        
        if (querryRect.contains(currentNode.point))
            vect.add(currentNode.point);

        iterateOverNode_RANGE(currentNode.rt, querryRect, vect);
        iterateOverNode_RANGE(currentNode.lb, querryRect, vect);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) 
            throw new java.lang.IllegalArgumentException("KdTree::range(): null argument!\n");

        java.util.ArrayList<Point2D> vect = new java.util.ArrayList<Point2D>();
        
        iterateOverNode_RANGE(m_root, rect, vect);
        
        return vect;
    }
    
    private Point2D iterateOverNode_NEAREST(Point2D champion, Point2D querryPoint, Node currentNode) {
        if (currentNode == null)
            return champion;

        double distQuerryPoint_champion = champion.distanceSquaredTo(querryPoint);
        if (currentNode.point.distanceSquaredTo(querryPoint) < distQuerryPoint_champion)
            champion = currentNode.point;
            
        /* pruning condition */
        distQuerryPoint_champion = champion.distanceSquaredTo(querryPoint);
        double distQuerryPoint_currNodeSquare = currentNode.rect.distanceSquaredTo(querryPoint);
        if (distQuerryPoint_champion < distQuerryPoint_currNodeSquare)
            return champion;
        
        double compCurrentPoint, compQuerryPoint;
        if (currentNode.comp_x) {
            compCurrentPoint = currentNode.point.x();
            compQuerryPoint = querryPoint.x();
        }
        else {
            compCurrentPoint = currentNode.point.y();
            compQuerryPoint = querryPoint.y();
        }

        Node firstVisitNode;
        Node secondVisitNode;
        if (compQuerryPoint <= compCurrentPoint) {
            firstVisitNode = currentNode.lb;
            secondVisitNode = currentNode.rt;
        }
        else {
            firstVisitNode = currentNode.rt;
            secondVisitNode = currentNode.lb;
        }

        champion = iterateOverNode_NEAREST(champion, querryPoint, firstVisitNode);
        champion = iterateOverNode_NEAREST(champion, querryPoint, secondVisitNode);
        return champion;
    }
    
    public Point2D nearest(Point2D querryPoint)  {
        if (querryPoint == null) 
            throw new java.lang.IllegalArgumentException("KdTree::nearest(): null argument!\n");
        if (isEmpty())
            return null;

        Point2D champion = m_root.point;
        return iterateOverNode_NEAREST(champion, querryPoint, m_root);
    }

/*     public static void main(String[] args)  {
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        // Point2D p = new Point2D(0.792202, 0.762825);
        Point2D p = new Point2D(0.615232, 0.064454);
        // Point2D p = new Point2D(0.792202, 0.762825);
        // Point2D p = new Point2D(0.4, 0.1);
        
        // StdOut.printf("contains point  %b\n", kdtree.contains(p1));
        StdOut.printf("contains point  %b\n", kdtree.contains(p));
        // StdOut.printf("contains point  %b\n", kdtree.contains(p3));
        // StdOut.printf("contains point  %b\n", kdtree.contains(p4));

        // StdDraw.clear();
        // StdDraw.setPenColor(StdDraw.BLACK);
        // StdDraw.setPenRadius(0.01);
        // kdtree.draw();
        // StdDraw.show();

    } */
}