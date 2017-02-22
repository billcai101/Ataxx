package ataxx;

import org.junit.Test;

import static ataxx.Command.Type.ERROR;
import static ataxx.Command.Type.PIECEMOVE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static ataxx.Move.*;
/**
 * Created by billcai on 11/10/16.
 */
public class UnitTests {
    private static final String[]
            GAME2 = {"a7-a5", "a1-a2", "a5-a4"};

    private static final String[]
            GAMEOVER = {"a7-a5", "a1-a3", "a5-a4",
                        "g7-e6", "g1-g3", "e6-g5", "g3-g4"};

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(s.charAt(0), s.charAt(1),
                    s.charAt(3), s.charAt(4));
        }
    }

    /**Testing Extend */
    @Test
    public void testUndo2() {
        Board b0 = new Board();
        makeMoves(b0, GAME2);
        Board b2 = new Board(b0);
        b0.makeMove('a', '2', 'a', '3');
        b0.undo();
        assertEquals("Undo faulty for extends", b0, b2);
    }

    /**Testing Undo */
    @Test public void testUndo3() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        b0.makeMove('a', '7', 'b', '5');
        b0.undo();
        assertEquals("Undo faulty for jumps", b0, b1);
    }

    /**Tests for Commands*/

    void check(String cmnd, Command.Type type, String... operands) {
        Command c = Command.parseCommand(cmnd);
        assertEquals("Wrong type of command identified", type,
                c.commandType());
        if (operands.length == 0) {
            assertEquals("Command has wrong number of operands", 0,
                    c.operands() == null ? 0 : c.operands().length);
        } else {
            assertArrayEquals("Operands extracted incorrectly",
                    operands, c.operands());
        }
    }

    void checkError(String cmnd) {
        check(cmnd, ERROR);
    }
    @Test
    public void testMOVE() {
        check("a1-b2", PIECEMOVE, "a", "1", "b", "2");
        checkError("a1 - b2");
        checkError("a1 b2");
    }
    /** Testing clear() function */
    @Test
    public void testClear() {
        Board b0 = new Board();
        b0.makeMove('a', '7', 'a', '6');
        b0.clear();
        assertEquals(b0.redPieces(), 2);
    }

    /** Testing gameOver()*/
    @Test
    public void testBoard2() {
        Board b0 = new Board();
        makeMoves(b0, GAMEOVER);
        assertEquals(b0.gameOver(), true);
    }

    /** Tests pass works */
    @Test
    public void testPass() {
        Board b0 = new Board();
        b0.set(79, PieceColor.BLUE);
        b0.set(68, PieceColor.BLUE);
        b0.set(69, PieceColor.BLUE);
        b0.set(70, PieceColor.BLUE);
        b0.set(80, PieceColor.BLUE);
        b0.set(81, PieceColor.BLUE);
        b0.set(91, PieceColor.BLUE);
        b0.set(92, PieceColor.BLUE);
        b0.set(30, PieceColor.BLUE);
        assertEquals(b0.legalMove(Move.pass()), true);
    }

    @Test
    public void testDraw() {
        Board b0 = new Board();
        for (int i = 0; i < 60; i += 1) {
            b0.set(i, PieceColor.BLUE);
        }
        for (int j = 60; j < 120; j += 1) {
            b0.set(j, PieceColor.RED);
        }
        assertEquals(b0.bluePieces(), b0.redPieces());
    }

    /** Tests to see if whoseMove is switching */

    @Test
    public void whoseMove() {
        Board b0 = new Board();
        b0.makeMove(Move.pass());
        b0.makeMove('a', '7', 'a', '6');
        assertEquals(b0.whoseMove(), PieceColor.BLUE);
    }

    @Test
    public void testcanMove() {
        Board b0 = new Board();
        b0.set(79, PieceColor.BLUE);
        b0.set(68, PieceColor.BLUE);
        b0.set(69, PieceColor.BLUE);
        b0.set(70, PieceColor.BLUE);
        b0.set(80, PieceColor.BLUE);
        b0.set(81, PieceColor.BLUE);
        b0.set(91, PieceColor.BLUE);
        b0.set(92, PieceColor.BLUE);
        b0.set(30, PieceColor.BLUE);
        assertEquals(b0.canMove(PieceColor.BLUE), true);
        assertEquals(b0.canMove(PieceColor.RED), false);
    }

    @Test
    public void testsetBlock() {
        Board b0 = new Board();
        b0.setBlock('b', '2');
        assertEquals(b0.get(36), PieceColor.BLOCKED);
        assertEquals(b0.get(80), PieceColor.BLOCKED);
        assertEquals(b0.get(84), PieceColor.BLOCKED);
        assertEquals(b0.get(40), PieceColor.BLOCKED);
    }
}
