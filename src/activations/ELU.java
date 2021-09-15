package activations;

public class ELU extends ActivationFunction {

    @Override
    public double apply(double value) {
        return (value > 0 ? value : 0.5 * (Math.pow(Math.E, value) - 1));
    }

    @Override
    public String shortCode() {
        return "e";
    }
}
