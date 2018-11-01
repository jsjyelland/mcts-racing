package solution;

import problem.Action;
import problem.ProblemSpec;
import simulator.State;

import java.util.ArrayList;

public class MCTS {

    private ProblemSpec problemSpec;

    private Node root;

    // The time limit to search for each action.
    private int timeLimit;

    // All possible actions given this problemSpec
    private ArrayList<Action> validActionsDiscretized;

    /**
     * Initialize the MCTS search object with all required information, and
     * create the list of possible actions
     * @param problemSpec The specification of the current problem
     * @param startState The start state of this search
     * @param timeLimit How long is allocated to this search per action. This
     *                  search will actually take just slightly longer than this
     */
    public MCTS(ProblemSpec problemSpec, State startState, int timeLimit) {
        this.problemSpec = problemSpec;
        this.root = new Node(startState);
        this.timeLimit = timeLimit;
        makeValidActionsDiscretized();
    }

    /**
     * Executes the MCTS search. Takes slightly longer than timeLimit. Will
     * return the approximately best Action object to perform.
     * @return the best Action object from the startState.
     */
    public Action getBestAction() {
        long startTime = System.currentTimeMillis();

        // Continue iterating through the search algorithm until the time limit
        // is reached
        while (System.currentTimeMillis() < startTime + timeLimit) {
            Node newNode = selectAndExpandNewNode();
            int randomPlayout = simulateRandomPlayout(newNode);
            backPropagate(newNode, randomPlayout);
        }

        // Technically this function will take us slightly over timeLimit, but
        // that's why a buffer is removed from timeLimit when passed to this
        // class
        return bestActionFromFinishedTree();
    }

    /*
     * Decision policy for exploration, returns new leaf Node.
     */
    private Node selectAndExpandNewNode() {
        // TODO
        return new Node(null);
    }

    /*
     * Simulates a random playout from leaf Node node. Returns 1 if the playout
     * is a win, otherwise 0.
     */
    private int simulateRandomPlayout(Node node) {
        // TODO
        return 0;
    }

    /*
     * Updates the visit and win amounts on all parents nodes from the leaf
     * node Node to the root node.
     */
    private void backPropagate(Node node, int playoutResult) {
        // TODO
    }

    /*
     * Returns the approximately optimal action from the root node.
     */
    private Action bestActionFromFinishedTree() {
        // TODO
        return new Action(null);
    }

    /*
     * Creates the list of valid actions (discretized) from the problem spec.
     */
    private void makeValidActionsDiscretized() {
        // TODO
        validActionsDiscretized = new ArrayList<>();
    }
}
