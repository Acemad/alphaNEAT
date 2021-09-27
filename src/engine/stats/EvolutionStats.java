package engine.stats;

import encoding.Genome;
import engine.Population;
import engine.Species;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import util.ObjectSaver;

import java.io.FileWriter;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * A statistics class that records and accumulates all interesting data throughout the evolution.
 * @author Acemad
 */
public class EvolutionStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Population Fitness Statistics (min/max/mean... fitness observed in genomes in each generation, ...)
    private final List<DescriptiveStatistics> genomesFitnessStats = new ArrayList<>();

    // Population hidden nodes' statistics (min/max/mean... nodes number observed in genomes in each generation,...)
    private final List<DescriptiveStatistics> genomesNodesStats = new ArrayList<>();

    // Population link' statistics (min/max/mean...links observed in genomes in each generation,...)
    private final List<DescriptiveStatistics> genomesLinksStats = new ArrayList<>();

    // Number of unique genomes observed so far
    private final Set<Integer> genomeIds = new HashSet<>();

    // The number of nodes/links emerged so far in each generation. This is supposed to cumulate across the generations
    private final DescriptiveStatistics nodeCumulateCountStats = new DescriptiveStatistics();
    private final DescriptiveStatistics linkCumulateCountStats = new DescriptiveStatistics();

    // Species stats: The species count in each generation
    private final DescriptiveStatistics speciesCountStats = new DescriptiveStatistics();
    // Species stats: The cumulated species count across the generations
    private final DescriptiveStatistics speciesCumulateCountStats = new DescriptiveStatistics();
    // Species stats: species fitness stats, contains the fitness data of each species' members for each generation
    // where it is present (the list contains one stat element for each generation)
    private final Map<Integer, List<DescriptiveStatistics>> speciesFitnessStats = new HashMap<>();
    // Species stats: species nodes/links stats, containing topological statistics of the genomes of each species
    private final Map<Integer, List<DescriptiveStatistics>> speciesNodesStats = new HashMap<>();
    private final Map<Integer, List<DescriptiveStatistics>> speciesLinksStats = new HashMap<>();
    // Species stats: species mean complexity stats
    private final Map<Integer, DescriptiveStatistics> speciesMeanComplexityStats = new HashMap<>();
    // Species stats: for each species, keep a list of the population ages this species existed in
    private final Map<Integer, List<Integer>> speciesExistenceStats = new HashMap<>();

    // Best genome topology stats: Node/link counts of the best genome in each generation
    private final DescriptiveStatistics bestGenomeNodesStats = new DescriptiveStatistics();
    private final DescriptiveStatistics bestGenomeLinksStats = new DescriptiveStatistics();

    // Reproduction stats: The stats of each reproduction operation types happening in each generation
    private final DescriptiveStatistics mutationOnlyReproductionsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics mutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics matingOnlyReproductionsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics matingsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics matingPlusMutationReproductionsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics totalReproductionsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics addNodeMutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics addLinkMutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics weightMutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics toggleEnableMutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics reEnableMutationsStats = new DescriptiveStatistics();
    private final DescriptiveStatistics activationMutationsStats = new DescriptiveStatistics();

    private final DescriptiveStatistics meanComplexityStats = new DescriptiveStatistics();

    /**
     * This method is called after the speciation step of each generation with the actual state of the population. It
     * updates the population fitness statistics, population topology statistics, the best genome topology statistics,
     * and the species statistics. Statistics are updated while keeping all the data of previous generations.
     *
     * @param population The current population instance to compute statistics for.
     */
    public void updateEvolutionStats(Population population) {

        updatePopulationFitnessStats(population);
        updatePopulationTopologyStats(population);
        updateBestGenomeTopologyStats(population);
        updateSpeciesStats(population);

        meanComplexityStats.addValue(population.meanComplexity());
    }

    /**
     * Updates the population fitness statistics. These consist of the min, max, mean, geoMean, median fitness for each
     * generation of genomes, as well as the variance, standard deviation, skewness, kurtosis, and sum.
     *
     * @param population The current population instance to compute statistics for
     */
    private void updatePopulationFitnessStats(Population population) {

        DescriptiveStatistics currentFitnessStats = new DescriptiveStatistics();

        for (Genome genome : population.getPopulationMembers()) {
            currentFitnessStats.addValue(genome.getFitness());
            genomeIds.add(genome.getId());
        }

        genomesFitnessStats.add(currentFitnessStats);
    }

    /**
     * Updates the population topology stats. These are related to the number of hidden nodes and links in the population.
     * Stats computed comprise the min, max, mean, median,...etc., of hidden nodes and links found in each genome of
     * each generation.
     * Also computes the cumulative number of distinct hidden nodes/links in the population in each generation
     *
     * @param population The current population instance to compute statistics for
     */
    private void updatePopulationTopologyStats(Population population) {

        DescriptiveStatistics currentNodesStats = new DescriptiveStatistics();
        DescriptiveStatistics currentLinksStats = new DescriptiveStatistics();

        for (Genome genome : population.getPopulationMembers()) {
            currentNodesStats.addValue(genome.getHiddenNodeGenes().size());
            currentLinksStats.addValue(genome.getLinkGenes().size());
        }

        genomesNodesStats.add(currentNodesStats);
        genomesLinksStats.add(currentLinksStats);

        // Number of distinct nodes/links emerging in each generation (cumulating)
        nodeCumulateCountStats.addValue(population.getInnovations().getHiddenNodeIds().size());
        linkCumulateCountStats.addValue(population.getInnovations().getLinkCount());
    }

    /**
     * Updates the best genome topology statistics. These consist of the number of hidden nodes, and links of the best
     * genome of each generation.
     *
     * @param population The current population instance to compute statistics for
     */
    private void updateBestGenomeTopologyStats(Population population) {
        // Best genome topology stats
        bestGenomeNodesStats.addValue(population.getBestGenome().getHiddenNodeGenes().size());
        bestGenomeLinksStats.addValue(population.getBestGenome().getLinkGenes().size());
    }

    /**
     * Update species statistics for each generation. These include the fitness, nodes, and links statistics
     * (min, max, mean,...etc.) of each species in each generation it appears in, and the population ages where
     * this species existed.
     *
     * @param population The current population instance to compute statistics for
     */
    private void updateSpeciesStats(Population population) {

        // Update species fitness/node/link/existence stats.
        for (Species species : population.getSpecies()) {

            // Compute the fitness, nodes, and links stats for this species using all members
            DescriptiveStatistics fitnessStats = new DescriptiveStatistics();
            DescriptiveStatistics nodesStats = new DescriptiveStatistics();
            DescriptiveStatistics linkStats = new DescriptiveStatistics();

            for (Genome member : species.getMembers()) {
                fitnessStats.addValue(member.getFitness());
                nodesStats.addValue(member.getHiddenNodeGenes().size());
                linkStats.addValue(member.getLinkGenes().size());
            }

            // Check if this species stats were previously computed (in a previous generation)
            if (!speciesFitnessStats.containsKey(species.getId())) {
                // New species, create the fitness stats, nodes/links stats and existence lists,
                // add them to the global map
                speciesFitnessStats.put(species.getId(), new ArrayList<>(List.of(fitnessStats)));
                speciesNodesStats.put(species.getId(), new ArrayList<>(List.of(nodesStats)));
                speciesLinksStats.put(species.getId(), new ArrayList<>(List.of(linkStats)));
                speciesExistenceStats.put(species.getId(), new ArrayList<>(List.of(population.getAge())));

                DescriptiveStatistics speciesMeanComplexity = new DescriptiveStatistics();
                speciesMeanComplexity.addValue(species.meanComplexity());
                speciesMeanComplexityStats.put(species.getId(), speciesMeanComplexity);

            } else {
                // Previously visited species, retrieve the stats lists and add new data
                speciesFitnessStats.get(species.getId()).add(fitnessStats);
                speciesNodesStats.get(species.getId()).add(nodesStats);
                speciesLinksStats.get(species.getId()).add(linkStats);
                speciesExistenceStats.get(species.getId()).add(population.getAge());

                speciesMeanComplexityStats.get(species.getId()).addValue(species.meanComplexity());
            }
        }

        // Update species count, and cumulate count stats
        speciesCountStats.addValue(population.getSpeciesCount());
        speciesCumulateCountStats.addValue(population.getInnovations().getSpeciesCount());
    }

    /**
     * Updates (merge in) the reproduction stats. These include the number of time a reproduction operator was applied
     * in the current generation, these numbers are present in the given ReproductionStats instance.
     *
     * @param reproductionStats The reproduction stats of the current generations
     */
    public void updateReproductionStats(ReproductionStats reproductionStats) {

        mutationOnlyReproductionsStats.addValue(reproductionStats.mutationOnlyReproductions().get());
        mutationsStats.addValue(reproductionStats.mutations().get());
        matingOnlyReproductionsStats.addValue(reproductionStats.matingOnlyReproductions().get());
        matingsStats.addValue(reproductionStats.matings().get());
        matingPlusMutationReproductionsStats.addValue(reproductionStats.matingPlusMutationReproductions().get());
        totalReproductionsStats.addValue(reproductionStats.totalReproductions().get());
        addNodeMutationsStats.addValue(reproductionStats.addNodeMutations().get());
        addLinkMutationsStats.addValue(reproductionStats.addLinkMutations().get());
        weightMutationsStats.addValue(reproductionStats.weightMutations().get());
        toggleEnableMutationsStats.addValue(reproductionStats.toggleEnableMutations().get());
        reEnableMutationsStats.addValue(reproductionStats.reEnableMutations().get());
        activationMutationsStats.addValue(reproductionStats.activationMutations().get());
    }

    /**
     * Save this instance as a serialized object
     * @param filePath Path of the file to save to
     */
    public void saveToFile(String filePath) {
        ObjectSaver.saveObjectToFile(this, filePath);
    }

    /**
     * Read and deserialize the object file and return it as an instance of this class.
     * @param filePath The file to read
     * @return An EvolutionStats instance
     */
    public static EvolutionStats readFromFile(String filePath) {
        return ObjectSaver.loadFromFile(filePath, EvolutionStats.class);
    }

    /**
     * TODO Elaborate a CSV report
     * @param filePath
     */
    public void saveAsCSV(String filePath) {
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(filePath), CSVFormat.EXCEL)) {
            csvPrinter.printRecord("gen", "max", "min", "mean", "geoMean", "median", "variance", "sd", "sum");
            for (int i = 0; i < genomesFitnessStats.size(); i++) {
                csvPrinter.printRecord(i+1,
                        genomesFitnessStats.get(i).getMax(),
                        genomesFitnessStats.get(i).getMin(),
                        genomesFitnessStats.get(i).getMean(),
                        genomesFitnessStats.get(i).getGeometricMean(),
                        genomesFitnessStats.get(i).getPercentile(0.5),
                        genomesFitnessStats.get(i).getPopulationVariance(),
                        genomesFitnessStats.get(i).getStandardDeviation(),
                        genomesFitnessStats.get(i).getSum());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /********************** Getters ***********************/

    public Map<Integer, List<DescriptiveStatistics>> getSpeciesFitnessStats() {
        return speciesFitnessStats;
    }

    public Map<Integer, List<Integer>> getSpeciesExistenceStats() {
        return speciesExistenceStats;
    }

    public DescriptiveStatistics getSpeciesCountStats() {
        return speciesCountStats;
    }

    public DescriptiveStatistics getNodeCumulateCountStats() {
        return nodeCumulateCountStats;
    }

    public DescriptiveStatistics getLinkCumulateCountStats() {
        return linkCumulateCountStats;
    }

    public DescriptiveStatistics getBestGenomeNodesStats() {
        return bestGenomeNodesStats;
    }

    public DescriptiveStatistics getBestGenomeLinksStats() {
        return bestGenomeLinksStats;
    }

    public DescriptiveStatistics getMutationOnlyReproductionsStats() {
        return mutationOnlyReproductionsStats;
    }

    public DescriptiveStatistics getMutationsStats() {
        return mutationsStats;
    }

    public DescriptiveStatistics getMatingOnlyReproductionsStats() {
        return matingOnlyReproductionsStats;
    }

    public DescriptiveStatistics getMatingsStats() {
        return matingsStats;
    }

    public DescriptiveStatistics getMatingPlusMutationReproductionsStats() {
        return matingPlusMutationReproductionsStats;
    }

    public DescriptiveStatistics getTotalReproductionsStats() {
        return totalReproductionsStats;
    }

    public DescriptiveStatistics getAddNodeMutationsStats() {
        return addNodeMutationsStats;
    }

    public DescriptiveStatistics getAddLinkMutationsStats() {
        return addLinkMutationsStats;
    }

    public DescriptiveStatistics getWeightMutationsStats() {
        return weightMutationsStats;
    }

    public DescriptiveStatistics getToggleEnableMutationsStats() {
        return toggleEnableMutationsStats;
    }

    public DescriptiveStatistics getReEnableMutationsStats() {
        return reEnableMutationsStats;
    }

    public DescriptiveStatistics getActivationMutationsStats() {
        return activationMutationsStats;
    }

    public DescriptiveStatistics getSpeciesCumulateCountStats() {
        return speciesCumulateCountStats;
    }

    public Map<Integer, List<DescriptiveStatistics>> getSpeciesNodesStats() {
        return speciesNodesStats;
    }

    public Map<Integer, List<DescriptiveStatistics>> getSpeciesLinksStats() {
        return speciesLinksStats;
    }

    public List<DescriptiveStatistics> getGenomesFitnessStats() {
        return genomesFitnessStats;
    }

    public Set<Integer> getGenomeIds() {
        return genomeIds;
    }

    public List<DescriptiveStatistics> getGenomesLinksStats() {
        return genomesLinksStats;
    }

    public List<DescriptiveStatistics> getGenomesNodesStats() {
        return genomesNodesStats;
    }

    public DescriptiveStatistics getMeanComplexityStats() {
        return meanComplexityStats;
    }

    public Map<Integer, DescriptiveStatistics> getSpeciesMeanComplexityStats() {
        return speciesMeanComplexityStats;
    }
}
