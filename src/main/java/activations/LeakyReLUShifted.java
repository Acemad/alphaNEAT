package activations;

public class LeakyReLUShifted extends ActivationFunction {


    @Override
    public double apply(double value) {
        return (new LeakyReLU()).apply(value + 0.5);
    }

    @Override
    public String shortCode() {
        return "lrs";
    }
}
