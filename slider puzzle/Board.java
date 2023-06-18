import edu.princeton.cs.algs4.StdOut;

public class Board {

    private int[][] m_tiles;
    private Pair m_blank;

    private class Pair {
        int first;
        int second;
        Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }
    private enum DIRECTIONS { LEFT, RIGHT, UP, DOWN; }

    /* columns-within-rows translation from 2d to 1d description */
    private int translate_to_1D(int i, int j, int Ncols) { 
        return Ncols * i + j;
    }

    private Pair translate_to_2D(int k, int Ncols) {
        int j = k % Ncols;
        int i = (k - j) / Ncols;
        return new Pair(i,j);
    }

    /* create a board from an n-by-n array of tiles,
       where tiles[row][col] = tile at (row, col)
       beginning at [0][0] in upper left corner */
    public Board(int[][] tiles) {
        int boardSize = tiles.length; // N_rows = N_cols assumed
        m_tiles = new int[boardSize][boardSize];
        m_tiles = tiles;
        externalLoop:
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (m_tiles[i][j] == 0) {
                    m_blank = new Pair(i, j);
                    break externalLoop;
                }
            }
        }
    }

    /* not allowed by autograder */
    private Board(Board board_copy) {
        int boardSize = board_copy.dimension();
        m_tiles = new int[boardSize][boardSize];
        m_blank = new Pair(board_copy.m_blank.first, board_copy.m_blank.second);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++)
                m_tiles[i][j] = board_copy.m_tiles[i][j];
        }
    }
                                           
    /* string representation of this board */
    public String toString() {
        final int boardSize = dimension();
        java.lang.StringBuilder builder = new java.lang.StringBuilder(3*boardSize*boardSize);
        builder.append(Integer.toString(boardSize) + "\n");
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                builder.append(" " + Integer.toString(m_tiles[i][j]));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /* board dimension n */
    public int dimension() {return m_tiles.length;}

    private void print() {
        String text = toString();
        StdOut.println(text);
    }

    /* number of tiles out of place */
    public int hamming() {
        int counter = 1;
        int penalty = 0;
        final int boardSize = dimension();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (m_tiles[i][j] == counter++) continue;
                if (m_tiles[i][j] == 0) continue; // don't penalize free tile
                penalty += 1;
            }
        }
        return penalty;
    }

    private int abs(int x) {
        if (x < 0) return -x;
        else return x;
    }

    /* sum of Manhattan distances between tiles and goal */
    public int manhattan() {
        int penalty = 0;
        final int boardSize = dimension();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // decrement current value because wanted board[0][0] == 1 (not 0)
                int curTileValue = m_tiles[i][j] - 1;
                if (curTileValue == -1) continue;
                
                // 1d value (decremented) at board[i][j] --> 2d value : shows where it SHOULD be
                Pair ij_prime = translate_to_2D(curTileValue, boardSize);
                int difference = abs(i - ij_prime.first) + 
                                 abs(j - ij_prime.second);
                penalty += difference;
            }
        }
        return penalty;
    }

    /* is this board the goal board? */
    public boolean isGoal() {
        return hamming() == 0;
    }

    /* does this board equal boardIn? */
    public boolean equals(Object boardIn) {
        if (boardIn == this)  return true;
        if (boardIn == null)  return false;
        if (boardIn.getClass() != this.getClass()) return false;
        Board thatBoard = (Board) boardIn;
        final int boardSize = dimension();
        if (boardSize != thatBoard.dimension())
            return false;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (m_tiles[i][j] != thatBoard.m_tiles[i][j])
                    return false;
            }
        }
        return true;
    }

    private Board move(DIRECTIONS direction) {
        Board neighbor = new Board(this);
        int boardEdge = dimension() - 1;
        // note: expressions 'up/left/etc.' refers to the TILE, not the BLANK
        int newFirst, newSecond;
        switch (direction) {
            case LEFT:
                if (m_blank.second == boardEdge)
                    return null;
                    // throw new IllegalStateException("Board::move: tried to move left when blank.second = boardEdge");
                newFirst  = m_blank.first;
                newSecond = m_blank.second+1;
                break;
            case RIGHT:
                if (m_blank.second == 0)
                    return null;
                    // throw new IllegalStateException("Board::move: tried to move right when blank.second = 0");
                newFirst  = m_blank.first;
                newSecond = m_blank.second-1;
                break;
            case UP:
                if (m_blank.first == boardEdge)
                    return null;
                    // throw new IllegalStateException("Board::move: tried to move up when blank.first = boardEdge");
                newFirst  = m_blank.first+1;
                newSecond = m_blank.second;
                break;
            case DOWN:
                if (m_blank.first == 0)
                    return null;
                    // throw new IllegalStateException("Board::move: tried to move down when blank.first = 0");
                newFirst  = m_blank.first-1;
                newSecond = m_blank.second;
                break;
            default:
                throw new IllegalArgumentException("Board::move: Enum out of range");
        }
        neighbor.m_tiles[m_blank.first][m_blank.second] = m_tiles[newFirst][newSecond];
        neighbor.m_tiles[newFirst][newSecond] = 0;
        neighbor.m_blank.first = newFirst;
        neighbor.m_blank.second = newSecond;
        return neighbor;
    }

    private boolean isCornerTile() {
        final int boardSize = dimension();
        if ((m_blank.first == 0          && m_blank.second == 0) ||
            (m_blank.first == 0          && m_blank.second == boardSize) ||
            (m_blank.first == boardSize  && m_blank.second == 0) ||
            (m_blank.first == boardSize  && m_blank.second == boardSize) )
             return true;
        else return false;
    }

    /* all neighboring boards */
    public Iterable<Board> neighbors() { // java.util.ArrayList<Board>
        final int boardSize = dimension();
        int neighbors;
        if (isCornerTile()) {
            neighbors = 2;
        }
        else if (m_blank.first  == 0 || m_blank.first  == boardSize ||
                 m_blank.second == 0 || m_blank.second == boardSize) {
            neighbors = 3;
        }
        else {
            neighbors = 4;
        }
        java.util.ArrayList<Board> vect = new java.util.ArrayList<Board>(neighbors);

        for (DIRECTIONS direction : DIRECTIONS.values()) {
            Board newNeigbor = move(direction);
            if (newNeigbor == null) continue;
            vect.add(newNeigbor);
/*             try {
                Board newNeigbor = move(direction);
                vect.add(newNeigbor);
            } catch (Exception e) {
                if (e instanceof IllegalStateException)
                    continue;
                else throw new IllegalStateException("Board::neighbors: something not right");
            } */
        }

        return vect;
    }

    /* a board that is obtained by exchanging any pair of tiles */
    public Board twin() {
        Board twinBoard = new Board(this);
        int swap_2 = 0; // first column (tile to be swapped with secon column in...)
        int swap_1;     // first or second row
        if (m_blank.first == 0)
            swap_1 = 1;
        else
            swap_1 = 0;
        int tileValue = m_tiles[swap_1][swap_2];
        twinBoard.m_tiles[swap_1][swap_2] = m_tiles[swap_1][swap_2+1];
        twinBoard.m_tiles[swap_1][swap_2+1] = tileValue;
        return twinBoard;
    }

    private static Board fillBoard(In input) {
        int n = input.readInt();
        int[][] tab = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                tab[i][j] = input.readInt();
        }
        Board boardOut =  new Board(tab);
        tab = null;
        return boardOut;
    }

     public static void main(String[] args) {
        
        In in  = new In(args[0]);
        In in2 = new In(args[1]);
        In in3 = new In(args[2]);

        Board board1 = fillBoard(in);
        Board board2 = fillBoard(in2);
        Board board3 = fillBoard(in3);

        String text = board1.toString();
        StdOut.println(text);
        int manha = board1.manhattan();
        StdOut.println("manhatan measure: ");
        StdOut.println(manha);
        int hamlin = board1.hamming();
        StdOut.println("Hamming measure: ");
        StdOut.println(hamlin);
        StdOut.println("is Gaol: ");
        StdOut.println(board1.isGoal());

        StdOut.println(board1.equals(board2));
        StdOut.println(board2.equals(board3));
        StdOut.println(board3.equals(board3));

        board3.print();
        // test.twin().print();

        // Board b1p = board2.move(DIRECTIONS.LEFT);
        // Board b2p = b1p.move(DIRECTIONS.UP);
        // Board b3p = b2p.move(DIRECTIONS.RIGHT);
        
        
        // java.util.ArrayList<Board> vect = test.neighbors();
        // for (Board x : vect) {
        //     x.print();
        // }
    }

}