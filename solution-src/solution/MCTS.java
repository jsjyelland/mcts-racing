package solution;

import problem.*;
import simulator.State;

import java.util.*;

/**
 * Monte Carlo Tree Search
 */
public class MCTS {
    /**
     * Reward for a winning a simulation
     */
    private static final double WIN_BONUS = 1;

    /**
     * Reward multiplier for winning a simulation faster
     */
    private static final double SPEED_MULTIPLIER = 1;

    /**
     * Reward multiplier for a simulation travelling a number of cells
     */
    private static final double DISTANCE_MULTIPLIER = 1;

    /**
     * The problem specification
     */
    private ProblemSpec problemSpec;

    /**
     * Root node of the tree
     */
    private Node root;

    /**
     * Number of time steps completed
     */
    private int stepsDone;

    /**
     * The time limit to search for each action.
     */
    private int timeLimit;

    /**
     * All possible actions given this problemSpec
     */
    private ArrayList<Action> validActionsDiscretized;

    /**
     * Number of discrete levels of fuel able to be added to the tank
     */
    private static final int FUEL_DISCRETE_INTERVALS = 6;

    /**
     * Initialize the MCTS search object with all required information, and create the list of
     * possible actions
     *
     * @param problemSpec The specification of the current problem
     * @param startState The start state of this search
     * @param stepsDone The amount of steps done so far
     * @param timeLimit How long is allocated to this search per action. This search will actually
     * take just slightly longer than this
     */
    public MCTS(ProblemSpec problemSpec, State startState, int stepsDone, int timeLimit) {
        this.problemSpec = problemSpec;
        this.root = new Node(startState, 0);
        this.stepsDone = stepsDone;
        this.timeLimit = timeLimit;

        // Make a list of all the possible actions
        makeValidActionsDiscretized();
    }

    /**
     * Executes the MCTS search. Takes slightly longer than timeLimit. Will return the approximately
     * best Action object to perform.
     *
     * @return the best Action object from the startState.
     */
    public Action getBestAction() {
        long startTime = System.currentTimeMillis();

        // Continue iterating through the search algorithm until the time limit
        // is reached
        while (System.currentTimeMillis() < startTime + timeLimit) {
            Node newNode = selectAndExpandNewNode();
            double randomPlayout = simulateRandomPlayout(newNode);
            backPropagate(newNode, randomPlayout);
        }

        // Technically this function will take us slightly over timeLimit, but
        // that's why a buffer is removed from timeLimit when passed to this
        // class
        return bestActionFromFinishedTree();
    }

    /**
     * Decision policy for exploration, returns new leaf Node.
     *
     * @return the leaf node added to the tree
     */
    private Node selectAndExpandNewNode() {
        // Start at the root
        Node node = root;

        // Iterate down the tree until reaching a goal state
        while (node.getState().getPos() < problemSpec.getN()) {
            // Get the best action from the current node using UCT
            Action action = selectBestAction(node);

            // Simulate a single action
            FromStateSimulator FSS = new FromStateSimulator(problemSpec);
            FSS.setStartState(node.getState(), stepsDone + node.getStepsFromRoot());
            FSS.step(action);
            State newState = FSS.getCurrentState();

            // Get the node representing the outcome of the transition
            Node child = node.childWithStateAction(newState, action);

            // If this outcome node has not been added to the tree, add it
            if (child == null) {
                Node newNode = new Node(newState, FSS.getSteps());
                newNode.setParentNodeAndAction(node, action);
                node.addChildNode(newNode);

                // Return this new node
                return newNode;
            }

            // Now repeat the process using this child node
            node = child;
        }

        return node;
    }

    /**
     * Select the best action to perform on a node using the UCT (Upper confidence bound for trees)
     * method.
     *
     * @param node the node to select the best action from
     *
     * @return the best Action object
     */
    private Action selectBestAction(Node node) {
        return Collections.max(validActionsDiscretized, Comparator.comparing(c -> UCTValue(c,
                node)));
    }

    /**
     * The UCT value of an action and a parent node
     *
     * @param action the action to calculate the UCT value of
     * @param parentNode the node which has the action as a child vertex
     *
     * @return the UCT value of the action
     */
    private double UCTValue(Action action, Node parentNode) {
        double actionVisits = (double) parentNode.getActionVisits(action);

        return parentNode.getActionReward(action) / actionVisits +
                Math.sqrt(2.0 * Math.log(parentNode.getVisits()) / actionVisits);
    }

    /**
     * Simulates a random playout from leaf Node node. Returns 1 if the playout is a win, otherwise
     * 0.
     *
     * @param node the node to simulate the playout from
     */
    private double simulateRandomPlayout(Node node) {
        State playoutState = node.getState().copyState();
        FromStateSimulator FSS = new FromStateSimulator(problemSpec);
        FSS.setStartState(playoutState, stepsDone);

        int status = FromStateSimulator.IN_PROGRESS;

        // Simulate until a win or loss
        while (status == FromStateSimulator.IN_PROGRESS) {
            Action action = selectRandomAction();
            status = FSS.step(action);
        }

        if (status == FromStateSimulator.WIN) {
            return WIN_BONUS + SPEED_MULTIPLIER * (problemSpec.getMaxT() - FSS.getSteps()) /
                    (double) problemSpec.getMaxT();
        } else {
            // The simulation was a loss
            return DISTANCE_MULTIPLIER * FSS.getCurrentState().getPos() /
                    (double) problemSpec.getN();
        }
    }

    /**
     * Selects a random action for the random playout
     *
     * @return the random action
     */
    private Action selectRandomAction() {
        int fuel;
        String car, driver;
        Tire tire;
        TirePressure pressure;

        // Possible tire pressures
        List<TirePressure> tirePressures = Arrays.asList(
                TirePressure.FIFTY_PERCENT,
                TirePressure.SEVENTY_FIVE_PERCENT,
                TirePressure.ONE_HUNDRED_PERCENT
        );

        // Possible fuel levels (note that this is an arbitrary discretization)
        List<Integer> fuelLevels = new ArrayList<>();
        int fuelInterval = ProblemSpec.FUEL_MAX / FUEL_DISCRETE_INTERVALS;

        for (int i = 0; i < FUEL_DISCRETE_INTERVALS; i++) {
            fuelLevels.add(fuelInterval * i);
        }

        List<ActionType> validActionTypes = problemSpec.getLevel().getAvailableActions();
        ActionType actionType = getRandomElement(validActionTypes);
        Action action;

        // Pick a random action from A1-A8 and then randomize the parameters
        switch (actionType.getActionNo()) {
            case 1:
                action = new Action(actionType);
                break;
            case 2:
                car = getRandomElement(problemSpec.getCarOrder());
                action = new Action(actionType, car);
                break;
            case 3:
                driver = getRandomElement(problemSpec.getDriverOrder());
                action = new Action(actionType, driver);
                break;
            case 4:
                tire = getRandomElement(problemSpec.getTireOrder());
                action = new Action(actionType, tire);
                break;
            case 5:
                fuel = getRandomElement(fuelLevels);
                action = new Action(actionType, fuel);
                break;
            case 6:
                pressure = getRandomElement(tirePressures);
                action = new Action(actionType, pressure);
                break;
            case 7:
                car = getRandomElement(problemSpec.getCarOrder());
                driver = getRandomElement(problemSpec.getDriverOrder());
                action = new Action(actionType, car, driver);
                break;
            default:
                // A8
                tire = getRandomElement(problemSpec.getTireOrder());
                fuel = getRandomElement(fuelLevels);
                pressure = getRandomElement(tirePressures);
                action = new Action(actionType, tire, fuel, pressure);
        }

        return action;

    }

    /**
     * Helper function to get a random element from a list
     *
     * @param list the list
     * @param <T> the type of the list
     *
     * @return the random element
     */
    private <T> T getRandomElement(List<T> list) {
        return list.get(randomInt(0, list.size()));
    }

    /**
     * Updates the visit and win amounts on all parents nodes from the leaf node Node to the root
     * node.
     *
     * @param node the node to begin backpropagation from
     * @param playoutResult the reward of the playout
     */
    private void backPropagate(Node node, double playoutResult) {
        while (node != null) {
            node.addVisit(playoutResult);
            node = node.getParentNode();
        }
    }

    /**
     * Get the approximately optimal action from the root node.
     *
     * @return the best action from the root based on it's win / simulation ratio
     */
    private Action bestActionFromFinishedTree() {
        return Collections.max(validActionsDiscretized, Comparator.comparing(
                c -> root.getActionReward(c) / (double) root.getActionVisits(c)));
    }

    /**
     * Creates the list of valid actions (discretized) from the problem spec.
     */
    private void makeValidActionsDiscretized() {
        validActionsDiscretized = new ArrayList<>();

        List<ActionType> actionTypes = problemSpec.getLevel().getAvailableActions();

        // Valid tire pressures
        List<TirePressure> tirePressures = Arrays.asList(
                TirePressure.FIFTY_PERCENT,
                TirePressure.SEVENTY_FIVE_PERCENT,
                TirePressure.ONE_HUNDRED_PERCENT
        );

        // Valid fuel levels (note that this is an arbitrary discretization)
        List<Integer> fuelLevels = new ArrayList<>();
        int fuelInterval = ProblemSpec.FUEL_MAX / FUEL_DISCRETE_INTERVALS;

        for (int i = 0; i < FUEL_DISCRETE_INTERVALS; i++) {
            fuelLevels.add(fuelInterval * i);
        }

        // Go through A1-A8 and add all possible combinations of the parameters
        for (ActionType actionType : actionTypes) {
            switch (actionType.getActionNo()) {
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
                                validActionsDiscretized.add(new Action(actionType, tire, fuel,
                                        pressure));
                            }
                        }
                    }

                    break;
            }
        }
    }

    /**
     * Helper function for generating a random int from min to max (inclusive min, exclusive max)
     *
     * @param min the lower bound of the random range (inclusive)
     * @param max the upper bound of the random range (exclusive)
     *
     * @return the random number
     */
    private static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt(max - min) + min;
    }
}
