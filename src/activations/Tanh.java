package activations;

/**
 * Tanh Activation function
 * @author Acemad
 */
public class Tanh extends ActivationFunction {

    /**
     * Apply the Tanh function on the given input
     * @param value Sum of incoming activations
     * @return
     */
    @Override
    public double apply(double value) {
        // (e^x – e^-x) / (e^x + e^-x)
        return (Math.exp(value) - Math.exp(-value)) / (Math.exp(value) + Math.exp(-value));
    }

    @Override
    public String shortCode() {
        return "t";
    }

}
