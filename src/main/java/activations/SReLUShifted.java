package activations;

public class SReLUShifted extends ActivationFunction {


    @Override
    public double apply(double value) {
        return (new SReLU()).apply(value + 0.5);
    }

    @Override
    public String shortCode() {
        return "srs";
    }
}
