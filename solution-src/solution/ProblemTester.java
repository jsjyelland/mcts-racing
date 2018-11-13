package solution;

import problem.ProblemSpec;
import simulator.Simulator;

import java.io.IOException;

/**
 * Repeatedly test a problem and calculate the statistics
 */
public class ProblemTester extends Main {
    /**
     * Entry point of the program
     *
     * @param args command line arguments. args[0] is the input file, args[1] is the output file,
     * args[2] is the number of tests to run, and optionally args[3] is the time per simulation
     * iteration.
     *
     * @throws IOException if there is an error handling the input or output file
     */
    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];
        int count = Integer.parseInt(args[2]);

        int timeLimit = STEP_TIME_LIMIT;

        // Optional third argument, a different step time limit (in millis)
        if (args.length > 3) {
            timeLimit = Integer.parseInt(args[3]);
        }

        // Load the problem
        ProblemSpec ps = new ProblemSpec(inputFile);
        System.out.println(ps.toString());

        // Create a simulator
        Simulator sim = new Simulator(ps, outputFile);

        int successful = 0;
        int totalStepsRequired = 0;

        // Test count number of times
        for (int i = 1; i < count + 1; i++) {
            // Do a simulation
            boolean solved = simulateProblem(ps, sim, timeLimit);

            if (solved) {
                System.out.println("Simulation successful.");

                successful += 1;
                totalStepsRequired += sim.getSteps();
            } else {
                System.out.println("Simulation failed.");
            }

            if (i == count) {
                System.out.println("Final results:");
            }

            // Print statistics
            System.out.println(successful + " Simulations successful out of " + i);
            System.out.println("Success percentage: " + (successful * 100 / (double) i) + "%");
            System.out.println("Average steps required for successful simulation: " +
                    totalStepsRequired / (double) successful);
        }
    }
}
