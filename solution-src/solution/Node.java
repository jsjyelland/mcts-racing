package solution;

import java.util.ArrayList;

import problem.Action;
import simulator.State;

/**
 * A node in the MCTS tree
 */
public class Node {
    /**
     * The parent node
     */
    private Node parentNode;

    /**
     * The action labelling the vertex connected to the parent
     */
    private Action parentAction;

    /**
     * The node's children
     */
    private ArrayList<Node> childNodes;

    /**
     * The state represented by the node
     */
    private State state;

    /**
     * Depth of the node in the tree
     */
    private int stepsFromRoot;

    /**
     * Number of times this node has been visited in the MCTS search
     */
    private int visits;

    /**
     * Number of times a simulation has resulted in a win from this node.
     */
    private double reward;

    /**
     * Create a node with a state and a tree depth
     *
     * @param state the state
     * @param stepsFromRoot the tree depth
     */
    public Node(State state, int stepsFromRoot) {
        this.state = state;
        this.stepsFromRoot = stepsFromRoot;

        childNodes = new ArrayList<>();
        parentNode = null;
        visits = 0;
        reward = 0;
    }

    /**
     * Set the parent node and label the vertex with an action
     *
     * @param parentNode the parent node
     * @param parentAction the action connecting this node to its parent
     */
    public void setParentNodeAndAction(Node parentNode, Action parentAction) {
        this.parentNode = parentNode;
        this.parentAction = parentAction;
    }

    /**
     * Add a child to this node
     *
     * @param childNode the child to add
     */
    public void addChildNode(Node childNode) {
        childNodes.add(childNode);
    }

    /**
     * Called when backpropagating. Result is either 0 or 1 (corresponding to loss / win from this
     * node).
     *
     * @param result the result of a random playout from this node, 0 or 1.
     */
    public void addVisit(double result) {
        visits += 1;
        reward += result;
    }

    /**
     * Get the parent node
     *
     * @return the parent node
     */
    public Node getParentNode() {
        return parentNode;
    }

    /**
     * Get the action labelling the vertex to the parent
     *
     * @return the action labelling the vertex to the parent
     */
    public Action getParentAction() {
        return parentAction;
    }

    /**
     * Get the child nodes
     *
     * @return the child nodes
     */
    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    /**
     * Get the state represented by the node
     *
     * @return the state represented by the node
     */
    public State getState() {
        return state;
    }

    /**
     * Get the number of times this node has been visited in the tree search
     *
     * @return the number of visits
     */
    public int getVisits() {
        return visits;
    }

    /**
     * Get the reward of this node
     *
     * @return the reward
     */
    public double getReward() {
        return reward;
    }

    /**
     * Get the tree depth
     *
     * @return the tree depth
     */
    public int getStepsFromRoot() {
        return stepsFromRoot;
    }

    /**
     * Get the child node with a specified state and action.
     *
     * @param state the state
     * @param action the action
     *
     * @return the child node if the state and action match, or null if none exists
     */
    public Node childWithStateAction(State state, Action action) {
        for (Node child : childNodes) {
            if (statesEqual(state, child.getState()) && actionsEqual(child.getParentAction(),
                    action)) {
                return child;
            }
        }

        // No child exists
        return null;
    }

    /**
     * Get the number of times an action has been visited (simulated) from this node
     *
     * @param action the action
     *
     * @return the number of visits
     */
    public int getActionVisits(Action action) {
        int visitSum = 0;

        // Sum up the number of visits for all the children that are connected by action
        for (Node child : childNodes) {
            if (actionsEqual(action, child.getParentAction())) {
                visitSum += child.getVisits();
            }
        }

        return visitSum;
    }

    /**
     * Get the total reward for an action
     *
     * @param action the action the child nodes are connected by
     *
     * @return the total reward
     */
    public double getActionReward(Action action) {
        double rewardSum = 0;

        // Sum up the reward for all the children that are connected by action
        for (Node child : childNodes) {
            if (actionsEqual(action, child.getParentAction())) {
                rewardSum += child.getReward();
            }
        }

        return rewardSum;
    }

    /**
     * Determine if two states are equal
     *
     * @param state1 the first state
     * @param state2 the second state
     *
     * @return whether state1 is equal to state2
     */
    private boolean statesEqual(State state1, State state2) {
        return state1.toString().equals(state2.toString()) &&
                (state1.isInBreakdownCondition() == state2.isInBreakdownCondition()) &&
                (state1.isInSlipCondition() == state2.isInSlipCondition());
    }

    /**
     * Determine if two actions are equal
     *
     * @param action1 the first action
     * @param action2 the second action
     *
     * @return whether action1 is equal to action2
     */
    private boolean actionsEqual(Action action1, Action action2) {
        return action1.getText().equals(action2.getText());
    }
}