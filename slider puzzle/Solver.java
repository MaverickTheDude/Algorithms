import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.LinkedList;

public class Solver {

    private Board initialBoard;
    private int INITIAL_DATA_STRUCT_LENGTH;

    // important note: it's ok to use only references to the Board here and in SearchNode (!)
    // note: we don't need that (but it's done, so let's keep it...)
    private int[][] unnecessaryFun(String input) {
        String[] values = input.split("\\s+");
        int Nvals = values.length - 1;
        assert Nvals == Integer.parseInt( values[0]) : "wrong input to Board constructor";
        int boardSize = (int) Math.sqrt(Nvals);

        int[][] tiles = new int[boardSize][boardSize];
        int counter = 1;        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                tiles[i][j] = Integer.parseInt( values[counter++] );
            }
        }
        return tiles;
    }

    private class SolutionPair {
        Iterable<Board> solutionList;
        int maxMoves;

        public SolutionPair(Iterable<Board> list, int moves) {
            solutionList = list;
            maxMoves = moves;
        }
    }

    private class SearchNode implements Comparable<SearchNode> {

        private Board m_board;
        private SearchNode m_previousNode;
        private int m_moves;
        private int m_manhattan;
        // private int m_hamming;

        public SearchNode(Board initialBoard, SearchNode previous){
            // m_board = new Board(unnecessaryFun(initialBoard.toString())); // no need to copy this here
            m_board = initialBoard;
            m_manhattan = m_board.manhattan();
            // m_hamming   = m_board.hamming();
            m_previousNode = previous;
            if (previous == null)
                m_moves = 0;
            else {
                m_moves = previous.m_moves+1;
            }
        }

        private int penalty() {
            // return m_hamming + m_moves;
            return m_manhattan + m_moves;
        }

        public int compareTo(SearchNode that) {
            int penaltyOurs   = this.penalty();
            int penaltyTheirs = that.penalty();
            if (penaltyOurs == penaltyTheirs) return 0;
            if (penaltyOurs < penaltyTheirs) return -1;
            return 1;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Solver::Solver(Board): null input");
        // initialBoard = new Board(unnecessaryFun(initial.toString()));
        initialBoard = initial;
        INITIAL_DATA_STRUCT_LENGTH = (int) (1.5 * initialBoard.manhattan());
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solution() != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        SolutionPair solution = solutionWrapper();
        return solution.maxMoves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        SolutionPair solution = solutionWrapper();
        return solution.solutionList;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    private SolutionPair solutionWrapper() {
        
        Board twinBoard = initialBoard.twin();
        MinPQ<SearchNode> priorityQueueSolution = createInitialSearchNode(initialBoard, INITIAL_DATA_STRUCT_LENGTH);
        MinPQ<SearchNode> priorityQueueTwin     = createInitialSearchNode(twinBoard,    INITIAL_DATA_STRUCT_LENGTH);

        while (true) {
            SearchNode nodeSolution = priorityQueueSolution.delMin();
            SearchNode nodeTwin     = priorityQueueTwin.delMin();
            
            // Stopping criteria
            if (nodeSolution.m_board.isGoal()) {
                Iterable<Board> solutionBoard = computeSolutionList(nodeSolution);
                int minMoves = nodeSolution.m_moves;
                SolutionPair solution = new SolutionPair(solutionBoard, minMoves);
                return solution;
            }
            else if (nodeTwin.m_board.isGoal())
                return new SolutionPair(null, -1);

            // Lockstep 1 -- solution
            Iterable<Board> neighborsSolution = nodeSolution.m_board.neighbors();

            for (Board board : neighborsSolution) {
                SearchNode previousNodeSolution = nodeSolution.m_previousNode;
                if (previousNodeSolution != null && board.equals(previousNodeSolution.m_board))
                    continue;
                SearchNode newNodeSolution = new SearchNode(board, nodeSolution);
                priorityQueueSolution.insert(newNodeSolution);
            }

            // Lockstep 2 -- twin (dual) solution
            Iterable<Board> neighborsTwin = nodeTwin.m_board.neighbors();
            
            for (Board board : neighborsTwin) {
                SearchNode previousNodeTwin = nodeTwin.m_previousNode;
                if (previousNodeTwin != null && board.equals(previousNodeTwin.m_board))
                    continue;
                SearchNode newNodeTwin = new SearchNode(board, nodeTwin);
                priorityQueueTwin.insert(newNodeTwin);
            }
        }
    }

    private MinPQ<SearchNode> createInitialSearchNode(Board initializionBoard, int INITIAL_DATA_STRUCT_LENGTH) {
        int penaltyCounter = 0;
        MinPQ<SearchNode> priorityQueue = new MinPQ<SearchNode>(INITIAL_DATA_STRUCT_LENGTH);
        SearchNode initializionNode = new SearchNode(initializionBoard, null);
        priorityQueue.insert(initializionNode);
        return priorityQueue;
    }

    private java.util.LinkedList<Board> computeSolutionList(SearchNode solutionNode) {
        java.util.LinkedList<Board> solutionList = new java.util.LinkedList<Board>();

        solutionList.add(solutionNode.m_board);
        SearchNode currentNode = solutionNode;

        while (currentNode.m_previousNode != null) {
            currentNode = currentNode.m_previousNode;
            solutionList.add(currentNode.m_board);
        }

        java.util.Collections.reverse(solutionList);
        return solutionList;
    }

    // test client (see below) 
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
    
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}