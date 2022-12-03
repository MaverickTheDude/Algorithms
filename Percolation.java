import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] site;
    private int n;
    private int baseNodeOne;
    private int baseNodeTwo;
    private int NopenSites;
    private WeightedQuickUnionUF UF;
    
    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        this.n = n;
        this.NopenSites = 0;
        this.baseNodeOne = n*n;
        this.baseNodeTwo = n*n+1;
        if (n < 1) {
            throw new IllegalArgumentException("n must be positive");
        }
        UF = new WeightedQuickUnionUF(n*n+2);
        site = new boolean[n*n];
        for (int i = 0; i < n; i++) {
            this.UF.union(i, this.baseNodeOne); // bound first layer to artificial base-node
            this.site[i] = false;
        }
        for (int i = n; i < n*n; i++) {
            this.site[i] = false;
        }
        for (int i = n*(n-1); i < n*n; i++)
            this.UF.union(i, this.baseNodeTwo); // bound first layer to artificial base-node
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || col < 1 || row > this.n || col > this.n)
            throw new IllegalArgumentException("coordinates out of range");
        int id = get_id(row, col);
        if (this.site[id] == true)
            return;
        
        this.site[id] = true;
        this.NopenSites += 1;
        if (row < n && isOpen(row+1, col  ))  UF.union(id, get_id(row+1, col  ) );
        if (col < n && isOpen(row  , col+1))  UF.union(id, get_id(row  , col+1) );
        if (row > 1 && isOpen(row-1, col  ))  UF.union(id, get_id(row-1, col  ) );
        if (col > 1 && isOpen(row,   col-1))  UF.union(id, get_id(row  , col-1) );
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || col < 1 || row > this.n || col > this.n)
            throw new IllegalArgumentException("coordinates out of range");
        
        int id = get_id(row, col);
        boolean connected = (UF.find(id) == UF.find(this.baseNodeOne)); // is id-node connected to the base-node ?
        if (connected && isOpen(row,col))
            return true;
        else
            return false;
    }
    
    // does the system percolate?
    public boolean percolates() {
        if (UF.find(this.baseNodeOne) == UF.find(this.baseNodeTwo))
            return true;
        return false;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || col < 1 || row > this.n || col > this.n)
            throw new IllegalArgumentException("coordinates out of range");
        int id = get_id(row, col);
        
        return site[id];
    }

    // returns the number of open sites
    public int numberOfOpenSites() { return this.NopenSites; }
    
    private int get_id(int row, int col) {
        // (1,1) is the upper-left corner, row-major order
        return (row-1)*this.n + col - 1;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation perc = new Percolation(1);
        perc.open(2,3);
        perc.open(1,3);
        System.out.println( perc.isFull(2,3) );
        System.out.println( perc.isFull(1,4) );
        
        // System.out.println( perc.UF.connected(2,12) ); true
        // System.out.println( perc.isOpen(5,2) );
        System.out.println( perc.numberOfOpenSites() );
    }
}