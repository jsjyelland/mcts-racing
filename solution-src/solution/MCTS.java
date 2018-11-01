package solution;

import problem.*;
import simulator.Simulator;
import simulator.State;

import java.util.*;

public class MCTS {
    private ProblemSpec problemSpec;

    private Node root;

    // The time limit to search for each action.
    private int timeLimit;

    // All possible actions given this problemSpec
    private ArrayList<Action> validActionsDiscretized;

    private static final int FUEL_DISCRETE_INTERVALS = 6;

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
        Node node = root;
        while (node.getState().getPos() < problemSpec.getN()) {
            Action action = selectBestAction(node);
            State newState = simulateNextState(node.getState(), action);
            Node child = node.childWithStateAction(newState, action);

            if (child == null) {
                Node newNode = new Node(newState);
                newNode.setParentNodeAndAction(node, action);
                node.addChildNode(newNode);

                return newNode;
            }

            node = child;
        }

        return new Node(null);
    }

    private State simulateNextState(State currentState, Action action) {
        return null;
    }

    /*
     * Select the best action to perform on a node using the UCT (Upper
     * confidence bound for trees) method.
     */
    private Action selectBestAction(Node node) {
        // TODO
        return null;
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
        validActionsDiscretized = new ArrayList<>();

        List<ActionType> actionTypes = problemSpec.getLevel().getAvailableActions();

        List<TirePressure> tirePressures = Arrays.asList(
                TirePressure.FIFTY_PERCENT,
                TirePressure.SEVENTY_FIVE_PERCENT,
                TirePressure.ONE_HUNDRED_PERCENT
        );

        List<Integer> fuelLevels = new ArrayList<>();
        int fuelInterval = ProblemSpec.FUEL_MAX / FUEL_DISCRETE_INTERVALS;

        for (int i = 0; i < FUEL_DISCRETE_INTERVALS; i++) {
            fuelLevels.add(fuelInterval * i);
        }

        for (ActionType actionType : actionTypes) {
            switch(actionType.getActionNo()) {
                case 1:
                    validActionsDiscretized.add(new Action(actionType));
                    break;

                case 2:
                    for (String car : problemSpec.getCarOrder()) {
                        validActionsDiscretized.add(new Action(actionType, car));
                    }

                    break;

                case 3:
                    for (String driver : problemSpec.getDriverOrder()) {
                        validActionsDiscretized.add(new Action(actionType, driver));
                    }

                    break;

                case 4:
                    for (Tire tire : problemSpec.getTireOrder()) {
                        validActionsDiscretized.add(new Action(actionType, tire));
                    }

                    break;

                case 5:
                    for (int fuel : fuelLevels) {
                        validActionsDiscretized.add(new Action(actionType, fuel));
                    }

                    break;

                case 6:
                    for (TirePressure pressure : tirePressures) {
                        validActionsDiscretized.add(new Action(actionType, pressure));
                    }

                    break;
                case 7:
                    for (String car : problemSpec.getCarOrder()) {
                        for (String driver : problemSpec.getDriverOrder()) {
                            validActionsDiscretized.add(new Action(actionType, car, driver));
                        }
                    }

                    break;

                case 8:
                    for (TirePressure pressure : tirePressures) {
                        for (int fuel : fuelLevels) {
                            for (Tire tire : problemSpec.getTireOrder()) {
                                validActionsDiscretized.add(new Action(actionType, tire, fuel, pressure));
                            }
                        }
                    }

                    break;
            }
        }
    }
}
