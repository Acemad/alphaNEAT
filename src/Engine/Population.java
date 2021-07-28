package Engine;

import encoding.Genome;
import innovation.Innovations;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private List<Genome> population = new ArrayList<>();
    private Innovations innovations;

    public Population(int numInput, int numOutput, double connectionProbability, boolean includeBias,
                      double biasConnectionProbability, int count) {

        innovations = new Innovations(numInput, numOutput, includeBias); // Initiate the innovations store. Sets the node count tracker/

        for (int i = 0; i < count; i++)
            population.add(new Genome(innovations, connectionProbability, biasConnectionProbability));

        // System.out.println(innovations.getAvailableLinks() + "\n" + innovations.getAvailableLinks().size());
        System.out.println("\n" + innovations.getLinksInUse());
    }

    @Override
    public String toString() {
        return "Population{" +
                "population=" + population +
                '}';
    }
}
