import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class FastCollinearPoints {

    private final int INITIAL_QUAD_CAPACITY = 6;
    private final int INITIAL_LINE_SEGMENTS_CAPACITY = 50;
    private vector<LineSegment> segmentsTab;

    private class Quad {
        private vector<Point> pointsALongLine;

        public Quad() {
            pointsALongLine = new vector<Point>(INITIAL_QUAD_CAPACITY);
        }

        public void push_back(Point point) {
            pointsALongLine.push_back(point);
        }

        public boolean partOfLine(Point point_i, Point point_j) {
            if (point_i.compareTo(point_j) == 0) throw new IllegalArgumentException("partOfLine: points are the same (should be distinct)");
            
            boolean point_i_ok = false;
            boolean point_j_ok = false;
            for (int it = 0; it < pointsALongLine.size(); ++it){
                if ( point_i.compareTo(pointsALongLine.at(it)) == 0 ) point_i_ok = true;
                if ( point_j.compareTo(pointsALongLine.at(it)) == 0 ) point_j_ok = true;
                if (point_i_ok && point_j_ok)                    return true;
            }
            return false;
        }

        public int elements() { return pointsALongLine.size(); }

        public LineSegment toSegments() {
            Point[] tmpTab = new Point[pointsALongLine.size()];
            for (int i = 0; i < pointsALongLine.size(); ++i)
                tmpTab[i] = pointsALongLine.at(i);
            java.util.Arrays.sort(tmpTab);
            LineSegment segmentOut = new LineSegment(tmpTab[0], tmpTab[tmpTab.length-1]);
            tmpTab = null;
            return segmentOut;
        }
    }

    private class vector<Item> {
        private Item[] m_items;
        private int m_elements = 0;
        private int m_capacity;

        @SuppressWarnings("unchecked")
        public vector(int capacity) {
            m_capacity = capacity;
            this.m_items = (Item[]) new Object[m_capacity];
        }

        public Item at(int index) {
            if (index >= m_elements)
                throw new IllegalArgumentException("vector: index out of range");
            return m_items[index];
        }

        public void push_back(Item item) {
            if (m_elements == m_capacity) {
                resize();
            }
            m_items[m_elements++] = item;
        }

        @SuppressWarnings("unchecked")
        public void resize() {
            Item[] newTab = (Item[]) new Object[2*m_capacity];
            for (int i = 0; i < m_capacity; ++i)
                newTab[i] = m_items[i];
            m_items = newTab;
            m_capacity *= 2;
        }

        public int size() { return m_elements; }
    }

    private Point[] removeSelf(Point myself, Point[] points) {
        Point[] newPointsTab = new Point[points.length-1];
        int cnt = 0;
        for (int i = 0; i < points.length; ++i) {
            if (myself.compareTo(points[i]) != 0)
                newPointsTab[cnt++] = points[i];
        }
        return newPointsTab;
    }

    public FastCollinearPoints(Point[] points) {         // finds all line segments containing 4 or more points
        cornerCases(points);
        final int Npoints = points.length;
        segmentsTab = new vector<LineSegment>(INITIAL_LINE_SEGMENTS_CAPACITY);
        vector<Quad> linesTotal = new vector<Quad>(INITIAL_LINE_SEGMENTS_CAPACITY);

        for (int i = 0; i < Npoints; ++i) {              // for any point i...

            Point point_i = points[i];
            Point[] currentPointsTab = removeSelf(point_i, points);
            java.util.Arrays.sort(currentPointsTab, point_i.slopeOrder());

            int Nslopes = Npoints - 1;
            double[] tmpSlopes = new double[Nslopes];
            for (int j = 0; j < Npoints-1; ++j)          // make *monotonic* table of slopes with point_i...
                tmpSlopes[j] = point_i.slopeTo(currentPointsTab[j]);

            searchForThreeIdenticalSlopes:
            for (int j = 0; j < Npoints-3; ++j) {

                for (int it = 0; it < linesTotal.size(); ++it){     // continue main loop if linesTotal has point_i and point_j
                    if (linesTotal.at(it).partOfLine(point_i, currentPointsTab[j]))
                        break searchForThreeIdenticalSlopes;
                }
                
                if (compare(tmpSlopes[j], tmpSlopes[j+1]) && 
                compare(tmpSlopes[j], tmpSlopes[j+2])) {
                    
                    Quad line = new Quad();
                    line.push_back(point_i);
                    line.push_back(currentPointsTab[j]);
                    line.push_back(currentPointsTab[j+1]);
                    line.push_back(currentPointsTab[j+2]);

                    /* corner case: check if 5 or more points are along the line */
                    for (int k = j+3; k < Nslopes; ++k) {
                        if ( compare(tmpSlopes[j], tmpSlopes[k]) )
                            line.push_back(currentPointsTab[k]);
                        else break;
                    }
                    j += line.elements() - 1; // shift j three (or more) units right
                    linesTotal.push_back(line);
                }
            }
        }

        for (int it = 0; it < linesTotal.size(); ++it)
            segmentsTab.push_back( linesTotal.at(it).toSegments() );
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) { // how does for-each work without implementing iterator for vector class?
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    /*public static void main(String[] args) {
        Point point0 = new Point(1,1);
        Point point1 = new Point(2,2);
        Point point2 = new Point(3,3);
        Point point3 = new Point(4,4);
        Point point3a = new Point(5,5);
        Point point4 = new Point(3,1);

        Point point5 = new Point(1,3);
        Point point6 = new Point(4,0);
        Point point7 = new Point(3,5);
        Point point8 = new Point(3,0);
        Point point9 = new Point(5,-1);

        Point[] points = {point0, point1, point2, point3, point3a, point4, point5, point6, point7, point8, point9};
        FastCollinearPoints fascioch = new FastCollinearPoints(points);
        LineSegment[] segment = fascioch.segments();
        
        System.out.println(fascioch.numberOfSegments());
        System.out.println(segment[0].toString());
        System.out.println(segment[1].toString());
        System.out.println(segment[2].toString());
    }  */

    private void cornerCases(Point[] points) {
        if (points == null) throw new IllegalArgumentException("FastCollinearPoints: input points table is null");
        if (points[0] == null) throw new IllegalArgumentException("FastCollinearPoints: input points table element is null");
        
        for (int i = 0; i < points.length-1; ++i) {
            for (int j = i+1; j < points.length; ++j) {
                if (points[j] == null)
                    throw new IllegalArgumentException("FastCollinearPoints: input points table element is null");
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("FastCollinearPoints: input points table has duplicate elements");
            }
        }
    }

    private boolean compare(double x, double y) {
        if (x == Double.POSITIVE_INFINITY && y == Double.POSITIVE_INFINITY)
            return true;
        return java.lang.Math.abs(x - y) < 1e-6;
    }
    
    public int numberOfSegments() {       // the number of line segments
        return this.segmentsTab.size();
    }

    public LineSegment[] segments() {               // the line segments
        int Nsegments = segmentsTab.size();
        LineSegment[] out = new LineSegment[Nsegments];
        for (int i = 0; i < Nsegments; ++i)
            out[i] = segmentsTab.at(i);
        return out;
    }
}