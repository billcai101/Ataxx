package make;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "UnitTest" in their name. These
 * may not be part of your make package per se (that is, it must be
 * possible to remove them and still have your package work). */

import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/** Unit tests for the make package. */
public class MyUnitTestMake {

    /** Run all JUnit tests in the make package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(make.MyUnitTestMake.class));
    }

    /** Test unfinished. */
    @Test
    public void test1() {
        Maker maker = new Maker();
        Rule rule = new Rule(maker, "hi");
        assertEquals(true, rule.isUnfinished());

    }

    /** Test for rebuilding a rule. */
    @Test
    public void test2() {
        Maker maker = new Maker();
        Rule rule = new Rule(maker, "hi");
        List<String> commands = new ArrayList<>();
        commands.add("hello");
        rule.addCommands(commands);
        rule.rebuild();
        assertEquals(false, rule.isUnfinished());

    }
}
