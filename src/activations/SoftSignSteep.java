package activations;

/**
 * @author Acemad
 */
public class SoftSignSteep extends ActivationFunction {

    @Override
    public double apply(double value) {
        return (0.5 + 0.5 * (value / (0.2 + Math.abs(value))));
    }

    @Override
    public String shortCode() {
        return "sgs";
    }
}
