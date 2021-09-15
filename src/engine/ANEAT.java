package engine;

import activations.ActivationType;
import encoding.Genome;

/**
 * Main class of ANEAT, contains facilities for loading NEAT configurations and running NEAT experiments
 *
 * @author Acemad
 */
public class ANEAT {

    // The configuration instance containing all NEAT parameters
    private final NEATConfig config;

    // The principal object in which evolution happens
    private final Population population;

    /**
     * ANEAT Constructor, takes a path to a NEATConfig parameter file and create the necessary instances for running
     * experiments
     * @param configFile The path to a NEATConfig parameter file
     */
    public ANEAT(String configFile) {

        config = new NEATConfig(configFile);
        population = new Population(config);

        // System.out.println("population = \n" + population.toConciseString());
        // System.out.println("configurations = \n" + config);
    }

    /**
     * Using the provided fitness function, starts a NEAT evolution process for the given number of generations
     *
     * @param fitnessFunction The function used for evaluating the quality of solutions
     * @param generations The number of generations to take
     */
    public void run(EvaluationFunction fitnessFunction, int generations) {
        for (int gen = 1; gen <= generations; gen++) {
            population.evolve(fitnessFunction, config);
            // Prints the status of the evolution
            printStatus(gen, generations);
        }
    }

    /**
     * Prints ths status of the evolution to the standard output
     *
     * @param generation The current generation
     * @param maxGenerations The maximum number of generations
     */
    private void printStatus(int generation, int maxGenerations) {
        String status = "Gen: " + String.format("%-4d", generation) +
                " | Top Fitness: " + population.getTopFitness() +
                " | Species Num: " + population.getSpeciesCount() +
                " | Population Num: " + population.getPopulationSize() +
                " | Hidden Nodes: " + population.getInnovations().getHiddenNodeIds().size();
        System.out.print(status + (generation == maxGenerations ? "\n" : "\r"));
    }

    /**
     * Returns the best genome in the current population
     *
     * @return The best genome in the population
     */
    public Genome getBestGenome() {
        return population.getBestGenome();
    }

}
