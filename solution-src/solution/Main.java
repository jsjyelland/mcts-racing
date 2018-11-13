package solution;

import problem.Action;
import problem.ProblemSpec;
import simulator.Simulator;
import simulator.State;

import java.io.IOException;

/**
 * Program to run an MCTS planner
 */
public class Main {
    /**
     * The length of time the MCTS search is allowed to run at each step.
     */
    static final int STEP_TIME_LIMIT = 15000;

    /**
     * Since the MCTS will actually take slightly longer than the time limit, add a small buffer
     */
    private static final int STEP_TIME_BUFFER = 100;

    /**
     * Main function, takes an input and output file and solves the problem using an MCTS planner
     *
     * @param args the command line arguments. args[0] is the input file, args[1] is the output
     * file, and args[2] is optionally a different time step limit
     *
     * @throws IOException if there is an exception generated handling the files
     */
    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];

        int timeLimit = STEP_TIME_LIMIT;

        // Optional third argument, a different step time limit (in millis)
        if (args.length > 2) {
            timeLimit = Integer.parseInt(args[2]);
        }

        // Load the problem
        ProblemSpec ps = new ProblemSpec(inputFile);
        System.out.println(ps.toString());

        // Solve the problem
        Simulator sim = new Simulator(ps, outputFile);
        boolean solved = simulateProblem(ps, sim, timeLimit);

        if (solved) {
            System.out.println("Simulation successful.");
        } else {
            System.out.println("Simulation failed.");
        }

    }

    /**
     * Simulate and solve the problem using the MCTS planner
     *
     * @param ps the problem information
     * @param sim the simulator
     * @param timeLimit the time limit per step of the planner
     *
     * @return whether the simulation was successful or not
     */
    public static boolean simulateProblem(ProblemSpec ps, Simulator sim, int timeLimit) {
        State state = sim.reset();
        int stepsDone = 0;

        // Simulate the problem until the problem is won or lost
        while (state != null) {
            // Make an MCTS object to calculate the best action to perform at this point
            MCTS mcts = new MCTS(ps, state, stepsDone, timeLimit - STEP_TIME_BUFFER);
            Action action = mcts.getBestAction();

            // Perform the action
            state = sim.step(action);

            if (sim.isGoalState(state)) {
                // Won the simulation
                return true;
            }

            stepsDone++;
        }

        return false;
    }
}
