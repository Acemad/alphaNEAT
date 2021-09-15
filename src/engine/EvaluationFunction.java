package engine;

import encoding.Genome;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A functional interface used to define the evaluation (fitness) function to optimize, and provide a way to execute
 * the evaluation concurrently
 *
 * @author Acemad
 */
public interface EvaluationFunction {

    /**
     * Takes a Genome and evaluate its fitness according to the requirements of the problem domain, as implemented by
     * a subclass
     * @param genome The Genome to evaluate
     * @return The value of the Genome
     */
    double evaluate(Genome genome);

    /**
     * Evaluates a population of Genomes concurrently across multiple threads using an ExecutorService
     * @param genomes The List of Genome to evaluate
     * @return true if the process
     */
    default boolean evaluateAll(List<Genome> genomes) {

        // Get the number of processors available
        int cores = Runtime.getRuntime().availableProcessors();

        // For each Genome in the list of genomes, create a Runnable task for evaluation and add to the list of tasks
        List<Runnable> tasks = new ArrayList<>();
        for (Genome genome : genomes)
            tasks.add(() -> genome.setFitness(evaluate(genome))); // Directly set the fitness of the Genome

        // Create a fixed thread pool and execute the tasks.
        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        tasks.forEach(executorService::execute);
        // Initiate a proper shutdown, executing all previously submitted tasks and preventing submission of new tasks
        executorService.shutdown();

        // Wait for the termination of all tasks
        try {
            if (!executorService.awaitTermination(10, TimeUnit.HOURS)) {
                executorService.shutdownNow();
                return false;
            } else
                return true; // All tasks terminated

        } catch (Exception exception) {
            executorService.shutdownNow();
            return false;
        }
    }
}
