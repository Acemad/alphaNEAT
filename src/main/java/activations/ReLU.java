package activations;

public class ReLU extends ActivationFunction {

    @Override
    public double apply(double value) {
        return Math.max(0, value);
    }

    @Override
    public String shortCode() {
        return "r";
    }
}
