package activations;

/**
 * Abstract class defining the activation function used in neural network nodes
 * @author Acemad
 */
public abstract class ActivationFunction {

    /**
     * Takes as an input the sum of incoming activations and apply the activation function which result in
     * the activated value
     * @param value Sum of incoming activations
     * @return
     */
    public abstract double apply(double value);

    /**
     * Returns a simple short code for the activation function to use for concise representations
     * @return A simple one character representation
     */
    public abstract String shortCode();

    /**
     * Returns an identical copy of this activation function instance
     * @return An ActivationFunction copy
     */
    public ActivationFunction newCopy() {
        ActivationFunction copy = null;
        try {
            copy = this.getClass().getConstructor().newInstance();
        } catch (Exception ignored) {}
        return copy;
    }

    /**
     * Returns a String representing the name of the activation function
     * @return A String representation of the activation function
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Compares activation functions by the name of their classes.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ActivationFunction activationFunction = (ActivationFunction) obj;
        return this.toString().equals(activationFunction.toString());
    }
}
