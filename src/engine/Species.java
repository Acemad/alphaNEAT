package engine;

import encoding.Genome;
import engine.stats.ReproductionStats;
import innovation.InnovationDB;
import operators.Crossover;
import operators.Mutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Species class: Defines a group of homogenous Genomes as a species. Species are used to implement speciation within
 * NEAT in order to protect innovation
 * @author Acemad
 */
public class Species implements Comparable<Species>, Serializable {

    // Species id, unique
    private final int id;

    // The list of Genomes belonging to this species
    private List<Genome> members = new ArrayList<>();

    // The leader/representative of this species
    private Genome leader;

    // Species' fitness-related values
    private double totalAdjustedFitness;
    private double averageAdjustedFitness;
    private Double maxFitnessSoFar = null;

    // How many generation the max fitness of the species did not improve
    private int staleness;

    // How many offspring this species should spawn
    private int spawnAmount;

    // Species age
    private int age = 0;

    // Species phased search *****
    // The pruning threshold after which we switch to the simplifying phase
    private double pruneThreshold;
    // Flag indicating whether we're currently in the simplifying phase or not
    private boolean simplifyingPhase = false;
    // The age of the species at which the last transition to simplifying phase happened
    private int lastTransitionAge;
    // Summary statistics for the mean complexity of the species
    private final DoubleSummaryStatistics complexityStats = new DoubleSummaryStatistics();

    /**
     * Creates a new Species using a given Genome, which becomes the leader of the new species
     * @param genome The first member and leader of the new species
     * @param innovationDB Innovation database used for generating unique species id
     */
    public Species(Genome genome, InnovationDB innovationDB) {
        this.members.add(genome);
        this.leader = genome;
        this.id = innovationDB.getNewSpeciesId();
    }

    /**
     * Adds a new member to this species
     * @param genome The new member Genome
     */
    public void addMember(Genome genome) {
        this.members.add(genome);
    }

    /**
     * Resets the leader of the species to the best member of the Species.
     */
    public void resetLeader() {
        if (!members.isEmpty()) {
            members.sort(Collections.reverseOrder());
            this.leader = members.get(0);
        }
        // this.maxFitnessSoFar = this.leader.getFitness();
    }

    /**
     * Compute the adjusted fitness of all members in the species, and calculate the total adjusted fitness of the
     * species, as well as the average adjusted fitness
     */
    public void computeAdjustedFitness() {

        double memberAdjustedFitness;
        totalAdjustedFitness = 0;

        // Compute adjusted fitness for each member
        for (Genome member : members) {
            memberAdjustedFitness = member.getFitness() / getSize();
            member.setAdjustedFitness(memberAdjustedFitness);
            // Accumulate totalAdjustedFitness
            totalAdjustedFitness += memberAdjustedFitness;
        }

        // Compute average adjusted fitness.
        averageAdjustedFitness = totalAdjustedFitness / getSize();
    }

    /**
     * Checks the staleness of the species against a given maximum staleness threshold.
     * @param maxSpeciesStaleness The maximum number of species generations in which no improvements occur
     * @return True if the species' staleness is greater than the threshold, false otherwise
     */
    public boolean checkStaleness(int maxSpeciesStaleness) {

        // Check for changes in the maximum fitness observed so far. Increment staleness if no change happened
        if (maxFitnessSoFar == null || leader.getFitness() > maxFitnessSoFar) {
            maxFitnessSoFar = leader.getFitness();
            staleness = 0;
        } else {
            staleness++;
        }

        // Check if the staleness surpasses a predefined threshold
        if (staleness > maxSpeciesStaleness) {
            staleness = 0;
            return true;
        }

        return false;
    }

    /**
     * Spawns a number of offspring Genomes equal to spawnAmount through a series of reproduction mechanisms (mutation,
     * crossover) within the members of this species
     *
     * @param innovationDB The innovations DB, required for reproduction operators
     * @param config The configuration instance containing all parameters
     * @param reproductionStats For keeping the statistics related to the frequencies of operators application
     * @return A list of new offspring genomes
     */
    public List<Genome> spawnOffsprings(InnovationDB innovationDB, NEATConfig config,
                                        ReproductionStats reproductionStats) {

        // Phase switching immediately before reproduction. Each species will select the appropriate mode using the
        // flag set by the phase selection method
        if (config.speciesPhasedSearch() && !config.globalPhasedSearch()) {
            if (simplifyingPhase) config.switchToSimplifying();
            else config.switchToComplexifying();
        }

        // The list of new offspring to generate
        List<Genome> offsprings = new ArrayList<>();

        // Elitism: Add the leader of the species first, if the spawnAmount > 0
        if (spawnAmount > 0 && config.elitismInSpecies())
            offsprings.add(new Genome(leader, innovationDB));

        // Spawn the required number of offsprings
        while (offsprings.size() < spawnAmount) {

            // The offspring to spawn
            Genome offspring;

            // Generate one offspring
            // If there is only one member and the mutate-only probability is high enough, generate an offspring through
            // mutation only
            if (members.size() < 2 || PRNG.nextDouble() < config.mutateOnlyProbability()) { // Mutate only

                // Select a random Genome and clone it
                Genome randomGenome = members.get(PRNG.nextInt(members.size()));
                offspring = new Genome(randomGenome, innovationDB);

                // Mutate the newly created offspring
                offspring = mutate(offspring, innovationDB, config, reproductionStats);

                /*Stats*/ reproductionStats.mutationOnlyReproductions().plusOne();
                /*Stats*/ reproductionStats.mutations().plusOne();

            } else { // In this case mating is a more meaningful choice

                // Select two parents randomly
                List<Genome> parents = new ArrayList<>(members);
                Genome parentA = parents.remove(PRNG.nextInt(parents.size()));
                Genome parentB = parents.remove(PRNG.nextInt(parents.size()));

                // Apply crossover
                offspring = Crossover.multipointCrossover(parentA, parentB, config, innovationDB);

                /*Stats*/ reproductionStats.matings().plusOne();

                // Mutate the resulting offspring only if the probability of mating only is low enough
                if (PRNG.nextDouble() > config.mateOnlyProbability()) {
                    offspring = mutate(offspring, innovationDB, config, reproductionStats);

                    /*Stats*/ reproductionStats.mutations().plusOne();
                    /*Stats*/ reproductionStats.matingPlusMutationReproductions().plusOne();
                } else
                    /*Stats*/ reproductionStats.matingOnlyReproductions().plusOne();
            }

            if (config.fixDanglingNodes()) {
                int danglingNodesFound;
                do {
                    danglingNodesFound = offspring.fixDanglingNodes(innovationDB, config.danglingRemoveProbability());
                    if (!config.fixDanglingNodesStrict()) break;
                } while (danglingNodesFound > 0);
            }
            // offspring.checkGenomeConsistency(innovationDB);


            // Add the new offspring to the new offsprings list
            offsprings.add(offspring);

            /*Stats*/ reproductionStats.totalReproductions().plusOne();
        }

        // Increment species age
        age++;

        return offsprings;
    }

    /**
     * Apply multiple mutation operators on a given Genome. The Genome returned is a mutated copy of the input Genome
     *
     * @param genome The genome to mutate
     * @param innovationDB The innovation database
     * @param config The configuration instance containing all parameters
     * @param reproductionStats For keeping the statistics related to the frequencies of operators application
     * @return A mutated Genome
     */
    private Genome mutate(Genome genome, InnovationDB innovationDB, NEATConfig config,
                          ReproductionStats reproductionStats) {

        // System.out.println("\t\t\t selected:" + genome.toConciseString());
        // Copy the reference of the input Genome
        Genome mutatedGenome = genome;

        boolean structuralMutation = false;

        // AddNode mutation
        if (PRNG.nextDouble() < config.mutateAddNodeProbability()) {
            // System.out.println("Add Node mutation start: " + genome.toConciseString());
            mutatedGenome = Mutation.addNewNode(mutatedGenome, innovationDB, config);
            structuralMutation = true;

            /*Stats*/ reproductionStats.addNodeMutations().plusOne();
        }

        // AddLink mutation
        if (PRNG.nextDouble() < config.mutateAddLinkProbability()) {
            // System.out.println("Add Link mutation start: " + genome.toConciseString());
            mutatedGenome = Mutation.addNewLink(mutatedGenome, innovationDB, config);
            structuralMutation = true;

            /*Stats*/ reproductionStats.addLinkMutations().plusOne();
        }

        if (PRNG.nextDouble() < config.mutateDeleteLinkProbability()) {
            mutatedGenome = Mutation.deleteLink(mutatedGenome, innovationDB);
            structuralMutation = true;

            /*Stats*/ reproductionStats.deleteLinkMutations().plusOne();
        }

        if (PRNG.nextDouble() < config.mutateDeleteNodeProbability()) {
            mutatedGenome = Mutation.deleteNode(mutatedGenome, innovationDB);
            structuralMutation = true;

            /*Stats*/ reproductionStats.deleteNodeMutations().plusOne();
        }

        if (PRNG.nextDouble() < config.mutateReOrientLinkProbability()) {
            mutatedGenome = Mutation.reOrientLink(mutatedGenome, innovationDB, config);
            structuralMutation = true;

            /*Stats*/ reproductionStats.reOrientLinkMutations().plusOne();
        }



        // If no structural mutation succeeded, proceed with the other mutations
        if (!structuralMutation) {
            // Weight mutation
            if (PRNG.nextDouble() < config.mutateWeightProbability()) {
                mutatedGenome = Mutation.mutateWeights(mutatedGenome, innovationDB, config);
                /*Stats*/ reproductionStats.weightMutations().plusOne();
            }
            // ToggleEnable mutation
            if (PRNG.nextDouble() < config.mutateToggleEnableProbability()) {
                mutatedGenome = Mutation.mutateToggleEnable(mutatedGenome, innovationDB);
                /*Stats*/ reproductionStats.toggleEnableMutations().plusOne();
            }
            // ReEnable mutation
            if (PRNG.nextDouble() < config.mutateReEnableProbability()) {
                mutatedGenome = Mutation.mutateReEnable(mutatedGenome, innovationDB);
                /*Stats*/ reproductionStats.reEnableMutations().plusOne();
            }
            // Activation mutation
            if (PRNG.nextDouble() < config.mutateActivationProbability()) {
                mutatedGenome = Mutation.mutateActivationType(mutatedGenome, innovationDB,
                        config.mutateActivationProportion(), config.allowedActivations());
                /*Stats*/ reproductionStats.activationMutations().plusOne();
            }
        }

        return mutatedGenome;
    }

    /**
     * Selects a subset of the species for reproduction
     * @param survivalThreshold The percent of members to select from an ordered list of members
     */
    public void selectParents(double survivalThreshold) {

        // Members must be ordered beforehand. The operations prior to this (resetLeader) guarantee an ordered list of
        // members.

        // Operates only on non-empty species
        if (!members.isEmpty()) {
            // Compute the number of parents from the actual size. We add 1 to at least select one individual
            int numberOfParents = (int) Math.floor(survivalThreshold * members.size() + 1);

            // Select the parents from the top of the list
            List<Genome> survivors = new ArrayList<>();
            for (int i = 0; i < numberOfParents; i++)
                survivors.add(members.get(i));

            // The survivors replace the members
            members = survivors;
        }
    }

    /**
     * Species phase selection method. This is responsible for selecting the appropriate phase (complexification or
     * simplification) species wise. In a complexification phase the species will reproduce using additive mutations
     * and in the simplification phase the species will reproduce using subtractive mutations.
     * This method is identical to the globalPhaseSelection method of the population class except for the way switching
     * happens. In this method a simple flag is used to switch phases, and the flag is used by the reproduction method
     * to determine phase before reproduction. This is to avoid one species changing the phase of other species.
     * (TODO Find a better way)
     *
     * @param config The configuration instance containing all parameters.
     */
    public void selectPhase(NEATConfig config) {

        double meanComplexity = meanComplexity();

        if (age == 0) {
            complexityStats.accept(meanComplexity);
            pruneThreshold = meanComplexity + config.meanComplexityThreshold();
        }

        double previousMeanComplexityAvg = complexityStats.getAverage();

        if (age >= 1) complexityStats.accept(meanComplexity);

        if (!simplifyingPhase) {
            if ((meanComplexity > pruneThreshold) && (staleness > config.minStaleComplexifyGenerations())) {
                simplifyingPhase = true;
                lastTransitionAge = age;
                // System.out.println(age + " Switching --> Simplifying " + meanComplexity + " > " + pruneThreshold
                //         + " , S:" + id);
            }
        }

        else {
            if (((age - lastTransitionAge) >= config.minSimplifyGenerations()) && (meanComplexity < pruneThreshold)
                    && (complexityStats.getAverage() >= previousMeanComplexityAvg)) {
                // System.out.println(age + " Switching --> Complexifying " + meanComplexity + " < " + pruneThreshold
                //         + " , S:" + id);

                simplifyingPhase = false;
                if (config.relativeThreshold())
                    pruneThreshold = meanComplexity + config.meanComplexityThreshold();
            }
        }
    }

    /**
     * Compute the mean complexity of the members of the species
     * @return Mean complexity of the members of this species
     */
    public double meanComplexity() {
        double sum = 0.0;
        for (Genome member : members)
            sum += member.complexity();
        return sum / members.size();
    }

    @Override
    public String toString() {
        return "Species " + id +
                "{ , members \n" + members +
                ", leader \n" + leader +
                "\n offspringToProduce=" + spawnAmount +
                ", totalAdjustedFitness=" + totalAdjustedFitness +
                ", averageAdjustedFitness=" + averageAdjustedFitness +
                '}';
    }

    /**
     * Generates a concise String representation of this species
     * @return A String representing a concise form of the species
     */
    public String toConciseString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("S%-2d",id))
               .append(String.format("<%2d> : (", getSize()));

        for (Genome member : members) {
            if (member == leader)
                builder.append(String.format("*G%-2d", member.getId()));
            else
                builder.append(String.format("G%-2d", member.getId()));

            if (members.indexOf(member) < members.size() - 1) builder.append(", ");
        }

        builder.append(String.format(") -> taf: % 2.2f", totalAdjustedFitness))
                .append(String.format(", aaf: % 2.2f", averageAdjustedFitness))
                .append(String.format(", maxfsf: % 2.2f", maxFitnessSoFar))
                .append(String.format(", stale: %d", staleness))
                .append(String.format(", sa: % d", spawnAmount));

        return builder.toString();
    }

    /**
     * Species are compared by the fitness of their leader
     * @param species The species to compare with
     * @return Results of the comparison
     */
    @Override
    public int compareTo(Species species) {
        return leader.compareTo(species.getLeader());
        // return Double.compare(leader.getFitness(), species.getLeader().getFitness());
    }

    public Genome getLeader() {
        return this.leader;
    }

    public List<Genome> getMembers() {
        return members;
    }

    public int getSize() {
        return members.size();
    }

    public int getSpawnAmount() {
        return spawnAmount;
    }

    public double getTotalAdjustedFitness() {
        return totalAdjustedFitness;
    }

    public void setSpawnAmount(int spawnAmount) {
        this.spawnAmount = spawnAmount;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public boolean isSimplifyingPhase() {
        return simplifyingPhase;
    }
}
