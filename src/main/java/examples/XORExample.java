package examples;

import encoding.Genome;
import encoding.phenotype.NeuralNetwork;
import engine.ANEAT;

public class XORExample {

    public static void main(String[] args) {

        String parentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
        String configPath = parentDir + "xorConfigs.cfg";
        String baseName = parentDir + "xor";

        for (int i = 0; i < 200; i++) {
            ANEAT aneat = new ANEAT(configPath);
            aneat.run(XORExample::evalXOR, 500, null);
            System.out.println("\tBestGenome:\n\t" + aneat.getBestGenome().toConciseString());
        }
    }

    /**
     *
     * @param genome
     * @return
     */
    public static double evalXOR(Genome genome) {

        NeuralNetwork network = genome.buildNetwork();
        double fitness = 0;

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                double[] input = {i, j};
                network.activate(input, 2);
                double[] output = network.getOutputValue();
                int expected = i ^ j;
                fitness += (1 - Math.abs(expected - output[0]));
            }

        // Max fitness = 16
        return (fitness * fitness);
    }

}
