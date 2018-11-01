package solution;

import java.util.ArrayList;
import problem.Action;
import simulator.State;

public class Node {
    private Node parentNode;
    private Action parentAction;

    private ArrayList<Node> childNodes;

    private State state;

    // Number of times this node has been visited in the MCTS search
    private int visits;
    // Number of times a simulation has resulted in a win from this node.
    private int wins;

    public Node(State state) {
        this.state = state;
        childNodes = new ArrayList<>();
        parentNode = null;
        visits = 0;
        wins = 0;
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

    public void setParentNodeAndAction(Node parentNode, Action parentAction) {
        this.parentNode = parentNode;
        this.parentAction = parentAction;
    }

    public void addChildNode(Node childNode) {
        childNodes.add(childNode);
    }

    /**
     * Called when backpropagating. Result is either 0 or 1
     * (corresponding to loss / win from this node).
     * @param result the result of a random playout from this node, 0 or 1.
     */
    public void addVisit(int result) {
        visits += 1;
        wins += result;
    }
}
