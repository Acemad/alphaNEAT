package activations;

import engine.PRNG;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An enum representing the different types of Activation functions available for NEAT
 * @author Acemad
 */
public enum ActivationType implements Serializable {

    // TODO Short description
    SIGMOID_STEEP,
    TANH,
    SOFTSIGN_STEEP,
    RELU,
    LEAKY_RELU,
    LEAKY_RELU_SHIFTED,
    SRELU,
    SRELU_SHIFTED,
    SOFT_PLUS,
    ELU;

    /**
     * Returns the ActivationFunction corresponding to the passed ActivationType
     * @param activationType The activation type requested
     * @return An ActivationFunction instance corresponding to the activationType
     */
    public static ActivationFunction getActivationFunction(ActivationType activationType) {

        ActivationFunction activationFunction = null;

        if (activationType != null) {

            switch (activationType) {
                case SIGMOID_STEEP -> activationFunction = new SigmoidSteep();
                case TANH -> activationFunction = new Tanh();
                case SOFTSIGN_STEEP -> activationFunction = new SoftSignSteep();
                case RELU -> activationFunction = new ReLU();
                case LEAKY_RELU -> activationFunction = new LeakyReLU();
                case LEAKY_RELU_SHIFTED -> activationFunction = new LeakyReLUShifted();
                case SRELU -> activationFunction = new SReLU();
                case SRELU_SHIFTED -> activationFunction = new SReLUShifted();
                case SOFT_PLUS -> activationFunction = new SoftPlus();
                case ELU -> activationFunction = new ELU();
            }
        }

        return activationFunction;
    }

    /**
     * Returns a random activation type from the given string of allowed activations
     *
     * @return An ActivationType from the given activations string
     */
    public static ActivationType getRandomType(String allowedActivations) {

        // The string is converted to a list of ActivationTypes.
        List<ActivationType> allowedActivationTypes = getActivationTypesFromString(allowedActivations);
        // A random activation is returned
        return allowedActivationTypes.get(PRNG.nextInt(allowedActivationTypes.size()));
    }

    /**
     * Converts a String formatted as a comma separated list of activation types to an actual list of activation types
     *
     * @param string The string to read from
     * @return A list of ActivationType enums
     */
    private static List<ActivationType> getActivationTypesFromString(String string) {

        List<ActivationType> activationTypes = new ArrayList<>();
        // Split the string by the delimiter ","
        String[] activationStrings = string.split(",");

        // Strip of whitespace and convert to ActivationType
        for (String activationString : activationStrings)
            activationTypes.add(ActivationType.valueOf(activationString.strip()));

        return activationTypes;
    }
}
