import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {

    private int allSites;
    private int Ntrials;
    private double[] trialsTab;
    private double CONFIDENCE_95 = 1.96;


    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1 || trials < 1)
            throw new IllegalArgumentException("parameters out of range");

        this.allSites = n*n;
        this.Ntrials  = trials;
        this.trialsTab = new double[trials];
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(this.trialsTab);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.trialsTab);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double s = stddev();
        double x = mean();
        return (x - CONFIDENCE_95*s / Math.sqrt(Ntrials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double s = stddev();
        double x = mean();
        return (x + CONFIDENCE_95*s / Math.sqrt(Ntrials));
    }

    // test client (see below)
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        PercolationStats PS = new PercolationStats(N, T);

        for (int i = 0; i < T; i++) {
            Percolation perc = new Percolation(N);
            while (true) {
                int row = StdRandom.uniformInt(1, N+1);
                int col = StdRandom.uniformInt(1, N+1);
                perc.open(row, col);

                if (perc.percolates()) {
                    double openSites = (double) perc.numberOfOpenSites();
                    PS.trialsTab[i] = openSites / (N*N);
                    // StdOut.printf("ops = %f\n", PS.trialsTab[i]);
                    break;
                }
            }
        }

        StdOut.printf("mean \t\t = %f\n", PS.mean());
        StdOut.printf("stddev \t\t = %f\n", PS.stddev());
        StdOut.printf("95%% confidence interval \t = [%f, %f]\n", PS.confidenceLo(), PS.confidenceHi());
    }

}
