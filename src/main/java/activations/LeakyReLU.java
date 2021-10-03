package activations;

public class LeakyReLU extends ActivationFunction {

    @Override
    public double apply(double value) {
        return (value > 0 ? value : 0.001 * value);
    }

    @Override
    public String shortCode() {
        return "lr";
    }
}
