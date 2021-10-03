package engine.stats;

/**
 * A simple counter
 * @author Acemad
 */
public class Counter {

    private int value;

    public Counter() {
        this.value = 0;
    }

    public Counter(int initialValue) {
        this.value = initialValue;
    }

    public void plusOne() {
        this.value++;
    }

    public void minusOne() {
        this.value--;
    }

    public int get() {
        return value;
    }
}
