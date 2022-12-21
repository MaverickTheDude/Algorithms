import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;

public class BruteCollinearPoints {

    private final int INITIAL_QUAD_CAPACITY = 4;
    private final int INITIAL_LINE_SEGMENTS_CAPACITY = 50;
    private vector_ls segmentsTab;

    private class Quad {

        private vector_int lineIndices;

        public Quad() { lineIndices = new vector_int(INITIAL_QUAD_CAPACITY); } // is default c-tor needed when we call Quad[] (tab of empty Quad objects) ?

        public Quad(int i, int j, int k, int s) {
            lineIndices = new vector_int(INITIAL_QUAD_CAPACITY);
            lineIndices.push_back(i);
            lineIndices.push_back(j);
            lineIndices.push_back(k);
            lineIndices.push_back(s);
        }

        public boolean partOfLine(int ind1, int ind2) {
            if (ind1 == ind2) throw new IllegalArgumentException("partOfLine: indices are the same (should be distinct)");
            
            boolean ind_i_ok = false;
            boolean ind_j_ok = false;
            for (int it = 0; it < lineIndices.size(); ++it) {
                if ( ind1 == lineIndices.at(it) ) ind_i_ok = true;
                if ( ind2 == lineIndices.at(it) ) ind_j_ok = true;
                if (ind_i_ok && ind_j_ok)         return true;
            }
            return false;
        }

        public void push_back(int ind) {
            lineIndices.push_back(ind);
        }

        public void push_back(int i, int j, int k, int s) {
            lineIndices.push_back(i);
            lineIndices.push_back(j);
            lineIndices.push_back(k);
            lineIndices.push_back(s);
        }

        public int elements() { return lineIndices.size(); }

        public LineSegment toSegments(Point[] points) {
            Point[] tmpTab = new Point[lineIndices.size()];
            for (int i = 0; i < lineIndices.size(); ++i) {
                int pointIndex = lineIndices.at(i);
                tmpTab[i] = points[pointIndex];
            }
            java.util.Arrays.sort(tmpTab);
            LineSegment segmentOut = new LineSegment(tmpTab[0], tmpTab[tmpTab.length-1]);
            tmpTab = null;
            return segmentOut;
        }
    }

    private class vector_int {
        private Integer[] m_items;
        private int m_elements = 0;
        private int m_capacity;

        public vector_int(int capacity) {
            m_capacity = capacity;
            this.m_items = new Integer[m_capacity];
        }

        public int at(int index) {
            if (index >= m_elements)
                throw new IllegalArgumentException("vector: index out of range");
            return m_items[index];
        }

        public void push_back(int item) {
            if (m_elements == m_capacity)
                resize();
            m_items[m_elements++] = item;
        }

        public void resize() {
            Integer[] newTab = new Integer[2*m_capacity];
            for (int i = 0; i < m_capacity; ++i)
                newTab[i] = m_items[i];
            m_items = newTab;
            m_capacity *= 2;
        }

        public int size() { return m_elements; }
    }

    private class vector_quad {
        private Quad[] m_items;
        private int m_elements = 0;
        private int m_capacity;

        public vector_quad(int capacity) {
            m_capacity = capacity;
            this.m_items = new Quad[m_capacity];
        }

        public Quad at(int index) {
            if (index >= m_elements)
                throw new IllegalArgumentException("vector: index out of range");
            return m_items[index];
        }

        public void push_back(Quad item) {
            if (m_elements == m_capacity)
                resize();
            m_items[m_elements++] = item;
        }

        public void resize() {
            Quad[] newTab = new Quad[2*m_capacity];
            for (int i = 0; i < m_capacity; ++i)
                newTab[i] = m_items[i];
            m_items = newTab;
            m_capacity *= 2;
        }

        public int size() { return m_elements; }
    }

    private class vector_ls {
        private LineSegment[] m_items;
        private int m_elements = 0;
        private int m_capacity;

        public vector_ls(int capacity) {
            m_capacity = capacity;
            this.m_items = new LineSegment[m_capacity];
        }

        public LineSegment at(int index) {
            if (index >= m_elements)
                throw new IllegalArgumentException("vector: index out of range");
            return m_items[index];
        }

        public void push_back(LineSegment item) {
            if (m_elements == m_capacity)
                resize();
            m_items[m_elements++] = item;
        }

        public void resize() {
            LineSegment[] newTab = new LineSegment[2*m_capacity];
            for (int i = 0; i < m_capacity; ++i)
                newTab[i] = m_items[i];
            m_items = newTab;
            m_capacity *= 2;
        }

        public int size() { return m_elements; }
    }

    public BruteCollinearPoints(Point[] points) {        // finds all line segments containing 4 or more points
        cornerCases(points);
        final int Npoints = points.length;
        segmentsTab = new vector_ls(INITIAL_LINE_SEGMENTS_CAPACITY);
        vector_quad linesTotal = new vector_quad(INITIAL_LINE_SEGMENTS_CAPACITY);

        for (int i = 0; i < Npoints; ++i) {              // for any point i...
            double[] tmpSlopes = new double[Npoints];
            for (int j = 0; j < Npoints; ++j)            // make table of slopes with any point j...
                tmpSlopes[j] = points[i].slopeTo(points[j]);

            searchForThreeIdenticalSlopes:
            for (int j = 0; j < Npoints-3; ++j) {         // scan the whole table for 3 equal numbers: point_j ...
                if (i == j) continue;
                
                for (int it = 0; it < linesTotal.size(); ++it)    // (but break if i,j are part of already found line)
                    if (linesTotal.at(it).partOfLine(i,j)) break searchForThreeIdenticalSlopes;

                double currentSlope = tmpSlopes[j];
                Quad tmpLine = new Quad();
                
                for (int k = j+1; k < Npoints-1; ++k) {   // ... point_k ...
                    if (compare(tmpSlopes[k], currentSlope)) {
                        boolean moreThanFourPoints = false;
                        for (int s = k+1; s < Npoints; ++s) {    // ... point_s
                            if (compare(tmpSlopes[s], currentSlope)) {
                                if (! moreThanFourPoints) {
                                    tmpLine.push_back(i,j,k,s);
                                    moreThanFourPoints = true;
                                }
                                else
                                    tmpLine.push_back(s);
                            }
                        }
                    }
                }
                if (tmpLine.elements() > 0)
                    linesTotal.push_back(tmpLine);
            }
        }
        for (int it = 0; it < linesTotal.size(); ++it)
            segmentsTab.push_back( linesTotal.at(it).toSegments(points) );
    }

    /*public static void main(String[] args) {

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
        LineSegment[] segmentsTab = collinear.segments();
        for (int it = 0; it < collinear.numberOfSegments(); ++it) {
            LineSegment segment = segmentsTab[it];
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
    

    public static void main(String[] args) {
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

        Point[] points = {point1, point0, point2, point3, point3a, point4, point5, point6, point7, point8, point9};
        BruteCollinearPoints brutal = new BruteCollinearPoints(points);
        LineSegment[] segment = brutal.segments();
        
        System.out.println(brutal.numberOfSegments());
        System.out.println(segment[0].toString());
        System.out.println(segment[1].toString());
        System.out.println(segment[2].toString());
    } */

    private void cornerCases(Point[] points) {
        if (points == null) throw new IllegalArgumentException("BruteCollinearPoints: input points table is null");
        if (points[0] == null) throw new IllegalArgumentException("BruteCollinearPoints: input points table element is null");
        
        for (int i = 0; i < points.length-1; ++i) {
            for (int j = i+1; j < points.length; ++j) {
                if (points[j] == null)
                    throw new IllegalArgumentException("BruteCollinearPoints: input points table element is null");
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("BruteCollinearPoints: input points table has duplicate elements");
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
        int Nsegments = numberOfSegments();
        LineSegment[] out = new LineSegment[Nsegments];
        for (int i = 0; i < Nsegments; ++i)
            out[i] = segmentsTab.at(i);
        return out;
    }
}