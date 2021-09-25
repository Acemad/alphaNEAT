package engine;

import activations.ActivationType;
import encoding.Genome;
import engine.stats.EvolutionStats;
import engine.stats.ReproductionStats;
import innovation.InnovationDB;
import util.ObjectSaver;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Population class, represents the main population of Genomes and their associated Species
 * @author Acemad
 */
public class Population implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // The population of Genomes
    private List<Genome>  population = new ArrayList<>();

    // Species in the population
    private final List<Species> allSpecies = new ArrayList<>();

    // The associated innovations database
    private final InnovationDB innovationDB;

    // Population staleness value (or, how many generations' fitness did not improve)
    private int staleness = 0;

    // Maximum fitness observed
    private Double maxFitnessSoFar = null;

    private double globalAdjustedFitnessTotal;
    private double globalAdjustedFitnessAverage;

    // The best genome of the current generation
    private Genome bestGenome;

    // Used to store the age of the population, in terms of how many evolution steps were performed.
    // Useful for serialization
    private int age = 0;

    // Evolution statistics record keeper
    // private final EvolutionStats evolutionStats = new EvolutionStats();

    /**
     * Constructs a population of Genomes and initializes the associated innovations DB, based on the given number of
     * input and output neurons, and the connection probability parameters.
     *
     * @param numInput The number of input neurons in each Genome
     * @param numOutput The number of output neurons in each Genome
     * @param connectionProbability The probability of connecting input to output neurons
     * @param includeBias Whether a bias neuron is included or not
     * @param biasConnectionProbability The probability of connecting bias neuron to output
     * @param defaultActivationType The default activation type used for new nodes
     * @param count The number of Genomes to generate (population size)
     */
    public Population(int numInput, int numOutput, double connectionProbability, boolean includeBias,
                      double biasConnectionProbability, ActivationType defaultActivationType,
                      double weightRangeMin, double weightRangeMax, int count) {

        // Initiate the innovations store. Sets the nodes' ids, and ids trackers
        innovationDB = new InnovationDB(numInput, numOutput, includeBias, defaultActivationType,
                weightRangeMin, weightRangeMax);

        // Generate Genomes
        while (population.size() < count)
            population.add(new Genome(innovationDB, connectionProbability, biasConnectionProbability));

        // Temporary value
        bestGenome = population.get(0);
    }

    /**
     * Constructs a population using the configurations from a NEATConfig instance
     *
     * @param config The NEAT configuration instance containing all parameters
     */
    public Population(NEATConfig config) {
        this(config.numInput(), config.numOutput(), config.connectionProbability(), config.includeBias(),
                config.biasConnectionProbability(), config.defaultActivationType(), config.weightRangeMin(),
                config.weightRangeMax(), config.populationSize());
    }

    /**
     * Perform a single NEAT evolution step, after which a new generation of Genomes replaces the actual population
     *
     * @param evaluationFunction The fitness function for evaluating Genomes
     * @param config The NEAT configuration instance containing all parameters
     */
    public void evolve(EvaluationFunction evaluationFunction, NEATConfig config, EvolutionStats evolutionStats) {

        // 1. Using the given evaluation function, evaluate the fitness of individuals in the population
        evaluatePopulation(evaluationFunction); // System.out.println("Eval done!");
        // 2. Divide Genomes into species, then clean up the species
        speciate(config); // System.out.println("Speciation done!");
        // Update evolution statistics
        evolutionStats.updateEvolutionStats(this);
        // 3. Check for stale species. Heavily penalize the fitness of stale species
        processSpeciesStaleness(config); // System.out.println("Species staleness done!");
        // 4. Compute the adjusted fitness of Genomes
        adjustFitness(); // System.out.println("Adjust fitness done!");
        // 5. Compute the amount of offspring a species should spawn
        computeSpawnAmounts(); // System.out.println("Spawn compute done!");
        // 6. Check for the staleness of the population, keep only the best species if population is stale
        processPopulationStaleness(config); //System.out.println("Population staleness done!");
        // 7. Generate a new generation of offsprings through mating and mutation within the species
        reproduce(config, evolutionStats); //System.out.println("Reproduce done!");
        // increment population age
        age++;
    }

    /**
     * Evaluate all the members of the population using the given evaluation function
     *
     * @param evaluationFunction The evaluation function used for evaluating the fitness of the Genomes
     */
    public void evaluatePopulation(EvaluationFunction evaluationFunction) {
        // Evaluate the population
        evaluationFunction.evaluateAll(population);
        // Sort the population in descending order by their fitness
        population.sort(Collections.reverseOrder());
        // Designate the best genome
        bestGenome = population.get(0);
    }

    /**
     * Speciate the population; put each Genome in the most compatible species, creating species as needed for Genomes
     * with no compatible species. After assigning species, the leader of each species is reset to the one with the
     * highest fitness within each species, and species are sorted by the fitness of their leaders.
     *
     * @param config The configuration instance containing all parameter values
     */
    public void speciate(NEATConfig config) {

        // Adapt compatibility threshold: Tries to keep the number of species fixed to a given value, at all time.
        // Aims to increase diversity
        if (config.aimForSpeciesNumber() && age > 1) {
            if (getSpeciesCount() < config.speciesNumberTarget())
                config.incrementCompatibilityThresholdBy(-0.25); //0.4 looks good, 0.3 is better
            else if (getSpeciesCount() > config.speciesNumberTarget())
                config.incrementCompatibilityThresholdBy(0.25);

            if (config.compatibilityThreshold() < 0.25) config.setCompatibilityThreshold(0.25);
        }

        // Clear all members of the species from the previous generations. The leader of the species is kept in his own
        // field
        for (Species species : allSpecies)
            species.getMembers().clear();

        // Put each genome in the species which he's most compatible with its representative
        for (Genome genome : population) {

            boolean compatibleSpeciesFound = false;

            for (Species species : allSpecies) {

                // A species compatible with this Genome is found, add it
                if (genome.isCompatibleWith(species.getLeader(), config.unmatchedCoeff(), config.weightDiffCoeff(),
                        config.activationDiffCoeff(), config.compatibilityThreshold())) {
                    compatibleSpeciesFound = true;
                    species.addMember(genome);
                    break;
                }
            }

            // No species compatible is found, create a new species using this Genome as a temporary leader
            if (!compatibleSpeciesFound)
                allSpecies.add(new Species(genome, innovationDB));
        }

        // Resets the leader of each species to the best performing genome (with the highest fitness)
        for (Species species : allSpecies)
            species.resetLeader();

        // Sort species by the fitness of their respective leader, highest to lowest.
        allSpecies.sort(Collections.reverseOrder());

        // Remove empty species after speciation, making sure not to remove a species whose leader is the best
        // performing Genome in the population
        allSpecies.removeIf(species -> species.getMembers().isEmpty() && species.getLeader() != bestGenome);
    }

    /**
     * Verify the staleness status of the species. A species is considered stale if it fails to improve its highest
     * fitness for a predefined number of generations (maxSpeciesStaleness)
     *
     * @param config The configuration instance containing all parameter values
     */
    public void processSpeciesStaleness(NEATConfig config) {

        // Check the staleness value of each species and penalize the fitness of the Genomes belonging to a stale
        // species. Leading species should not get penalized, even if stale.
        for (Species species : allSpecies) {

            if (species.checkStaleness(config.maxSpeciesStaleness()) && species.getLeader() != bestGenome) {
                // Stale Species: Penalization of the members, divide fitness by 100
                for (Genome member : species.getMembers())
                    member.setFitness(member.getFitness() * 0.01);
            }
        }
    }

    /**
     * Compute the adjusted fitness of all Genomes and Species, and compute relevant values for the population
     * This is where explicit fitness sharing takes place.
     */
    public void adjustFitness() {

        // Resetting the global total value
        globalAdjustedFitnessTotal = 0;

        // Compute adjusted fitness for all species, and compute the global total by summing up the species' totals
        for (Species species : allSpecies) {
            species.computeAdjustedFitness();
            globalAdjustedFitnessTotal += species.getTotalAdjustedFitness();
        }

        // Compute the global average
        globalAdjustedFitnessAverage = globalAdjustedFitnessTotal / population.size();
    }

    /**
     * Computes the offspring spawn amount for each species, the sum of the computed spawn amounts must be equal to the
     * size of the population. Spawn amounts are calculated using the adjusted fitness on an individual level, then
     * summed up in the Species level.
     */
    public void computeSpawnAmounts() {

        // 1. Compute the raw value of the spawn amount for each individual
        for (Genome genome : population)
            genome.setSpawnAmount(genome.getAdjustedFitness() / globalAdjustedFitnessAverage);

        // 2. Compute species spawn amounts by adding up the individual spawn amounts.
        double remainder = 0;
        int expectedOffspring = 0;
        for (Species species : allSpecies) {

            // Total species' spawn amount
            double spawnAmount = 0;
            for (Genome member : species.getMembers())
                spawnAmount += member.getSpawnAmount();

            // Take the integer part
            int discreteAmount = (int) spawnAmount;
            // Accumulate the decimal parts, increase the discrete amount by 1 if decimal parts surpass 1
            remainder += spawnAmount - discreteAmount;
            if (remainder >= 1) {
                discreteAmount++;
                remainder--;
            }

            species.setSpawnAmount(discreteAmount);
            // update the total of expected offspring
            expectedOffspring += discreteAmount;
        }

        // 3. In case the expected amount of offsprings is lower than the populations' size
        if (expectedOffspring < population.size()) {
            // Find the species expecting the most offspring

            Species bestSpecies = null;
            for (Species species : allSpecies) {
                if (bestSpecies == null || species.getSpawnAmount() > bestSpecies.getSpawnAmount())
                    bestSpecies = species;
            }

            // Add an offspring to the best species, and increase the amount of expected species
            if (bestSpecies != null) {
                bestSpecies.setSpawnAmount(bestSpecies.getSpawnAmount() + 1);
                expectedOffspring++;
            }

            // If still the expected offsprings fall shorter than the size of the population, then set the spawn
            // amounts of all species to zero and allow the best species to reproduce the entire amount of the
            // population size.
            if (expectedOffspring < population.size()) {
                for (Species species : allSpecies)
                    species.setSpawnAmount(0);
                if (bestSpecies != null)
                    bestSpecies.setSpawnAmount(population.size());
            }
        }
    }

    /**
     * Update the population staleness counter and act upon a stale population by only allowing a limited number of
     * species to reproduce. A population is considered stale if it does not improve for a predefined number of
     * generations
     *
     * @param config The configuration instance containing all parameter values
     */
    public void processPopulationStaleness(NEATConfig config) {

        // 1. Check whether the maxFitness has improved. If not, increment staleness.
        if (maxFitnessSoFar == null || bestGenome.getFitness() > maxFitnessSoFar) {
            // Population improved
            maxFitnessSoFar = bestGenome.getFitness();
            staleness = 0;
        } else // stale population
            staleness++;

        // 2. Stale population: allow the two top species only to reproduce, remove the rest.
        if (staleness > config.maxPopulationStaleness()) {

            // Reset the staleness counter
            staleness = 0;

            // Nullify all the species' spawn amounts
            for (Species species : allSpecies)
                species.setSpawnAmount(0);

            // Divide all spawn amounts between the two highest ranking species, or give it all to the only remaining
            // species
            if (allSpecies.size() >= 2) {
                allSpecies.get(0).setSpawnAmount(population.size() / 2);
                allSpecies.get(1).setSpawnAmount(population.size() - (population.size() / 2));
            } else
                allSpecies.get(0).setSpawnAmount(population.size());
        }
    }

    /**
     * Creates a new generation of Genomes by reproducing within each species. The new generation replaces the old one.
     *
     * @param config The configuration instance containing all parameter values
     */
    public void reproduce(NEATConfig config, EvolutionStats evolutionStats) {

        // The new generation of offsprings
        List<Genome> newGeneration = new ArrayList<>();
        // Keeps a record of the frequencies of the applications of reproduction operators, for this generation
        ReproductionStats reproductionStats = new ReproductionStats();

        // For each species generate the required number of offsprings (spawn amount) within the members of the species
        // and add the new members to the new population
        for (Species species : allSpecies) {

            // Select a subset of parents for reproduction. Selection is done by choosing the percent of parents from
            // the top of the ordered list of members
            species.selectParents(config.parentsSurvivalThreshold());
            // System.out.println("\t Parent selection done " + species.toConciseString());
            // for (Genome member : species.getMembers()) System.out.println("\t\t " + member.toConciseString());

            // Generate the offspring and add to the new generation
            newGeneration.addAll(species.spawnOffsprings(innovationDB, config, reproductionStats));
            // System.out.println("\t offspring spawning selection done");
        }

        // Update the evolution statistics with the reproduction stats of this generation
        evolutionStats.updateReproductionStats(reproductionStats);

        // The old population is replaced by the new generation
        population = newGeneration;
    }

    /**
     * Saves the state of this population instance to a given file on disk. If the file does not exist, it will be
     * created. This is useful for resuming the evolution with an older population.
     * @param filePath The path to the file to save population into
     */
    public void saveToFile(String filePath) {
        ObjectSaver.saveObjectToFile(this, filePath);
    }

    /**
     * Reads (deserialize) a population instance from the given file, and returns the full Population object.
     * Useful for loading previously saved populations.
     * @param filePath The population file
     * @return A Population instance deserialized from the file
     */
    public static Population readFromFile(String filePath) {
        return ObjectSaver.loadFromFile(filePath, Population.class);
    }

    @Override
    public String toString() {
        return "Population{ \n" + population + '}';
    }

    /**
     * Generates a concise String representation of this population, along all species and essential information
     *
     * @return A String representation of the population
     */
    public String toConciseString() {
        StringBuilder builder = new StringBuilder("Population: ");
        builder.append(population.size()).append(" Genome ------------------------------------> \n");
        for (Genome genome : population)
            builder.append(genome.toConciseString()).append("\n");

        builder.append(String.format("GlobalAFTotal: %.2f\n", globalAdjustedFitnessTotal))
               .append(String.format("GlobalAFAvg: %.2f\n", globalAdjustedFitnessAverage));

        if (bestGenome != null)
            builder.append("BestGenome: G").append(bestGenome.getId());
            builder.append(String.format(", MaxFitnessSoFar: %.10f\n", maxFitnessSoFar));

        builder.append(String.format("Staleness: %d\n", staleness));

        builder.append("Species: ").append(getSpeciesCount()).append(" ---------------------------------->\n");
        int speciatedGenomes = 0, totalExpectedOffspring = 0;
        for (Species species : allSpecies) {
            builder.append(species.toConciseString()).append("\n");
            speciatedGenomes += species.getSize();
            totalExpectedOffspring += species.getSpawnAmount();
        }
        builder.append("Genomes in species: ").append(speciatedGenomes)
                .append("\nTotal Expected Offspring: ").append(totalExpectedOffspring);

        builder.append("\nBEST Fitness: ").append(bestGenome.getFitness());

        return builder.toString();
    }

    /**
     * Returns the number of actual species in the population
     *
     * @return The number of actual species in the population
     */
    public int getSpeciesCount() {
        return allSpecies.size();
    }

    /**
     * Returns the number of genomes in the population
     *
     * @return The number of genomes in the population
     */
    public int getPopulationSize() {
        return population.size();
    }

    /**
     * Returns the fitness of the actual best Genome
     *
     * @return The fitness value of the best Genome
     */
    public double getTopFitness() {
        return bestGenome.getFitness();
    }

    /**
     * Returns the best Genome from the current population
     *
     * @return The best Genome from the current population
     */
    public Genome getBestGenome() {
        return bestGenome;
    }

    public InnovationDB getInnovations() {
        return innovationDB;
    }

    public int getAge() {
        return age;
    }

    public List<Genome> getPopulationMembers() {
        return population;
    }

    // public EvolutionStats getEvolutionStats() {
    //     return evolutionStats;
    // }

    public List<Species> getSpecies() {
        return allSpecies;
    }
}


