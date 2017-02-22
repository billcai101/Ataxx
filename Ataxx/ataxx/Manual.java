package ataxx;

import static ataxx.PieceColor.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Bill Cai
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Move move;
        Command cmnd = game().getMoveCmnd(myColor().toString());
        if (cmnd.commandType().equals(Command.Type.PIECEMOVE)) {
            char a = cmnd.operands()[0].charAt(0);
            char b = cmnd.operands()[1].charAt(0);
            char c = cmnd.operands()[2].charAt(0);
            char d = cmnd.operands()[3].charAt(0);
            move = Move.move(a, b, c, d);
        } else {
            move = Move.pass();
        }
        return move;

    }
}

