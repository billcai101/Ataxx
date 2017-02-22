package ataxx;
import java.util.ArrayList;
/** A Player that computes its own moves.
 *  @author Bill Cai
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        if (!board().canMove(myColor())) {
            System.out.println(myColor().toString() + " passes.");
            return Move.pass();
        }
        Move move = notIdealMove(board());
        System.out.println(myColor().toString()
                + " moves " + move.toString() + ".");
        return move;
    }

    /**Checks to see how good the move is on the board.
     * @param board
     * board
     * @param move
     * move
     * @return  int*/
    private int heuristic(Move move, Board board) {
        if (move.isHigh()) {
            return INFTY;
        } else if (move.isLow()) {
            return -INFTY;
        } else {
            board.makeMove(move);
            if (board.gameOver()) {
                board.undo();
                return WINNING_VALUE;
            }
            int value = staticScore(board);
            board.undo();
            return value;
        }

    }
    /** Return a heuristic value for BOARD.
     * @param board
     * board
     * @return  int*/
    private int staticScore(Board board) {
        return board.numPieces(myColor());
    }

    /** Creates a list of all possible moves for my AI at a given board state.
     * @param board
     * board
     * @return ArrayList*/
    public ArrayList<Move> allMoves(Board board) {
        ArrayList<Move> allpossiblemoves = new ArrayList<>();
        for (char c0 = 'a'; c0 < 'h'; c0 += 1) {
            for (char r0 = '1'; r0 < '8'; r0 += 1) {
                if (board.get(c0, r0).equals(board.whoseMove())) {
                    for (int rc = -2; rc < 3; rc += 1) {
                        for (int cc = -2; cc < 3; cc += 1) {
                            char c1 = (char) (c0 + cc);
                            char r1 = (char) (r0 + rc);
                            if (c1 >= 'a' && c1 <= 'g'
                                    && r1 >= '1' && r1 <= '7') {
                                Move move;
                                move = Move.move(c0, r0, c1, r1);
                                if (move != null) {
                                    if (board.legalMove(move)) {
                                        allpossiblemoves.add(move);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        if (allpossiblemoves.size() == 0) {
            allpossiblemoves.add(Move.pass());
        }
        return allpossiblemoves;
    }
    /** Simple Algorithm used to find a move.
     * @param board
     * board
     * @return int*/
    public Move notIdealMove(Board board) {
        Move result = Move.low();
        int value = -INFTY;
        for (Move move: allMoves(board)) {
            int val = heuristic(move, board);
            if (val > value) {
                result = move;
                value = val;
            }
        }
        return result;
    }
}
