package solution;

import java.util.ArrayList;

import problem.Action;
import simulator.State;

public class Node {
    private Node parentNode;
    private Action parentAction;

    private ArrayList<Node> childNodes;

    private State state;

    private int stepsFromRoot;

    // Number of times this node has been visited in the MCTS search
    private int visits;
    // Number of times a simulation has resulted in a win from this node.
    private double reward;

    public Node(State state, int stepsFromRoot) {
        this.state = state;
        this.stepsFromRoot = stepsFromRoot;
        childNodes = new ArrayList<>();
        parentNode = null;
        visits = 0;
        reward = 0;
    }

    public void setParentNodeAndAction(Node parentNode, Action parentAction) {
        this.parentNode = parentNode;
        this.parentAction = parentAction;
    }

    public void addChildNode(Node childNode) {
        childNodes.add(childNode);
    }

    /**
     * Called when backpropagating. Result is either 0 or 1 (corresponding to
     * loss / win from this node).
     *
     * @param result the result of a random playout from this node, 0 or 1.
     */
    public void addVisit(double result) {
        visits += 1;
        reward += result;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public Action getParentAction() {
        return parentAction;
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public State getState() {
        return state;
    }

    public int getVisits() {
        return visits;
    }

    public double getReward() {
        return reward;
    }

    public int getStepsFromRoot() {
        return stepsFromRoot;
    }

    /**
     * Get the child node with a specified state and action.
     *
     * @param state the state
     * @param action the action
     *
     * @return the child node if the state and action match, or null if none
     * exists
     */
    public Node childWithStateAction(State state, Action action) {
        for (Node child : childNodes) {
            if (statesEqual(state, child.getState()) && actionsEqual(child.getParentAction(), action)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Get the number of times an action has been visited (simulated) from this
     * node
     *
     * @param action the action
     *
     * @return the number of visits
     */
    public int getActionVisits(Action action) {
        int visitSum = 0;

        for (Node child : childNodes) {
            if (actionsEqual(action, child.getParentAction())) {
                visitSum += child.getVisits();
            }
        }

        return visitSum;
    }

    public double getActionReward(Action action) {
        double rewardSum = 0;

        for (Node child : childNodes) {
            if (actionsEqual(action, child.getParentAction())) {
                rewardSum += child.getReward();
            }
        }

        return rewardSum;
    }

    private boolean statesEqual(State state1, State state2) {
        return state1.toString().equals(state2.toString()) &&
                (state1.isInBreakdownCondition() == state2.isInBreakdownCondition()) &&
                (state1.isInSlipCondition() == state2.isInSlipCondition());
    }

    private boolean actionsEqual(Action action1, Action action2) {
        return action1.getText().equals(action2.getText());
    }
}