package engine;

import encoding.Genome;
import engine.stats.EvolutionStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private final EvolutionStats evolutionStats;

    /**
     * ANEAT Constructor, takes a path to a NEATConfig parameter file and create the necessary instances for running
     * experiments
     * @param configFile The path to a NEATConfig parameter file
     */
    public ANEAT(String configFile) {

        config = new NEATConfig(configFile);
        population = new Population(config);
        evolutionStats = new EvolutionStats();
    }

    /**
     * ANEAT Resume constructor, takes a configuration file path and a path to a saved population object to continue
     * evolution from
     * @param configFile The path to a NEATConfig parameter file
     * @param populationFile The population file to continue evolution from
     * @param statsFile The evolution stats file to continue updating
     */
    public ANEAT(String configFile, String populationFile, String statsFile) {
        config = new NEATConfig(configFile);
        System.out.println("Loading files ...");
        population = Population.readFromFile(populationFile);
        if (statsFile != null)
            evolutionStats = EvolutionStats.readFromFile(statsFile);
        else
            evolutionStats = new EvolutionStats();
    }

    /**
     * Using the provided fitness function, starts a NEAT evolution process for the given number of generations.
     * This method will also save the population and the best genome to separate files on each generation, if
     * the provided baseFileName is not null
     *
     * @param fitnessFunction The function used for evaluating the quality of solutions
     * @param generations The number of generations to take
     * @param baseFileName Base name of the population and best genomes file to save on each generation
     */
    public void run(EvaluationFunction fitnessFunction, int generations, String baseFileName) {

        for (int gen = 1; gen <= generations; gen++) {
            // Evolve population
            population.evolve(fitnessFunction, config, evolutionStats);
            // Prints the status of the evolution
            printStatus(gen, generations);
            // Save the population:
            savePopulation(baseFileName);
            saveBestGenome(baseFileName);
        }
        saveStats(baseFileName);
        System.gc();
    }

    /**
     * Saves the population using the given base file name. Only saves the three last generations, deletes the rest.
     * @param baseFileName Path to the file
     */
    private void savePopulation(String baseFileName) {

        if (baseFileName != null) {
            population.saveToFile(baseFileName + "Pop-" + population.getAge());
            // Keep only the three most recent generations
            if (population.getAge() > 3) {
                try {
                    Files.deleteIfExists(Path.of(baseFileName + "Pop-" + (population.getAge() - 3)));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the best genome of the population using the given path. Only saves the three last generations.
     * @param baseFileName Path to the file
     */
    private void saveBestGenome(String baseFileName) {
        if (baseFileName != null) {
            population.getBestGenome().saveToFile(baseFileName + "Best-" + population.getAge());
            // Keep only the three most recent generations
            if (population.getAge() > 3) {
                try {
                    Files.deleteIfExists(Path.of(baseFileName + "Best-" + (population.getAge() - 3)));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the evolution stats instance to a file on the disk using the given path. Also saves a CSV short stats
     * version
     * @param baseFileName Basic file path to save to
     */
    private void saveStats(String baseFileName) {

        if (baseFileName != null) {
            saveCSVReport(baseFileName + "Stats.csv", evolutionStats);
            evolutionStats.saveToFile(baseFileName + "Stats");
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
                " | Age: " + String.format("%-4d", population.getAge()) +
                " | Top Fitness: " + population.getTopFitness() +
                " | Species Num: " + population.getSpeciesCount() +
                " | Population Num: " + population.getPopulationSize() +
                " | Hidden Nodes: " + population.getInnovations().getHiddenNodeIds().size();
        System.out.print(status + (generation == maxGenerations ? "\n" : "\r"));
    }

    /**
     * TODO Elaborate a CSV report
     * @param filePath
     */
    public void saveCSVReport(String filePath, EvolutionStats stats) {
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(filePath), CSVFormat.EXCEL)) {
            csvPrinter.printRecord("gen", "max", "min", "mean", "geoMean", "median", "variance", "sd", "sum");
            for (int i = 0; i < stats.getPopulationFitnessStats().size(); i++) {
                csvPrinter.printRecord(i+1,
                        stats.getPopulationFitnessStats().get(i).getMax(),
                        stats.getPopulationFitnessStats().get(i).getMin(),
                        stats.getPopulationFitnessStats().get(i).getMean(),
                        stats.getPopulationFitnessStats().get(i).getGeometricMean(),
                        stats.getPopulationFitnessStats().get(i).getPercentile(0.5),
                        stats.getPopulationFitnessStats().get(i).getPopulationVariance(),
                        stats.getPopulationFitnessStats().get(i).getStandardDeviation(),
                        stats.getPopulationFitnessStats().get(i).getSum());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the best genome in the current population
     *
     * @return The best genome in the population
     */
    public Genome getBestGenome() {
        return population.getBestGenome();
    }

    public EvolutionStats getEvolutionStats() {
        return evolutionStats;
    }
}
