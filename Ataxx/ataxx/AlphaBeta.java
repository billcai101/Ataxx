package ataxx;
import static java.lang.Math.min;
import java.util.ArrayList;

/**
 * @author billcai
 * Created by billcai on 11/11/16.
 * This was my implementation for Alpha Beta pruning game tree search.
 * The problem it ran into was that if during its search the game would end
 * then it would just end the game.
 * Also for this implementation I followed Hilfingers Model
 * (lecture 22) but instead of using integers I returned a move at each step.
 */
class AlphaBeta extends Player {
    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AlphaBeta(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        if (!board().canMove(myColor())) {
            System.out.println(myColor().toString() + " passes.");
            return Move.pass();
        } else {
            Move move = findMax(game().board(), MAX_DEPTH - 2, -INFTY, INFTY);
            System.out.println(myColor().toString()
                    + " moves " + move.toString() + ".");
            return move;
        }
    }

    /** @param board
     * board
     * @param alpha
     * alpha
     * @param beta
     * beta
     *@return
     * move
     */
    private Move simpleFindMin2(Board board, int alpha, int beta) {
        if (board.gameOver()) {
            if (board.numPieces(myColor())
                    > board.numPieces(myColor().opposite())) {
                return Move.high();
            } else {

                return Move.low();
            }
        }
        Move bestsofar = Move.high();
        for (Move move: allMoves(board)) {
            int value = heuristic(move, board);
            int value2 = heuristic(bestsofar, board);
            if (value <= value2) {
                bestsofar = move;
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestsofar;
    }

    /** @param board
     * board
     * @param alpha
     * alpha
     * @param beta
     * beta
     *@return
     * move
     */
    private Move simpleFindMax2(Board board, int alpha, int beta) {
        if (board.gameOver()) {
            if (board.numPieces(myColor())
                    > board.numPieces(myColor().opposite())) {
                return Move.high();
            } else {

                return Move.low();
            }
        }
        Move bestsofar = Move.low();
        for (Move move: allMoves(board)) {
            int value = heuristic(move, board);
            int value2 = heuristic(bestsofar, board);
            if (value >= value2) {
                bestsofar = move;
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestsofar;
    }

    /** @param board
     * board
     *  @param depth
     *  depth
     *  @param beta
     *  beta
     *  @param alpha
     *  alpha
     *  @return Max Move
     *  */
    private Move findMax(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMax2(board, alpha, beta);
        }
        Move bestsofar = Move.low();
        int value = -INFTY;
        ArrayList<Move> moves = allMoves(board);
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Board temp = new Board(board);
            int value2 = heuristic(move, temp);
            temp.makeMove(move);
            Move response = findMin(temp, depth - 1, alpha, beta);
            int value3 = heuristic(response, temp);
            if (value3 >= value) {
                bestsofar = move;
                value = value2;
                alpha = Math.max(alpha, value3);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestsofar;
    }
    /** @param board
     * board
     *  @param depth
     *  depth
     *  @param beta
     *  beta
     *  @param alpha
     *  alpha
     *  @return Min Move
     *  */
    private Move findMin(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.gameOver()) {
            return simpleFindMin2(board, alpha, beta);
        }
        Move bestsofar = Move.high();
        int value = INFTY;
        ArrayList<Move> moves = allMoves(board);
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Board temp = new Board(board);
            int value2 = heuristic(move, temp);
            temp.makeMove(move);
            Move response = findMax(temp, depth - 1, alpha, beta);
            int value3 = heuristic(response, temp);
            if (value3 <= value) {
                bestsofar = move;
                value = value2;
                beta = min(beta, value3);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestsofar;
    }

    /** @param move
     * move
     * @return Heuristic
     * heuristic
     * @param board
     * board   */
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

    /** @param board  Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        return board.numPieces(myColor());
    }

    /** @param board  Returns a list of all moves. */
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
}
