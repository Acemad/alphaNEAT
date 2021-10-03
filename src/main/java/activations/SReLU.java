package activations;

public class SReLU extends ActivationFunction {

    @Override
    public double apply(double value) {
        double l = 0.001, r = 0.999, g = 0.00001;
        if (value <= l)
            return l + (value - l) * g;
        else if (value < r)
            return value;
        else
            return r + (value - r) * g;
    }

    @Override
    public String shortCode() {
        return "sr";
    }
}
