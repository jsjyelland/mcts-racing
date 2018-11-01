package solution;

import problem.Action;
import problem.ProblemSpec;
import simulator.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        while (node != null) {
            node.addVisit(playoutResult);
            node = node.getParentNode();
        }
    }

    /*
     * Returns the approximately optimal action from the root node.
     */
    private Action bestActionFromFinishedTree() {
        // A map of action texts to one such action object (to avoid having to
        // go from text->Action manually)
        HashMap<String, Action> textActions = new HashMap<>();
        // A map of action texts to their resulting nodes
        HashMap<String, ArrayList<Node>> textNodes = new HashMap<>();
        for (Action action: validActionsDiscretized) {
            for (Node node: root.getChildNodes()) {
                // Action.text is unique: equal text <=> equal actions
                if (node.getParentAction().getText().equals(action.getText())) {
                    textNodes.putIfAbsent(action.getText(), new ArrayList<>());
                    textNodes.get(action.getText()).add(node);
                    textActions.put(action.getText(), action);
                }
            }
        }
        // A map of mean success (results/visits) to their action texts
        HashMap<Double, String> meanTexts = new HashMap<>();
        // highest seen mean
        double maxMean = 0;
        for (Map.Entry<String, ArrayList<Node>> entry : textNodes
                .entrySet()) {
            String text = entry.getKey();
            int resultSum = 0;
            int visitSum = 0;
            for (Node node: entry.getValue()) {
                resultSum += node.getWins();
                visitSum += node.getVisits();
            }
            double mean = (double) resultSum / (double) visitSum;
            meanTexts.put(mean, text);
            if (mean > maxMean) {
                maxMean = mean;
            }
        }

        // Get the action text with the highest seen mean and convert it back
        // to an Action, then return.
        return textActions.get(meanTexts.get(maxMean));
    }

    /*
     * Creates the list of valid actions (discretized) from the problem spec.
     */
    private void makeValidActionsDiscretized() {
        // TODO
        validActionsDiscretized = new ArrayList<>();
    }
}
