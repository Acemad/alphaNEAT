package activations;

public class SoftPlus extends ActivationFunction {


    @Override
    public double apply(double value) {
        return Math.log(1 + Math.pow(Math.E, value));
    }

    @Override
    public String shortCode() {
        return "sp";
    }
}
