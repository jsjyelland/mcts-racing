package solution;

import problem.ProblemSpec;
import simulator.Simulator;

import java.io.IOException;

// Simple class to repeatedly test a problem
public class ProblemTester extends Main {
    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];
        int count = Integer.parseInt(args[2]);

        int timeLimit = STEP_TIME_LIMIT;
        // Optional third argument, a different step time limit (in millis)
        if (args.length > 3) {
            timeLimit = Integer.parseInt(args[3]);
        }

        ProblemSpec ps = new ProblemSpec(inputFile);

        System.out.println(ps.toString());

        Simulator sim = new Simulator(ps, outputFile);

        int successful = 0;
        int totalStepsRequired = 0;

        for (int i = 1; i < count + 1; i++) {
            boolean solved = simulateProblem(ps, sim, timeLimit);
            if (solved) {
                System.out.println("Simulation successful.");
            } else {
                System.out.println("Simulation failed.");
            }
            if (solved) {
                successful += 1;
                totalStepsRequired += sim.getSteps();
            }

            if (i == count) {
                System.out.println("Final results:");
            }
            System.out.println(successful + " Simulations successful out of " + i);
            System.out.println("Success percentage: " + (successful * 100 / (double) i) + "%");
            System.out.println("Average steps required for successful simulation: " + totalStepsRequired / (double) successful);
        }
    }
}
