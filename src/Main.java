import Engine.Population;
import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import encoding.NodeType;
import innovation.Innovations;
import operators.Mutation;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {





        // Population population = new Population(5,4,0.5,true, 1, 10);
        // System.out.println("Number of Possible Links: " + Genome.numberOfPossibleLinks(5, 4, 0, true));
        // System.out.println(population + "\n");

        Innovations innovations = new Innovations(3,2,false);

        List<Genome> pop = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            pop.add(new Genome(innovations, 1,1));
        }

        // System.out.println(pop);

        // Genome genome1 = new Genome(innovations, 0.5, 1);
        // System.out.println("G1\n" + genome1);

        // Genome genome2 = new Genome(innovations, 0.5, 1);
        // System.out.println("G2\n" + genome2);


        /*for (Genome genome : pop) {
            Mutation.addNewLink(genome, innovations);
        }*/

        Genome genome = pop.get(0);
        /*for (int i = 0; i < 50; i++) {
            genome = Mutation.addNewLink(genome, innovations);
        }*/


        /*for (int i = 0; i < 10; i++) {
            genome = Mutation.addNewNode(genome, innovations);
        }*/
        for (Genome gen : pop) {
            System.out.println("All Possible Links (Calculated) - Before: " + gen.calculatePossibleLinks() + "\n");
            gen = Mutation.addNewLink(Mutation.addNewNode(gen, innovations), innovations);
            System.out.println("All Possible Links (Calculated) - After : " + gen.calculatePossibleLinks() + "\n");
        }


        System.out.println("\n" + genome.generatePossibleLinks());
        // Pair<Integer, Integer> pair = new Pair<>(5,6);
        // System.out.println(pair.equals(new Pair<>(5,4)));

        // Map<Pair<Integer, Integer>, String> dict = new HashMap<>();
        // for (int i = 0; i < 100000; i++) {
        //     System.out.println("Put(" + i + ")");
        //     dict.put(new Pair<>(i, i + 1), "num:" + i);
        // }

        // System.out.println(dict.get(new Pair<>(56657, 56659)));

        // System.out.println("NodeCount: " + innovations.getNodeCount());

        // Visualizer.showGStream(mutated);
    }

}
