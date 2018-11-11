package solution;

import problem.Action;
import problem.ProblemSpec;
import simulator.Simulator;
import simulator.State;

import java.io.IOException;

public class Main {

    // The length of time the MCTS search is allowed to run at each step.
    static final int STEP_TIME_LIMIT = 15000;
    // Since the MCTS will actually take slightly longer than the time limit,
    // add a small buffer
    private static final int STEP_TIME_BUFFER = 100;

    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];

        int timeLimit = STEP_TIME_LIMIT;
        // Optional third argument, a different step time limit (in millis)
        if (args.length > 2) {
            timeLimit = Integer.parseInt(args[2]);
        }

        ProblemSpec ps = new ProblemSpec(inputFile);

        System.out.println(ps.toString());

        Simulator sim = new Simulator(ps, outputFile);

        boolean solved = simulateProblem(ps, sim, timeLimit);
        if (solved) {
            System.out.println("Simulation successful.");
        } else {
            System.out.println("Simulation failed.");
        }

    }

    public static boolean simulateProblem(ProblemSpec ps, Simulator sim, int timeLimit) {
        State state = sim.reset();
        int stepsDone = 0;

        while (state != null) {
            MCTS mcts = new MCTS(ps, state, stepsDone,
                    timeLimit - STEP_TIME_BUFFER);
            Action action = mcts.getBestAction();
            state = sim.step(action);
            if (sim.isGoalState(state)) {
                return true;
            }
            stepsDone++;
        }
        return false;
    }
}
