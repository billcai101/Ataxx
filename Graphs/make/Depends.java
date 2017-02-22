package make;

import graph.LabeledGraph;

/** A directed, labeled subtype of Graph that describes dependencies between
 *  targets in a Makefile.
 *  @author Bill Cai
 *  My Tests for Make are in MyUnitTestMake.java
 */
class Depends extends LabeledGraph<Rule, Void> {
    /** An empty dependency graph. */
    Depends() {
        super(new graph.DirectedGraph());
    }

}
