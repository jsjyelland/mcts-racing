package solution;

import problem.Action;
import problem.ProblemSpec;
import simulator.State;

public class MCTS {

    private ProblemSpec problemSpec;
    private Node root;
    private int timeLimit;

    public MCTS(ProblemSpec problemSpec, State startState, int timeLimit) {
        this.problemSpec = problemSpec;
        this.root = new Node(startState);
        this.timeLimit = timeLimit;
    }

    public Action getBestAction() {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < startTime + timeLimit) {
            Node newNode = selectAndExpandNewNode();
            int randomPlayout = simulateRandomPlayout(newNode);
            backPropagate(newNode, randomPlayout);
        }

        return bestActionFromTree();
    }

    private Node selectAndExpandNewNode() {
        // TODO
    }

    private int simulateRandomPlayout(Node node) {
        // TODO
    }

    private void backPropagate(Node node, int playoutResult) {
        // TODO
    }

    private Action bestActionFromTree() {
        // TODO
    }
}
