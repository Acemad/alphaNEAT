import Engine.Population;
import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import encoding.NodeType;
import encoding.phenotype.NeuralNetwork;
import innovation.Innovations;
import operators.Crossover;
import operators.Mutation;
import util.Pair;

import java.util.*;

public class Main {

    public static void main(String[] args) {





        // Population population = new Population(5,4,0.5,true, 1, 10);
        // System.out.println("Number of Possible Links: " + Genome.numberOfPossibleLinks(5, 4, 0, true));
        // System.out.println(population + "\n");

        Innovations innovations = new Innovations(4, 2,true);

        List<Genome> pop = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
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


        /*for (int i = 0; i < 50; i++) {
            genome = Mutation.addNewLink(genome, innovations);
        }*/


        /*for (int i = 0; i < 10; i++) {
            genome = Mutation.addNewNode(genome, innovations);
        }*/
        // for (Genome gen : pop) {
            // System.out.println("All Possible Links (Calculated) - Before: " + gen.calculatePossibleLinks() + "\n");
            // gen = Mutation.addNewLink(Mutation.addNewNode(gen, innovations), innovations);
            // System.out.println("All Possible Links (Calculated) - After : " + gen.calculatePossibleLinks() + "\n");
        // }


        Genome gen1 = pop.get(0);
        gen1.setFitness(12);
        Genome gen2 = pop.get(1);
        gen2.setFitness(10);

        for (int i = 0; i < 3; i++) {
            gen1 = Mutation.addNewNode(gen1, innovations);
            gen1 = Mutation.addNewLink(gen1, innovations);
            // gen2 = Mutation.addNewLink(gen2, innovations);
        }

        for (int i = 0; i < 1; i++) {
            // gen1 = Mutation.addNewLink(gen1, innovations);
            gen2 = Mutation.addNewNode(gen2, innovations);
            gen2 = Mutation.addNewLink(gen2, innovations);
        }

        Crossover.crossover(innovations, gen1, gen2);
        NeuralNetwork neuralNetwork = new NeuralNetwork(gen2);
        System.out.println(neuralNetwork);
        neuralNetwork.activate(new double[]{1,2,1,2}, 5);
        System.out.println(Arrays.toString(neuralNetwork.getOutputValue()));
        System.out.println("Input Neurons\n" + NeuralNetwork.neuronsToString(neuralNetwork.getInputNeurons(), true));




        // System.out.println("\n" + genome.generatePossibleLinks());
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
