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

    private int numInput;
    private int numOutput;
    private double connectionProbability;
    private boolean includeBias;
    private double biasConnectionProbability;
    private int populationSize;
    private ActivationType defaultActivationType;
    private double weightRangeMin;
    private double weightRangeMax;

    private boolean linkTypeFiltering;
    private double linksBetweenHiddenNodesRate;
    private double hiddenLoopLinksRate;
    private double outputLoopLinksRate;
    private double outputToHiddenLinksRate;
    private double outputToOutputLinksRate;
    private double hiddenToHiddenBackwardLinksRate;
    private double hiddenToHiddenSameLevelLinksRate;

    private double unmatchedCoeff;
    private double weightDiffCoeff;
    private double activationDiffCoeff;
    private double compatibilityThreshold;
    private boolean aimForSpeciesNumber;
    private int speciesNumberTarget;

    private int maxSpeciesStaleness;

    private int maxPopulationStaleness;

    private double parentsSurvivalThreshold;

    private boolean elitismInSpecies;
    private double mutateOnlyProbability;
    private double mateOnlyProbability;
    private double mateAveragingProbability;
    private double mateKeepGeneDisabledProbability;

    private double mutateAddNodeProbability;
    private double mutateAddNodeOldLinksPriority;
    private double mutateAddLinkProbability;
    private double mutateWeightProbability;
    private boolean capWeights;
    private double mutateToggleEnableProbability;
    private double mutateReEnableProbability;
    private double mutateActivationProbability;

    private double weightPerturbationStrength;
    private double mutateActivationRate;

    private boolean fixDanglingNodes;
    private boolean fixDanglingNodesStrict;
    private double danglingRemoveProbability;

    private String allowedActivations;

    private boolean phasedSearch;
    private double meanComplexityThreshold;
    private boolean absoluteThreshold;
    private int minSimplifyGenerations;
    private int minStaleComplexifyGenerations;
    private double mutateDeleteLinkProbability;
    private double mutateDeleteLinkProbabilitySimplify;
    private double mutateWeightProbabilitySimplify;
    private double mutateActivationProbabilitySimplify;

    private double backupMutateAddLinkProbability;
    private double backupMutateAddNodeProbability;
    private double backupMutateOnlyProbability;
    private double backupMutateDeleteLinkProbability;
    private double backupMutateWeightProbability;
    private double backupMutateActivationProbability;

    /**
     *
     * @param configFile
     */
    public NEATConfig(String configFile) {

        loadConfigurations(configFile);

        numInput = Integer.parseInt(configs.getProperty("numInput"));
        numOutput = Integer.parseInt(configs.getProperty("numOutput"));
        connectionProbability = Double.parseDouble(configs.getProperty("connectionProbability"));
        includeBias = Boolean.parseBoolean(configs.getProperty("includeBias"));
        biasConnectionProbability = Double.parseDouble(configs.getProperty("biasConnectionProbability"));
        populationSize = Integer.parseInt(configs.getProperty("populationSize"));

        unmatchedCoeff = Double.parseDouble(configs.getProperty("unmatchedCoeff"));
        weightDiffCoeff = Double.parseDouble(configs.getProperty("weightDiffCoeff"));
        activationDiffCoeff = Double.parseDouble(configs.getProperty("activationDiffCoeff"));
        compatibilityThreshold = Double.parseDouble(configs.getProperty("compatibilityThreshold"));
        aimForSpeciesNumber = Boolean.parseBoolean(configs.getProperty("aimForSpeciesNumber"));
        speciesNumberTarget = Integer.parseInt(configs.getProperty("speciesNumberTarget"));


        defaultActivationType = ActivationType.valueOf(configs.getProperty("defaultActivationType"));
        weightRangeMin = Double.parseDouble(configs.getProperty("weightRangeMin"));
        weightRangeMax = Double.parseDouble(configs.getProperty("weightRangeMax"));

        linkTypeFiltering = Boolean.parseBoolean(configs.getProperty("linkTypeFiltering"));
        linksBetweenHiddenNodesRate = Double.parseDouble(configs.getProperty("linksBetweenHiddenNodesRate"));
        hiddenLoopLinksRate = Double.parseDouble(configs.getProperty("hiddenLoopLinksRate"));
        outputLoopLinksRate = Double.parseDouble(configs.getProperty("outputLoopLinksRate"));
        outputToHiddenLinksRate = Double.parseDouble(configs.getProperty("outputToHiddenLinksRate"));
        outputToOutputLinksRate = Double.parseDouble(configs.getProperty("outputToOutputLinksRate"));
        hiddenToHiddenBackwardLinksRate = Double.parseDouble(configs.getProperty("hiddenToHiddenBackwardLinksRate"));
        hiddenToHiddenSameLevelLinksRate = Double.parseDouble(configs.getProperty("hiddenToHiddenSameLevelLinksRate"));

        maxSpeciesStaleness = Integer.parseInt(configs.getProperty("maxSpeciesStaleness"));

        maxPopulationStaleness = Integer.parseInt(configs.getProperty("maxPopulationStaleness"));

        parentsSurvivalThreshold = Double.parseDouble(configs.getProperty("parentsSurvivalThreshold"));

        elitismInSpecies = Boolean.parseBoolean(configs.getProperty("elitismInSpecies"));
        mutateOnlyProbability = Double.parseDouble(configs.getProperty("mutateOnlyProbability"));
        mateOnlyProbability = Double.parseDouble(configs.getProperty("mateOnlyProbability"));
        mateAveragingProbability = Double.parseDouble(configs.getProperty("mateAveragingProbability"));
        mateKeepGeneDisabledProbability = Double.parseDouble(configs.getProperty("mateKeepGeneDisabledProbability"));

        mutateAddNodeProbability = Double.parseDouble(configs.getProperty("mutateAddNodeProbability"));
        mutateAddNodeOldLinksPriority = Double.parseDouble(configs.getProperty("mutateAddNodeOldLinksPriority"));
        mutateAddLinkProbability = Double.parseDouble(configs.getProperty("mutateAddLinkProbability"));
        mutateWeightProbability = Double.parseDouble(configs.getProperty("mutateWeightProbability"));
        capWeights = Boolean.parseBoolean(configs.getProperty("capWeights"));
        mutateToggleEnableProbability = Double.parseDouble(configs.getProperty("mutateToggleEnableProbability"));
        mutateReEnableProbability = Double.parseDouble(configs.getProperty("mutateReEnableProbability"));
        mutateActivationProbability = Double.parseDouble(configs.getProperty("mutateActivationProbability"));

        weightPerturbationStrength = Double.parseDouble(configs.getProperty("weightPerturbationStrength"));
        mutateActivationRate = Double.parseDouble(configs.getProperty("mutateActivationRate"));

        fixDanglingNodes = Boolean.parseBoolean(configs.getProperty("fixDanglingNodes"));
        fixDanglingNodesStrict = Boolean.parseBoolean(configs.getProperty("fixDanglingNodesStrict"));
        danglingRemoveProbability = Double.parseDouble(configs.getProperty("danglingRemoveProbability"));
        allowedActivations = configs.getProperty("allowedActivations");

        phasedSearch = Boolean.parseBoolean(configs.getProperty("phasedSearch"));
        meanComplexityThreshold = Double.parseDouble(configs.getProperty("meanComplexityThreshold"));
        minSimplifyGenerations = Integer.parseInt(configs.getProperty("minSimplifyGenerations"));
        absoluteThreshold = Boolean.parseBoolean(configs.getProperty("absoluteThreshold"));
        minStaleComplexifyGenerations = Integer.parseInt(configs.getProperty("minStaleComplexifyGenerations"));
        mutateDeleteLinkProbability = Double.parseDouble(configs.getProperty("mutateDeleteLinkProbability"));
        mutateDeleteLinkProbabilitySimplify = Double.parseDouble(configs.getProperty("mutateDeleteLinkProbabilitySimplify"));
        mutateWeightProbabilitySimplify = Double.parseDouble(configs.getProperty("mutateWeightProbabilitySimplify"));
        mutateActivationProbabilitySimplify = Double.parseDouble(configs.getProperty("mutateActivationProbabilitySimplify"));

        // Backup for phased search
        backupMutateAddLinkProbability = mutateAddLinkProbability;
        backupMutateAddNodeProbability = mutateAddNodeProbability;
        backupMutateOnlyProbability = mutateOnlyProbability;
        backupMutateDeleteLinkProbability = mutateDeleteLinkProbability;
        backupMutateWeightProbability = mutateWeightProbability;
        backupMutateActivationProbability = mutateActivationProbability;

    }

    /**
     *
     */
    public void switchToSimplifying() {
        mutateAddLinkProbability = 0;
        mutateAddNodeProbability = 0;
        mutateOnlyProbability = 1;
        mutateDeleteLinkProbability = mutateDeleteLinkProbabilitySimplify;
        mutateWeightProbability = mutateWeightProbabilitySimplify;
        mutateActivationProbability = mutateActivationProbabilitySimplify;
    }

    /**
     *
     */
    public void switchToComplexifying() {
        mutateAddLinkProbability = backupMutateAddLinkProbability;
        mutateAddNodeProbability = backupMutateAddNodeProbability;
        mutateOnlyProbability = backupMutateOnlyProbability;
        mutateDeleteLinkProbability = backupMutateDeleteLinkProbability;
        mutateWeightProbability = backupMutateWeightProbability;
        mutateActivationProbability = backupMutateActivationProbability;
    }

    /**
     *
     * @param configFile
     */
    private void loadConfigurations(String configFile) {

        // Get the root path
        // String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
        //         .getResource("")).getPath();

        // Load the configurations file from the root folder
        try {
            configs.load(new FileInputStream(configFile));
        } catch (Exception exception) {
            System.err.println("Problem loading configuration file");
            exception.printStackTrace();
        }
    }

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

    public double mutateActivationRate() {
        return mutateActivationRate;
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

    public double linksBetweenHiddenNodesRate() {
        return linksBetweenHiddenNodesRate;
    }

    public double hiddenLoopLinksRate() {
        return hiddenLoopLinksRate;
    }

    public double outputLoopLinksRate() {
        return outputLoopLinksRate;
    }

    public double outputToHiddenLinksRate() {
        return outputToHiddenLinksRate;
    }

    public double outputToOutputLinksRate() {
        return outputToOutputLinksRate;
    }

    public double hiddenToHiddenBackwardLinksRate() {
        return hiddenToHiddenBackwardLinksRate;
    }

    public double hiddenToHiddenSameLevelLinksRate() {
        return hiddenToHiddenSameLevelLinksRate;
    }

    public void incrementCompatibilityThresholdBy(double value) {
        compatibilityThreshold += value;
    }

    public void setCompatibilityThreshold(double compatibilityThreshold) {
        this.compatibilityThreshold = compatibilityThreshold;
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

    public boolean phasedSearch() {
        return phasedSearch;
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

    public boolean absoluteThreshold() {
        return absoluteThreshold;
    }

    @Override
    public String toString() {
        return "NEATConfig{" +
                "numInput=" + numInput +
                ", numOutput=" + numOutput +
                ", connectionProbability=" + connectionProbability +
                ", includeBias=" + includeBias +
                ", biasConnectionProbability=" + biasConnectionProbability +
                ", populationSize=" + populationSize +
                ", defaultActivationType=" + defaultActivationType +
                ", weightRangeMin=" + weightRangeMin +
                ", weightRangeMax=" + weightRangeMax +
                ", linkTypeFiltering=" + linkTypeFiltering +
                ", linksBetweenHiddenNodesRate=" + linksBetweenHiddenNodesRate +
                ", hiddenLoopLinksRate=" + hiddenLoopLinksRate +
                ", outputLoopLinksRate=" + outputLoopLinksRate +
                ", outputToHiddenLinksRate=" + outputToHiddenLinksRate +
                ", outputToOutputLinksRate=" + outputToOutputLinksRate +
                ", hiddenToHiddenBackwardLinksRate=" + hiddenToHiddenBackwardLinksRate +
                ", hiddenToHiddenSameLevelLinksRate=" + hiddenToHiddenSameLevelLinksRate +
                ", unmatchedCoeff=" + unmatchedCoeff +
                ", weightDiffCoeff=" + weightDiffCoeff +
                ", activationDiffCoeff=" + activationDiffCoeff +
                ", compatibilityThreshold=" + compatibilityThreshold +
                ", aimForSpeciesNumber=" + aimForSpeciesNumber +
                ", speciesNumberTarget=" + speciesNumberTarget +
                ", maxSpeciesStaleness=" + maxSpeciesStaleness +
                ", maxPopulationStaleness=" + maxPopulationStaleness +
                ", parentsSurvivalThreshold=" + parentsSurvivalThreshold +
                ", elitismInSpecies=" + elitismInSpecies +
                ", mutateOnlyProbability=" + mutateOnlyProbability +
                ", mateOnlyProbability=" + mateOnlyProbability +
                ", mateAveragingProbability=" + mateAveragingProbability +
                ", mateKeepGeneDisabledProbability=" + mateKeepGeneDisabledProbability +
                ", mutateAddNodeProbability=" + mutateAddNodeProbability +
                ", mutateAddNodeOldLinksPriority=" + mutateAddNodeOldLinksPriority +
                ", mutateAddLinkProbability=" + mutateAddLinkProbability +
                ", mutateWeightProbability=" + mutateWeightProbability +
                ", capWeights=" + capWeights +
                ", mutateToggleEnableProbability=" + mutateToggleEnableProbability +
                ", mutateReEnableProbability=" + mutateReEnableProbability +
                ", mutateActivationProbability=" + mutateActivationProbability +
                ", weightPerturbationStrength=" + weightPerturbationStrength +
                ", mutateActivationRate=" + mutateActivationRate +
                ", fixDanglingNodes=" + fixDanglingNodes +
                ", fixDanglingNodesStrict=" + fixDanglingNodesStrict +
                ", danglingRemoveProbability=" + danglingRemoveProbability +
                ", allowedActivations='" + allowedActivations + '\'' +
                ", phasedSearch=" + phasedSearch +
                ", mutateDeleteLinkProbability=" + mutateDeleteLinkProbability +
                ", mutateDeleteLinkProbabilitySimplify=" + mutateDeleteLinkProbabilitySimplify +
                ", mutateWeightProbabilitySimplify=" + mutateWeightProbabilitySimplify +
                '}';
    }





}
