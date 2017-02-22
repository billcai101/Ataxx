package trip;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your trip package per se (that is, it must be
 * possible to remove them and still have your package work). */
/** I tried but I have no clue how to make JUnit tests for trip */

import org.junit.Test;
import ucb.junit.textui;

/** Unit tests for the trip package. */
public class TripUnitTest {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(trip.TripUnitTest.class));
    }


    @Test
    public void test1() {
        Trip trip = new Trip();

    }

}
