package activations;

public abstract class ActivationFunction {
    public abstract double apply(double value);
    public abstract ActivationFunction newCopy();
}
