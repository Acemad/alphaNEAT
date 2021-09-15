package engine;

import activations.ActivationType;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class NEATConfig {

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

    private double unmatchedCoeff;
    private double weightDiffCoeff;
    private double activationDiffCoeff;
    private double compatibilityThreshold;

    private int maxSpeciesStaleness;

    private int maxPopulationStaleness;

    private double parentsSurvivalThreshold;

    private boolean elitismInSpecies;
    private double mutateOnlyProbability;
    private double mateOnlyProbability;
    private double mateAveragingProbability;
    private double mateKeepGeneDisabledProbability;

    private double mutateAddNodeProbability;
    private double mutateAddLinkProbability;
    private double mutateWeightProbability;
    private double mutateToggleEnableProbability;
    private double mutateReEnableProbability;
    private double mutateActivationProbability;

    private double weightPerturbationStrength;
    private double mutateActivationRate;

    private boolean fixDanglingNodes;
    private double danglingRemoveProbability;

    private String allowedActivations;

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
        defaultActivationType = ActivationType.valueOf(configs.getProperty("defaultActivationType"));
        weightRangeMin = Double.parseDouble(configs.getProperty("weightRangeMin"));
        weightRangeMax = Double.parseDouble(configs.getProperty("weightRangeMax"));

        maxSpeciesStaleness = Integer.parseInt(configs.getProperty("maxSpeciesStaleness"));

        maxPopulationStaleness = Integer.parseInt(configs.getProperty("maxPopulationStaleness"));

        parentsSurvivalThreshold = Double.parseDouble(configs.getProperty("parentsSurvivalThreshold"));

        elitismInSpecies = Boolean.parseBoolean(configs.getProperty("elitismInSpecies"));
        mutateOnlyProbability = Double.parseDouble(configs.getProperty("mutateOnlyProbability"));
        mateOnlyProbability = Double.parseDouble(configs.getProperty("mateOnlyProbability"));
        mateAveragingProbability = Double.parseDouble(configs.getProperty("mateAveragingProbability"));
        mateKeepGeneDisabledProbability = Double.parseDouble(configs.getProperty("mateKeepGeneDisabledProbability"));

        mutateAddNodeProbability = Double.parseDouble(configs.getProperty("mutateAddNodeProbability"));
        mutateAddLinkProbability = Double.parseDouble(configs.getProperty("mutateAddLinkProbability"));
        mutateWeightProbability = Double.parseDouble(configs.getProperty("mutateWeightProbability"));
        mutateToggleEnableProbability = Double.parseDouble(configs.getProperty("mutateToggleEnableProbability"));
        mutateReEnableProbability = Double.parseDouble(configs.getProperty("mutateReEnableProbability"));
        mutateActivationProbability = Double.parseDouble(configs.getProperty("mutateActivationProbability"));

        weightPerturbationStrength = Double.parseDouble(configs.getProperty("weightPerturbationStrength"));
        mutateActivationRate = Double.parseDouble(configs.getProperty("mutateActivationRate"));

        fixDanglingNodes = Boolean.parseBoolean(configs.getProperty("fixDanglingNodes"));
        danglingRemoveProbability = Double.parseDouble(configs.getProperty("danglingRemoveProbability"));
        allowedActivations = configs.getProperty("allowedActivations");
    }

    private void loadConfigurations(String configFile) {

        // Get the root path
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("")).getPath();

        // Load the configurations file from the root folder
        try {
            configs.load(new FileInputStream(rootPath + configFile));
        } catch (Exception exception) {
            System.err.println("Problem loading configuration file");
            exception.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "NEATConfig{\n" + configs + '}';
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
}