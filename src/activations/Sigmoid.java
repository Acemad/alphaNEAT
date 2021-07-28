package activations;

public class Sigmoid extends ActivationFunction {

    @Override
    public double apply(double value) {
        return (1 / (1 + Math.exp(-4.924273 * value)));
    }

    @Override
    public ActivationFunction newCopy() {
        return new Sigmoid();
    }
}
