package solution;

import java.util.ArrayList;
import problem.Action;
import simulator.State;

public class Node {
    private Node parentNode;
    private Action parentAction;

    private ArrayList<Node> childNodes;

    private State state;

    private Node(State state) {
        this.state = state;
        childNodes = new ArrayList<>();
        parentNode = null;
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
}
