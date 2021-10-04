package engine;

import activations.ActivationType;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * A NEAT configuration parameters file reader
 * @author Acemad
 */
public class NEATConfig {

    // Parameters store
    private final Properties configs = new Properties();

    /* Initial population parameters: ********************************************************************************/

    private int             populationSize;             // Number of genomes in the population
    private int             numInput;                   // Number of input nodes
    private int             numOutput;                  // Number of output nodes
    private double          connectionProbability;      // The probability of creating initial input->output links
    private boolean         includeBias;                // Include a bias node, or not
    private double          biasConnectionProbability;  // The probability of creating initial bias->output links
    private ActivationType  defaultActivationType;      // Activation function of the initial nodes
    private double          weightRangeMin;             // Minimum link weight (initially and if capWeights is true)
    private double          weightRangeMax;             // Maximum link weight (ditto)

    /* Link type filtering: limits the type of links that can be added to genome *************************************/

    private boolean         linkTypeFiltering;                        // Enable/disable link type filtering
    private double          linksBetweenHiddenNodesProportion;        // Rate of all types of hidden->hidden links
    private double          hiddenLoopLinksProportion;                // Rate of hiddenA->hiddenA loop links
    private double          outputLoopLinksProportion;                // Rate of outputA->outputA loop links
    private double          outputToHiddenLinksProportion;            // Rate of output->hidden links
    private double          outputToOutputLinksProportion;            // Rate of output->output non-loop links
    private double          hiddenToHiddenBackwardLinksProportion;    // Rate of hidden->hidden backwards links
    private double          hiddenToHiddenSameLevelLinksProportion;   // Rate of hidden->hidden same level links

    /* General evolution parameters **********************************************************************************/

    private int             maxPopulationStaleness;     // Maximum generations the population's max fitness stales
    private int             maxSpeciesStaleness;        // Maximum generations a species max fitness doesn't improve
    private double          parentsSurvivalThreshold;   // Percent of parents from each species allowed reproducing
    private boolean         elitismInSpecies;           // Include the best genome of the species for the next gen

    /* Speciation parameters *****************************************************************************************/

    private double          unmatchedCoeff;             // Coefficient of unmatched links in compatibility score
    private double          weightDiffCoeff;            // Coefficient of weight difference in compatibility score
    private double          activationDiffCoeff;        // Coefficient of activation difference in compatibility score
    private double          compatibilityThreshold;     // Score threshold over which two genomes are incompatible
    private boolean         aimForSpeciesNumber;        // Aim for a fixed number of species (dynamic threshold)
    private int             speciesNumberTarget;        // Number of species to aim for if the above is true
    private double          compatibilityThresholdIncrement; // The number by which the threshold is increased/decreased

    /* Mating parameters *********************************************************************************************/

    private double          mateOnlyProbability;        // Prob of mating-only reproduction (if enough genomes exist)
    private double          mateAveragingProbability;           // Prob of averaging matching gene weights on mating
    private double          mateKeepGeneDisabledProbability;    // Prob of keeping a genes disabled

    /* Mutation parameters *******************************************************************************************/

    private double          mutateOnlyProbability;      // Prob of mutation-only reproduction
    private double          mutateAddNodeProbability;             // Prob. of adding a new node by interrupting a link
    private double          mutateAddNodeOldLinksPriority;        // Add node: Prioritize older links for interruption
    private double          mutateAddLinkProbability;             // Prob. of adding a new link to the genome
    private double          mutateWeightProbability;              // Prob. of mutating links' weights
    private double          mutateWeightProportion;               // Weight mut: Proportion of links to mutate by genome
    private double          weightPerturbationStrength;           // Weight mut: Strength of weight perturbation
    private double          gaussianWeightPerturbationProportion; // Weight mut: Proportion of Gaussian perturbations
    private double          gaussianWeightPerturbationSigma;      // Wright mut: Standard deviation of Gauss. Perturb.
    private boolean         capWeights;                           // Weight mut: Cap weights after mutation
    private double          mutateToggleEnableProbability;        // Prob. of randomly enabling/disabling links
    private double          mutateReEnableProbability;            // Prob. of randomly re-enabling disabled links
    private double          mutateActivationProbability;          // Prob. of mutating node's activation functions
    private String          allowedActivations;                   // Act mut: The activation functions allowed
    private double          mutateActivationProportion;           // Act mut: The proportion of nodes to mutate act.
    private double          mutateDeleteLinkProbability;          // Prob. of deleting a random link
    private double          mutateDeleteNodeProbability;          // Prob. of deleting a random node (single in or out)
    private double          mutateReOrientLinkProbability;        // Prob. of re-orienting a random link

    /* Phased Search parameters **************************************************************************************/

    private boolean         globalPhasedSearch;                    // Enable/disable phased search population-wise
    private boolean         speciesPhasedSearch;                   // Enable/disable phased search species-wise
    private double          meanComplexityThreshold;               // Complexity threshold after which switching occurs
    private boolean         relativeThreshold;                     // T:Compute new threshold, F: keep abs threshold
    private int             minStaleComplexifyGenerations;         // Min staleness after which switching is ok
    private int             minSimplifyGenerations;                // Min generations to stay in simplify phase
    private double          mutateDeleteLinkProbabilitySimplify;   // Prob. of deleting a random link in simplify mode
    private double          mutateDeleteNodeProbabilitySimplify;   // Prob. of deleting a random node in simplify mode
    private double          mutateWeightProbabilitySimplify;       // Prob. of weigh mutation in simplify mode
    private double          mutateActivationProbabilitySimplify;   // Prob. of activation mutation in simplify mode
    private double          mutateReOrientLinkProbabilitySimplify; // Prob. of re-orient mutation in simplify mode

    /* Structure repair parameters: fixing dangling nodes by removing them or reconnecting them to input/output ******/

    private boolean         fixDanglingNodes;            // Enable/disable fixing nodes at a dead-end after reproducing
    private boolean         fixDanglingNodesStrict;      // Execute multiple passes to remove all dangling nodes
    private double          danglingRemoveProbability;   // Prob. of removing a dangling node, reconnect otherwise

    /* System ********************************************************************************************************/

    private int             evaluationThreads;

    /* Backup variables for phase switching parameters ***************/

    private double backupMutateAddLinkProbability;
    private double backupMutateAddNodeProbability;
    private double backupMutateOnlyProbability;
    private double backupMutateDeleteLinkProbability;
    private double backupMutateWeightProbability;
    private double backupMutateActivationProbability;
    private double backupMutateReOrientLinkProbability;
    private double backupMutateDeleteNodeProbability;

    /**
     * Constructor: creates a NEATConfig instance using the given file path. Loads the file and initializes the instance
     * variables with the values from the file
     *
     * @param configFile Path towards a Parameters file
     */
    public NEATConfig(String configFile) {

        // Load the configurations file
        loadConfigurations(configFile);

        // Instance variables initialization
        /* Initial population parameters: ****************************************************************************/

        populationSize                         = Integer.parseInt(configs.getProperty("populationSize"));
        numInput                               = Integer.parseInt(configs.getProperty("numInput"));
        numOutput                              = Integer.parseInt(configs.getProperty("numOutput"));
        connectionProbability                  = Double.parseDouble(configs.getProperty("connectionProbability"));
        includeBias                            = Boolean.parseBoolean(configs.getProperty("includeBias"));
        biasConnectionProbability              = Double.parseDouble(configs.getProperty("biasConnectionProbability"));
        defaultActivationType                  = ActivationType.valueOf(configs.getProperty("defaultActivationType"));
        weightRangeMin                         = Double.parseDouble(configs.getProperty("weightRangeMin"));
        weightRangeMax                         = Double.parseDouble(configs.getProperty("weightRangeMax"));

        /* Link type filtering parameters: ***************************************************************************/

        linkTypeFiltering                      = Boolean.parseBoolean(configs.getProperty("linkTypeFiltering"));
        linksBetweenHiddenNodesProportion      = Double.parseDouble(configs.getProperty("linksBetweenHiddenNodesProportion"));
        hiddenLoopLinksProportion              = Double.parseDouble(configs.getProperty("hiddenLoopLinksProportion"));
        outputLoopLinksProportion              = Double.parseDouble(configs.getProperty("outputLoopLinksProportion"));
        outputToHiddenLinksProportion          = Double.parseDouble(configs.getProperty("outputToHiddenLinksProportion"));
        outputToOutputLinksProportion          = Double.parseDouble(configs.getProperty("outputToOutputLinksProportion"));
        hiddenToHiddenBackwardLinksProportion  = Double.parseDouble(configs.getProperty("hiddenToHiddenBackwardLinksProportion"));
        hiddenToHiddenSameLevelLinksProportion = Double.parseDouble(configs.getProperty("hiddenToHiddenSameLevelLinksProportion"));

        /* General evolution parameters ******************************************************************************/

        maxPopulationStaleness                 = Integer.parseInt(configs.getProperty("maxPopulationStaleness"));
        maxSpeciesStaleness                    = Integer.parseInt(configs.getProperty("maxSpeciesStaleness"));
        parentsSurvivalThreshold               = Double.parseDouble(configs.getProperty("parentsSurvivalThreshold"));
        elitismInSpecies                       = Boolean.parseBoolean(configs.getProperty("elitismInSpecies"));

        /* Speciation parameters *************************************************************************************/

        unmatchedCoeff                         = Double.parseDouble(configs.getProperty("unmatchedCoeff"));
        weightDiffCoeff                        = Double.parseDouble(configs.getProperty("weightDiffCoeff"));
        activationDiffCoeff                    = Double.parseDouble(configs.getProperty("activationDiffCoeff"));
        compatibilityThreshold                 = Double.parseDouble(configs.getProperty("compatibilityThreshold"));
        aimForSpeciesNumber                    = Boolean.parseBoolean(configs.getProperty("aimForSpeciesNumber"));
        speciesNumberTarget                    = Integer.parseInt(configs.getProperty("speciesNumberTarget"));
        compatibilityThresholdIncrement        = Double.parseDouble(configs.getProperty("compatibilityThresholdIncrement"));

        /* Mating parameters *****************************************************************************************/

        mateOnlyProbability                    = Double.parseDouble(configs.getProperty("mateOnlyProbability"));
        mateAveragingProbability               = Double.parseDouble(configs.getProperty("mateAveragingProbability"));
        mateKeepGeneDisabledProbability        = Double.parseDouble(configs.getProperty("mateKeepGeneDisabledProbability"));

        /* Mutation parameters ***************************************************************************************/

        mutateOnlyProbability                  = Double.parseDouble(configs.getProperty("mutateOnlyProbability"));
        mutateAddNodeProbability               = Double.parseDouble(configs.getProperty("mutateAddNodeProbability"));
        mutateAddNodeOldLinksPriority          = Double.parseDouble(configs.getProperty("mutateAddNodeOldLinksPriority"));
        mutateAddLinkProbability               = Double.parseDouble(configs.getProperty("mutateAddLinkProbability"));
        mutateWeightProbability                = Double.parseDouble(configs.getProperty("mutateWeightProbability"));
        mutateWeightProportion                 = Double.parseDouble(configs.getProperty("mutateWeightProportion"));
        weightPerturbationStrength             = Double.parseDouble(configs.getProperty("weightPerturbationStrength"));
        gaussianWeightPerturbationProportion   = Double.parseDouble(configs.getProperty("gaussianWeightPerturbationProportion"));
        gaussianWeightPerturbationSigma        = Double.parseDouble(configs.getProperty("gaussianWeightPerturbationSigma"));
        capWeights                             = Boolean.parseBoolean(configs.getProperty("capWeights"));
        mutateToggleEnableProbability          = Double.parseDouble(configs.getProperty("mutateToggleEnableProbability"));
        mutateReEnableProbability              = Double.parseDouble(configs.getProperty("mutateReEnableProbability"));
        mutateActivationProbability            = Double.parseDouble(configs.getProperty("mutateActivationProbability"));
        allowedActivations                     = configs.getProperty("allowedActivations");
        mutateActivationProportion             = Double.parseDouble(configs.getProperty("mutateActivationProportion"));
        mutateDeleteLinkProbability            = Double.parseDouble(configs.getProperty("mutateDeleteLinkProbability"));
        mutateDeleteNodeProbability            = Double.parseDouble(configs.getProperty("mutateDeleteNodeProbability"));
        mutateReOrientLinkProbability          = Double.parseDouble(configs.getProperty("mutateReOrientLinkProbability"));

        /* Phased Search parameters **********************************************************************************/

        globalPhasedSearch                     = Boolean.parseBoolean(configs.getProperty("globalPhasedSearch"));
        speciesPhasedSearch                    = Boolean.parseBoolean(configs.getProperty("speciesPhasedSearch"));
        meanComplexityThreshold                = Double.parseDouble(configs.getProperty("meanComplexityThreshold"));
        relativeThreshold                      = Boolean.parseBoolean(configs.getProperty("relativeThreshold"));
        minStaleComplexifyGenerations          = Integer.parseInt(configs.getProperty("minStaleComplexifyGenerations"));
        minSimplifyGenerations                 = Integer.parseInt(configs.getProperty("minSimplifyGenerations"));
        mutateDeleteLinkProbabilitySimplify    = Double.parseDouble(configs.getProperty("mutateDeleteLinkProbabilitySimplify"));
        mutateDeleteNodeProbabilitySimplify    = Double.parseDouble(configs.getProperty("mutateDeleteNodeProbabilitySimplify"));
        mutateWeightProbabilitySimplify        = Double.parseDouble(configs.getProperty("mutateWeightProbabilitySimplify"));
        mutateActivationProbabilitySimplify    = Double.parseDouble(configs.getProperty("mutateActivationProbabilitySimplify"));
        mutateReOrientLinkProbabilitySimplify  = Double.parseDouble(configs.getProperty("mutateReOrientLinkProbabilitySimplify"));

        /* Structure repair parameters *******************************************************************************/

        fixDanglingNodes                       = Boolean.parseBoolean(configs.getProperty("fixDanglingNodes"));
        fixDanglingNodesStrict                 = Boolean.parseBoolean(configs.getProperty("fixDanglingNodesStrict"));
        danglingRemoveProbability              = Double.parseDouble(configs.getProperty("danglingRemoveProbability"));

        /* System ****************************************************************************************************/

        evaluationThreads                      = Integer.parseInt(configs.getProperty("evaluationThreads"));

        // Backup for phased search: Keep a copy of the parameters that change between phases in order to restore them
        // later
        backupMutateAddLinkProbability = mutateAddLinkProbability;
        backupMutateAddNodeProbability = mutateAddNodeProbability;
        backupMutateOnlyProbability = mutateOnlyProbability;
        backupMutateDeleteLinkProbability = mutateDeleteLinkProbability;
        backupMutateWeightProbability = mutateWeightProbability;
        backupMutateActivationProbability = mutateActivationProbability;
        backupMutateReOrientLinkProbability = mutateReOrientLinkProbability;
        backupMutateDeleteNodeProbability = mutateDeleteNodeProbability;

    }

    /**
     * Load the parameter configuration file from the given file path. The configuration file is a simple Java
     * Proprieties file.
     *
     * @param configFile Path to the configuration file
     */
    private void loadConfigurations(String configFile) {

        try {
            configs.load(new FileInputStream(configFile));
        } catch (Exception exception) {
            System.err.println("Problem loading configuration file");
            exception.printStackTrace();
        }
    }

    /**
     * Switch a subset of parameters to the simplify variants to trigger entry to the simplification phase in a phased
     * search.
     */
    public void switchToSimplifying() {
        mutateAddLinkProbability      = 0;
        mutateAddNodeProbability      = 0;
        mutateOnlyProbability         = 1;
        mutateDeleteLinkProbability   = mutateDeleteLinkProbabilitySimplify;
        mutateWeightProbability       = mutateWeightProbabilitySimplify;
        mutateActivationProbability   = mutateActivationProbabilitySimplify;
        mutateReOrientLinkProbability = mutateReOrientLinkProbabilitySimplify;
        mutateDeleteNodeProbability   = mutateDeleteNodeProbabilitySimplify;
    }

    /**
     * Switch the same subset of parameters to their original values to trigger a return to the complexification phase
     * in a phased search.
     */
    public void switchToComplexifying() {
        mutateAddLinkProbability      = backupMutateAddLinkProbability;
        mutateAddNodeProbability      = backupMutateAddNodeProbability;
        mutateOnlyProbability         = backupMutateOnlyProbability;
        mutateDeleteLinkProbability   = backupMutateDeleteLinkProbability;
        mutateWeightProbability       = backupMutateWeightProbability;
        mutateActivationProbability   = backupMutateActivationProbability;
        mutateReOrientLinkProbability = backupMutateReOrientLinkProbability;
        mutateDeleteNodeProbability   = backupMutateDeleteNodeProbability;
    }

    /**
     * Increments the compatibility threshold by the given value. This is used for dynamic threshold adaptation.
     * @param value The value used to increment compatibility threshold
     */
    public void incrementCompatibilityThresholdBy(double value) {
        compatibilityThreshold += value;
    }

    /**
     * Set the compatibility threshold to the given value. This is used for dynamic threshold adaptation
     * @param compatibilityThreshold The new threshold
     */
    public void setCompatibilityThreshold(double compatibilityThreshold) {
        this.compatibilityThreshold = compatibilityThreshold;
    }

    @Override
    public String toString() {
        return "NEATConfig{" +
                "populationSize=" + populationSize +
                ", numInput=" + numInput +
                ", numOutput=" + numOutput +
                ", connectionProbability=" + connectionProbability +
                ", includeBias=" + includeBias +
                ", biasConnectionProbability=" + biasConnectionProbability +
                ", defaultActivationType=" + defaultActivationType +
                ", weightRangeMin=" + weightRangeMin +
                ", weightRangeMax=" + weightRangeMax +
                ", linkTypeFiltering=" + linkTypeFiltering +
                ", linksBetweenHiddenNodesProportion=" + linksBetweenHiddenNodesProportion +
                ", hiddenLoopLinksProportion=" + hiddenLoopLinksProportion +
                ", outputLoopLinksProportion=" + outputLoopLinksProportion +
                ", outputToHiddenLinksProportion=" + outputToHiddenLinksProportion +
                ", outputToOutputLinksProportion=" + outputToOutputLinksProportion +
                ", hiddenToHiddenBackwardLinksProportion=" + hiddenToHiddenBackwardLinksProportion +
                ", hiddenToHiddenSameLevelLinksProportion=" + hiddenToHiddenSameLevelLinksProportion +
                ", maxPopulationStaleness=" + maxPopulationStaleness +
                ", maxSpeciesStaleness=" + maxSpeciesStaleness +
                ", parentsSurvivalThreshold=" + parentsSurvivalThreshold +
                ", elitismInSpecies=" + elitismInSpecies +
                ", unmatchedCoeff=" + unmatchedCoeff +
                ", weightDiffCoeff=" + weightDiffCoeff +
                ", activationDiffCoeff=" + activationDiffCoeff +
                ", compatibilityThreshold=" + compatibilityThreshold +
                ", aimForSpeciesNumber=" + aimForSpeciesNumber +
                ", speciesNumberTarget=" + speciesNumberTarget +
                ", compatibilityThresholdIncrement=" + compatibilityThresholdIncrement +
                ", mateOnlyProbability=" + mateOnlyProbability +
                ", mateAveragingProbability=" + mateAveragingProbability +
                ", mateKeepGeneDisabledProbability=" + mateKeepGeneDisabledProbability +
                ", mutateOnlyProbability=" + mutateOnlyProbability +
                ", mutateAddNodeProbability=" + mutateAddNodeProbability +
                ", mutateAddNodeOldLinksPriority=" + mutateAddNodeOldLinksPriority +
                ", mutateAddLinkProbability=" + mutateAddLinkProbability +
                ", mutateWeightProbability=" + mutateWeightProbability +
                ", mutateWeightProportion=" + mutateWeightProportion +
                ", weightPerturbationStrength=" + weightPerturbationStrength +
                ", gaussianWeightPerturbationProportion=" + gaussianWeightPerturbationProportion +
                ", gaussianWeightPerturbationSigma=" + gaussianWeightPerturbationSigma +
                ", capWeights=" + capWeights +
                ", mutateToggleEnableProbability=" + mutateToggleEnableProbability +
                ", mutateReEnableProbability=" + mutateReEnableProbability +
                ", mutateActivationProbability=" + mutateActivationProbability +
                ", allowedActivations='" + allowedActivations + '\'' +
                ", mutateActivationProportion=" + mutateActivationProportion +
                ", mutateDeleteLinkProbability=" + mutateDeleteLinkProbability +
                ", mutateDeleteNodeProbability=" + mutateDeleteNodeProbability +
                ", mutateReOrientLinkProbability=" + mutateReOrientLinkProbability +
                ", globalPhasedSearch=" + globalPhasedSearch +
                ", speciesPhasedSearch=" + speciesPhasedSearch +
                ", meanComplexityThreshold=" + meanComplexityThreshold +
                ", relativeThreshold=" + relativeThreshold +
                ", minStaleComplexifyGenerations=" + minStaleComplexifyGenerations +
                ", minSimplifyGenerations=" + minSimplifyGenerations +
                ", mutateDeleteLinkProbabilitySimplify=" + mutateDeleteLinkProbabilitySimplify +
                ", mutateDeleteNodeProbabilitySimplify=" + mutateDeleteNodeProbabilitySimplify +
                ", mutateWeightProbabilitySimplify=" + mutateWeightProbabilitySimplify +
                ", mutateActivationProbabilitySimplify=" + mutateActivationProbabilitySimplify +
                ", mutateReOrientLinkProbabilitySimplify=" + mutateReOrientLinkProbabilitySimplify +
                ", fixDanglingNodes=" + fixDanglingNodes +
                ", fixDanglingNodesStrict=" + fixDanglingNodesStrict +
                ", danglingRemoveProbability=" + danglingRemoveProbability +
                ", evaluationThreads=" + evaluationThreads +
                '}';
    }

    /* Parameter Getters *********************************************************************************************/

    public int numInput() {
        return numInput;
    }

    public int numOutput() {
        return numOutput;
    }

    public double connectionProbability() {
        return connectionProbability;
    }

    public boolean includeBias() {
        return includeBias;
    }

    public double biasConnectionProbability() {
        return biasConnectionProbability;
    }

    public int populationSize() {
        return populationSize;
    }

    public ActivationType defaultActivationType() {
        return defaultActivationType;
    }

    public double unmatchedCoeff() {
        return unmatchedCoeff;
    }

    public double weightDiffCoeff() {
        return weightDiffCoeff;
    }

    public double compatibilityThreshold() {
        return compatibilityThreshold;
    }

    public int maxSpeciesStaleness() {
        return maxSpeciesStaleness;
    }

    public int maxPopulationStaleness() {
        return maxPopulationStaleness;
    }

    public double parentsSurvivalThreshold() {
        return parentsSurvivalThreshold;
    }

    public boolean elitismInSpecies() {
        return elitismInSpecies;
    }

    public double mutateOnlyProbability() {
        return mutateOnlyProbability;
    }

    public double mateOnlyProbability() {
        return mateOnlyProbability;
    }

    public double mateAveragingProbability() {
        return mateAveragingProbability;
    }

    public double mateKeepGeneDisabledProbability() {
        return mateKeepGeneDisabledProbability;
    }

    public double mutateAddNodeProbability() {
        return mutateAddNodeProbability;
    }

    public double mutateAddLinkProbability() {
        return mutateAddLinkProbability;
    }

    public double mutateWeightProbability() {
        return mutateWeightProbability;
    }

    public double mutateToggleEnableProbability() {
        return mutateToggleEnableProbability;
    }

    public double mutateReEnableProbability() {
        return mutateReEnableProbability;
    }

    public double mutateActivationProbability() {
        return mutateActivationProbability;
    }

    public double weightRangeMin() {
        return weightRangeMin;
    }

    public double weightRangeMax() {
        return weightRangeMax;
    }

    public double weightPerturbationStrength() {
        return weightPerturbationStrength;
    }

    public double mutateActivationProportion() {
        return mutateActivationProportion;
    }

    public double activationDiffCoeff() {
        return activationDiffCoeff;
    }

    public boolean fixDanglingNodes() {
        return fixDanglingNodes;
    }

    public double danglingRemoveProbability() {
        return danglingRemoveProbability;
    }

    public String allowedActivations() {
        return allowedActivations;
    }

    public boolean linkTypeFiltering() {
        return linkTypeFiltering;
    }

    public double linksBetweenHiddenNodesProportion() {
        return linksBetweenHiddenNodesProportion;
    }

    public double hiddenLoopLinksProportion() {
        return hiddenLoopLinksProportion;
    }

    public double outputLoopLinksProportion() {
        return outputLoopLinksProportion;
    }

    public double outputToHiddenLinksProportion() {
        return outputToHiddenLinksProportion;
    }

    public double outputToOutputLinksProportion() {
        return outputToOutputLinksProportion;
    }

    public double hiddenToHiddenBackwardLinksProportion() {
        return hiddenToHiddenBackwardLinksProportion;
    }

    public double hiddenToHiddenSameLevelLinksProportion() {
        return hiddenToHiddenSameLevelLinksProportion;
    }

    public boolean aimForSpeciesNumber() {
        return aimForSpeciesNumber;
    }

    public int speciesNumberTarget() {
        return speciesNumberTarget;
    }

    public double mutateAddNodeOldLinksPriority() {
        return mutateAddNodeOldLinksPriority;
    }

    public boolean capWeights() {
        return capWeights;
    }

    public boolean fixDanglingNodesStrict() {
        return fixDanglingNodesStrict;
    }

    public boolean globalPhasedSearch() {
        return globalPhasedSearch;
    }

    public double mutateDeleteLinkProbability() {
        return mutateDeleteLinkProbability;
    }

    public double meanComplexityThreshold() {
        return meanComplexityThreshold;
    }

    public double minSimplifyGenerations() {
        return minSimplifyGenerations;
    }

    public int minStaleComplexifyGenerations() {
        return minStaleComplexifyGenerations;
    }

    public boolean relativeThreshold() {
        return relativeThreshold;
    }

    public boolean speciesPhasedSearch() {
        return speciesPhasedSearch;
    }

    public double mutateReOrientLinkProbability() {
        return mutateReOrientLinkProbability;
    }

    public double mutateDeleteNodeProbability() {
        return mutateDeleteNodeProbability;
    }

    public double gaussianWeightPerturbationProportion() {
        return gaussianWeightPerturbationProportion;
    }

    public double gaussianWeightPerturbationSigma() {
        return gaussianWeightPerturbationSigma;
    }

    public double mutateWeightProportion() {
        return mutateWeightProportion;
    }

    public int evaluationThreads() {
        return evaluationThreads;
    }

    public double compatibilityThresholdIncrement() {
        return compatibilityThresholdIncrement;
    }


}
