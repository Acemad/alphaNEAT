package activations;

public class Tanh extends ActivationFunction {
    @Override
    public double apply(double value) {
        // (e^x – e^-x) / (e^x + e^-x)
        return (Math.exp(value) - Math.exp(-value)) / (Math.exp(value) + Math.exp(-value));
    }

    @Override
    public ActivationFunction newCopy() {
        return new Tanh();
    }
}
