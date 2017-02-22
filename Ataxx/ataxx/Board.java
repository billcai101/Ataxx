package ataxx;

/* Author: P. N. Hilfinger, (C) 2008. */

import java.util.Observable;
import java.util.Stack;
import java.util.Arrays;
import java.util.Formatter;
import java.util.ArrayList;
import static ataxx.PieceColor.*;
import static ataxx.GameException.error;

/** An Ataxx board.   The squares are labeled by column (a char value between
 *  'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 *  + 2) or by linearized index, an integer described below.  Values of
 *  the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 *  two layers of border squares, which are always blocked.
 *  This artificial border (which is never actually printed) is a common
 *  trick that allows one to avoid testing for edge conditions.
 *  For example, to look at all the possible moves from a square, sq,
 *  on the normal board (i.e., not in the border region), one can simply
 *  look at all squares within two rows and columns of sq without worrying
 *  about going off the board. Since squares in the border region are
 *  blocked, the normal logic that prevents moving to a blocked square
 *  will apply.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Bill Cai
 */
class Board extends Observable {

    /** Number of squares on a side of the board. */
    static final int SIDE = 7;
    /** Length of a side + an artificial 2-deep border region. */
    static final int EXTENDED_SIDE = SIDE + 4;

    /** Number of non-extending moves before game ends. */
    static final int JUMP_LIMIT = 25;

    /** A new, cleared board at the start of the game. */
    Board() {
        _board = new PieceColor[EXTENDED_SIDE * EXTENDED_SIDE];
        clear();
        colorStack = new Stack<>();
        locationStack = new Stack<>();
        _whoseMove = RED;
    }

    /** A copy of B. */
    Board(Board b) {
        _board = b._board.clone();
        _whoseMove = b.whoseMove();
        colorStack = (Stack<PieceColor>) b.colorStack().clone();
        locationStack = (Stack<Integer>) b.locationStack().clone();
        jumpStack = (Stack<Integer>) b.jumpStack().clone();
    }

    /** Return the linearized index of square COL ROW. */
    static int index(char col, char row) {
        return (row - '1' + 2) * EXTENDED_SIDE + (col - 'a' + 2);
    }

    /** Return the linearized index of the square that is DC columns and DR
     *  rows away from the square with index SQ. */
    static int neighbor(int sq, int dc, int dr) {
        return sq + dc + dr * EXTENDED_SIDE;
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions and no blocks. */
    void clear() {
        _whoseMove = RED;
        for (int i = 0; i < _board.length; i += 1) {
            unrecordedSet(i, EMPTY);
        }
        for (int j = 0; j < 11; j += 1) {
            unrecordedSet(j, BLOCKED);
            unrecordedSet(j + EXTENDED_SIDE, BLOCKED);
        }
        for (int k = 0; k < EXTENDED_SIDE * 10 + 1; k += 11) {
            unrecordedSet(k, BLOCKED);
            unrecordedSet(k + 1, BLOCKED);
        }
        for (int l = EXTENDED_SIDE - 2;
             l < EXTENDED_SIDE * EXTENDED_SIDE - 1; l += 11) {
            unrecordedSet(l, BLOCKED);
            unrecordedSet(l + 1, BLOCKED);
        }
        for (int m = 9 * EXTENDED_SIDE; m < 10 * EXTENDED_SIDE; m += 1) {
            unrecordedSet(m, BLOCKED);
            unrecordedSet(m + EXTENDED_SIDE, BLOCKED);
        }
        unrecordedSet(2 * EXTENDED_SIDE + 2, BLUE);
        unrecordedSet(9 * EXTENDED_SIDE - 3, BLUE);
        unrecordedSet(3 * EXTENDED_SIDE - 3, RED);
        unrecordedSet(9 * EXTENDED_SIDE - 9, RED);
        numJumps = 0;
        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if neither side has
     *  any moves, if one side has no pieces, or if there have been
     *  MAX_JUMPS consecutive jumps without intervening extends. */
    boolean gameOver() {
        if (numPieces(BLUE) == 0 || numPieces(RED) == 0) {
            return true;
        }
        if (numJumps() >= JUMP_LIMIT) {
            return true;
        }
        if (!canMove(BLUE) && !canMove(RED)) {
            return true;
        }
        return false;

    }

    /** Return number of red pieces on the board. */
    int redPieces() {
        return numPieces(RED);
    }

    /** Return number of blue pieces on the board. */
    int bluePieces() {
        return numPieces(BLUE);
    }

    /** Return number of COLOR pieces on the board. */
    int numPieces(PieceColor color) {
        int result = 0;
        for (int i = 0; i < _board.length; i += 1) {
            if (get(i).equals(color)) {
                result += 1;
            }
        }
        return result;
    }

    /** The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     *  '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     *  BLOCKED.  Returns the same value as get(index(C, R)). */
    PieceColor get(char c, char r) {
        return _board[index(c, r)];
    }

    /** Return the current contents of square with linearized index SQ. */
    PieceColor get(int sq) {
        return _board[sq];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'g', and
     *  '1' <= R <= '7'. */
    private void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /** Set square with linearized index SQ to V.  This operation is
     *  undoable. */
    public void set(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /** Set square at C R to V (not undoable). */
    private void unrecordedSet(char c, char r, PieceColor v) {
        _board[index(c, r)] = v;
    }

    /** Set square at linearized index SQ to V (not undoable). */
    private void unrecordedSet(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /** Return true iff MOVE is legal on the current board. */
    boolean legalMove(Move move) {
        if (move == null) {
            return false;
        } else if (move.isPass() && !canMove(_whoseMove)) {
            return true;
        } else if (move.isExtend() || move.isJump()) {
            if (!get(move.col0(), move.row0()).equals(_whoseMove)) {
                return false;
            }
            if (!get(move.col1(), move.row1()).equals(EMPTY)) {
                return false;
            }
            return true;
        }

        return false;
    }


    /** Return true iff player WHO can move, ignoring whether it is
     *  that player's move and whether the game is over. */
    boolean canMove(PieceColor who) {
        ArrayList<Integer> pieces = new ArrayList<>();
        for (int i = 2 * EXTENDED_SIDE + 2; i < 9 * EXTENDED_SIDE - 2; i += 1) {
            if (who == (get(i))) {
                pieces.add(i);
            }
        }
        for (int location: pieces) {
            if (piececanmove(location)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Return number of non-pass moves made in the current game since the
     *  last extend move added a piece to the board (or since the
     *  start of the game). Used to detect end-of-game. */
    int numJumps() {
        return numJumps;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(c0, r0, c1, r1));
        }
    }

    /** Make the MOVE on this Board, assuming it is legal. */
    void makeMove(Move move) {
        if (move.isPass() && canMove(_whoseMove)) {
            System.out.println("Illegal Move.");
            return;
        } else {
            if (move.isPass()) {
                numJumps += 0;
                startUndo();
                startUndo();
            } else if (move.isExtend()) {
                startUndo();
                addUndo(index(move.col1(), move.row1()), _whoseMove);
                unrecordedSet(move.col1(), move.row1(), _whoseMove);
                int location = index(move.col1(), move.row1());
                changecolor(location, _whoseMove);
                numJumps = 0;
            } else if (move.isJump()) {
                startUndo();
                locationStack.push(index(move.col0(), move.row0()));
                colorStack.push(get(index(move.col0(), move.row0())));
                addUndo(index(move.col1(), move.row1()), _whoseMove);
                unrecordedSet(move.col1(), move.row1(), _whoseMove);
                unrecordedSet(move.col0(), move.row0(), EMPTY);
                int location = index(move.col1(), move.row1());
                changecolor(location, _whoseMove);
                numJumps += 1;
            }
            PieceColor opponent = _whoseMove.opposite();
            _whoseMove = opponent;
            setChanged();
            notifyObservers();
        }
    }
    /** Update to indicate that the current player passes, assuming it
     *  is legal to do so.  The only effect is to change whoseMove(). */
    void pass() {
        assert !canMove(_whoseMove);
        setChanged();
        notifyObservers();
    }

    /** Undo the last move. */
    void undo() {
        while (colorStack().peek() != null) {
            set(locationStack().peek(), colorStack().peek());
            locationStack().pop();
            colorStack().pop();
        }
        locationStack().pop();
        colorStack().pop();
        _whoseMove = _whoseMove.opposite();
        setChanged();
        notifyObservers();
    }

    /** Indicate beginning of a move in the undo stack. */
    private void startUndo() {
        colorStack.push(null);
        locationStack.push(null);
    }

    /** Add an undo action for changing SQ to NEWCOLOR on current
     *  board. */
    private void addUndo(int sq, PieceColor newColor) {
        jumpStack.push(numJumps());
        for (int i = -1; i < 2; i += 1) {
            for (int j = -1; j < 2; j += 1) {
                int location = sq + i * EXTENDED_SIDE + j;
                colorStack.push(get(location));
                locationStack.push(location);
            }
        }


    }

    /** Return true iff it is legal to place a block at C R. */
    boolean legalBlock(char c, char r) {
        if (c == 'a' && r == '1') {
            return false;
        }
        if (c == 'a' && r == '7') {
            return false;
        }
        if (c == 'g' && r == '1') {
            return false;
        }
        if (c == 'g' && r == '7') {
            return false;
        }
        return true;
    }

    /** Return true iff it is legal to place a block at CR. */
    boolean legalBlock(String cr) {
        return legalBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Set a block on the square C R and its reflections across the middle
     *  row and/or column, if that square is unoccupied and not
     *  in one of the corners. Has no effect if any of the squares is
     *  already occupied by a block.  It is an error to place a block on a
     *  piece. */
    void setBlock(char c, char r) {
        if (!legalBlock(c, r)) {
            throw error("illegal block placement");
        }
        int magic = EXTENDED_SIDE * EXTENDED_SIDE - 1;
        int placement = index(c, r);
        int mirror1 = magic - placement;
        int placement2 =  placement - 1 + EXTENDED_SIDE - 2 * (placement % 11);
        int mirror2 = magic - placement2;
        unrecordedSet(placement, BLOCKED);
        unrecordedSet(mirror1, BLOCKED);
        unrecordedSet(placement2, BLOCKED);
        unrecordedSet(mirror2, BLOCKED);
        setChanged();
        notifyObservers();
    }

    /** Place a block at CR. */
    void setBlock(String cr) {
        setBlock(cr.charAt(0), cr.charAt(1));
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /* .equals used only for testing purposes. */
    @Override
    public boolean equals(Object obj) {
        Board other = (Board) obj;
        return Arrays.equals(_board, other._board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_board);
    }

    /** Return a text depiction of the board (not a dump).  If LEGEND,
     *  supply row and column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        return out.toString();
    }

    /** For reasons of efficiency in copying the board,
     *  we use a 1D array to represent it, using the usual access
     *  algorithm: row r, column c => index(r, c).
     *
     *  Next, instead of using a 7x7 board, we use an 11x11 board in
     *  which the outer two rows and columns are blocks, and
     *  row 2, column 2 actually represents row 0, column 0
     *  of the real board.  As a result of this trick, there is no
     *  need to special-case being near the edge: we don't move
     *  off the edge because it looks blocked.
     *
     *  Using characters as indices, it follows that if 'a' <= c <= 'g'
     *  and '1' <= r <= '7', then row c, column r of the board corresponds
     *  to board[(c -'a' + 2) + 11 (r - '1' + 2) ], or by a little
     *  re-grouping of terms, board[c + 11 * r + SQUARE_CORRECTION]. */
    private final PieceColor[] _board;

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Num of Jumps made (self). */
    private static int numJumps = 0;


    /**@return @param location Returns if piece can move. */
    private boolean piececanmove(int location) {
        for (int dc = -2; dc < 3; dc += 1) {
            for (int dr = -2; dr < 3; dr += 1) {
                if (get(neighbor(location, dc, dr)).equals(EMPTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Prints out the board. */
    public void dump() {
        System.out.print("===");
        for (int i = 7; i > 0; i -= 1) {
            System.out.println();
            System.out.print("  ");
            for (int j = (i + 1) * EXTENDED_SIDE + 2;
                 j < (i + 1) * 11 + 9; j += 1) {
                if (get(j).equals(RED)) {
                    System.out.print("r ");
                }
                if (get(j).equals(BLUE)) {
                    System.out.print("b ");
                }
                if (get(j).equals(EMPTY)) {
                    System.out.print("- ");
                }
                if (get(j).equals(BLOCKED)) {
                    System.out.print("X ");
                }
            }
        }
        System.out.println();
        System.out.println("===");
    }

    /** @param color  Changes the color of the 9 surrounding
     *  squares if appropriate
     *  @param square              */
    public void changecolor(int square, PieceColor color) {
        for (int i = -1; i < 2; i += 1) {
            for (int j = -1; j < 2; j += 1) {
                if (get(neighbor(square, i, j)).equals(color.opposite())) {
                    unrecordedSet(neighbor(square, i, j), color);
                }
            }
        }
    }
    /** Returns colorStack. */
    public Stack<PieceColor> colorStack() {
        return colorStack;
    }
    /** Returns locationStack. */
    public Stack<Integer> locationStack() {
        return locationStack;
    }
    /** Returns jumpStack. */
    public Stack<Integer> jumpStack() {
        return jumpStack;
    }
    /** A stack of Piece Colors. */
    private static Stack<PieceColor> colorStack = new Stack<>();
    /** A stack of Piece Locations. */
    private static Stack<Integer> locationStack = new Stack<>();
    /** A stack tracking jumps made. */
    private static Stack<Integer> jumpStack = new Stack<>();
}
