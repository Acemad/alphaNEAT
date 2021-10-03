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
        return Math.tanh(value);
    }

    @Override
    public String shortCode() {
        return "t";
    }

}
