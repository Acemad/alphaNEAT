package examples;

import encoding.Genome;
import encoding.phenotype.NeuralNetwork;
import engine.ANEAT;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * An example use case of ANEAT for the XOR problem.
 * Uses the xorConfigs.cfg file located in the xor/ directory
 * @author Acemad
 */
public class XORExample {

    public static final String fileSeparator = System.getProperty("file.separator");

    public static void main(String[] args) {

        String experiment = "xor"; // Name of the current experiment
        // Location of the configuration file, and future save files
        String parentDir = System.getProperty("user.dir") + fileSeparator + experiment + fileSeparator;
        String configPath = parentDir + "xorConfigs.cfg"; // Path to the configuration file
        String baseName = parentDir + experiment; //Prefix for population, best genome, and statistics files

        int runs = 10; // Number of runs
        int generations = 500; // Number of generations in each run

        for (int i = 0; i < runs; i++) {

            // Start a fresh new run
            ANEAT aneat = new ANEAT(configPath);
            // Normal run, without saving
            aneat.run(XORExample::evalXOR, generations, null);

            /* Resuming from a previous run */
            // int generation = 1000; // Generation to resume from (must have been saved previously)
            // ANEAT aneat = new ANEAT(configPath,
            //                         baseName + "Pop-" + generation,    // Previous population file
            //                         baseName + "Stats"); // Previous stats file (null: create new stats)

            /* Saving evolution files for resuming evolution later */
            // To save the last three generations, last three best genomes, and statistic files, include the base name
            // parameter
            // aneat.run(XORExample::evalXOR, generations, baseName);

            System.out.println("\tBestGenome ->\n\t" + aneat.getBestGenome().toConciseString());
        }
    }

    /**
     * XOR fitness function. Receives a genome and evaluate its aptitude to solve XOR. A Neural Network is derived from
     * the genome and is tested using the four input cases of XOR.
     *
     * This method is compatible with the EvaluationFunction functional interface.
     *
     * @param genome A genome to evaluate
     * @return Fitness value of the genome
     */
    public static double evalXOR(Genome genome) {

        // Derive the neural network (Phenotype)
        NeuralNetwork network = genome.buildNetwork();
        double fitness = 0;

        // Compute fitness of the network using the inputs (0,0) (0,1) (1,0) (1,1)
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                double[] input = {i, j};
                network.activate(input, 1); // Activation is done in 2 passes (possibility of recurrent links)
                double[] output = network.getOutputValue(); // Retrieve output
                int expected = i ^ j;
                fitness += (1 - Math.abs(expected - output[0])); // Compare with expected, accumulate the difference
            }

        // Max fitness = 16
        return (fitness * fitness);
    }

}
