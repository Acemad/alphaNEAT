import activations.ActivationType;
import encoding.phenotype.NeuralNetwork;
import engine.ANEAT;
import engine.NEATConfig;
import engine.NRandom;
import engine.Population;
import encoding.Genome;
import innovation.InnovationDB;
import operators.Crossover;
import operators.Mutation;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Population population = new Population(5,4,0.5,true, 1, 10);
        // System.out.println("Number of Possible Links: " + Genome.numberOfPossibleLinks(5, 4, 0, true));
        // System.out.println(population + "\n");

        // Last Tests ///////////////////////////////////////////////////////////
        /*Innovations innovations = new Innovations(4, 2,true);

        List<Genome> pop = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            pop.add(new Genome(innovations, 1,1));
        }*/

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


        /* Last Tests ///////////////////////////////////////////////////////
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

        Genome gen3 = Crossover.crossover(innovations, gen1, gen2);
        NeuralNetwork neuralNetwork = new NeuralNetwork(gen2);
        System.out.println(neuralNetwork);
        neuralNetwork.activate(new double[]{1,2,1,2}, 5);
        System.out.println(Arrays.toString(neuralNetwork.getOutputValue()));
        System.out.println("Input Neurons\n" + NeuralNetwork.neuronsToString(neuralNetwork.getInputNeurons(), true));

        System.out.println("Compat: " + gen1.isCompatibleWith(gen2, 1,1, 1));*/


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

        // Innovations innovations = new Innovations(3,1,true);

        // Genome genome = new Genome(innovations, 1, 1);
        // System.out.println("Genome =\n" + genome.toConciseString());

        // genome = Species.mutate(genome, innovations);
        // System.out.println("Mutated genome =\n" + genome.toConciseString());

        // genome = Mutation.addNewNode(genome, innovations);
        // System.out.println("Add Node 1 =\n" + genome.toConciseString());

        // genome = Mutation.addNewNode(genome, innovations);
        // System.out.println("Add Node 2 =\n" + genome.toConciseString());

        // for (int i = 0; i < 5; i++)
        //     genome = Mutation.addNewLink(genome, innovations);

        // System.out.println("Add Link 5 =\n" + genome.toConciseString());



        // genome = Mutation.mutateReEnable(genome, innovations);
        // System.out.println("ReEnable 1 =\n" + genome.toConciseString());

        // genome = Mutation.mutateReEnable(genome, innovations);
        // System.out.println("ReEnable 2 =\n" + genome.toConciseString());

        // ActivationFunction function = new Sigmoid();
        // ActivationFunction copy = function.newCopy();
        //
        // System.out.println("function = " + (function));
        // System.out.println("copy = " + (copy));
        // System.out.println("Equals = " + Objects.equals(function,copy));


        // System.out.println("Path: " + Thread.currentThread().getContextClassLoader().getResource("").getPath());

        // Crossover Test:
        /*InnovationDB innovations = new InnovationDB(3,2,true, ActivationType.SIGMOID, -1, 1);

        Genome g1 = new Genome(innovations, 1, 1);
        g1 = Mutation.addNewNode(g1, innovations);
        g1 = Mutation.addNewNode(g1, innovations);
        g1.setFitness(0);
        Mutation.mutateActivationType(g1, innovations, 0.5);

        Genome g2 = new Genome(innovations, 1, 1);
        g2 = Mutation.addNewNode(g2, innovations);
        g2 = Mutation.addNewNode(g2, innovations);

        System.out.println("g1: " + g1.toConciseString());
        System.out.println("g2: " + g2.toConciseString());

        Genome g3 = Crossover.multipointCrossover(g1, g2, innovations, new NEATConfig("configs"));
        System.out.println("\ng3: " + g3.toConciseString());*/

        /*InnovationDB innovations = new InnovationDB(3,2,
                true, ActivationType.SIGMOID, 2, 5);
        for (int i = 0; i < 1000; i++) {
            double rnd = NRandom.getRandomWeight();
            if (rnd >= 2 && rnd < 5)
                System.out.println( i + " rnd = " + rnd);
            else
                System.err.println("!!! rnd = " + rnd);

        }*/


        for (int i = 0; i < 20; i++) {
            ANEAT aneat = new ANEAT("configs");
            aneat.run(Main::evalXOR, 1000);
            System.out.println("BestGenome:\n\t " + aneat.getBestGenome().toConciseString());
        }


        // evolve(1);
    }

    public static void evolve(int numGenerations) {

        // Initialize population
        Population population = new Population(2,1,1, true, 1, ActivationType.SIGMOID_STEEP, -1,1, 100);

        // System.out.println("population Species Before: \n" + population.getAllSpeciesConciseStr());
        // Evaluate population
        System.out.println("Initial Population: \n" + population.toConciseString());
        for (int i = 0; i < 1000; i++) {
            // System.out.println("############### Generation " + i);
            population.evolve(Main::evalXOR, null);
            // System.out.println("Population: \n" + population.toConciseString());
            System.out.println("Gen: " + i + " Top Fitness: " + population.getTopFitness() +
                    " Species: " + population.getSpeciesCount() + " Population: " + population.getPopulationSize());
        }

        System.out.println("population = " + population.getBestGenome().toConciseString());




        // Process weak genomes
        // Staleness processing
        // Spawn quantities
        // Breeding new generation

        // System.out.println("Final: \n" + population.toConciseString());
        // System.out.println("population Species After: \n" + population.getAllSpeciesConciseStr());


    }

    public static double eval(Genome genome) {
        try {Thread.sleep(((new Random()).nextInt(2) + 1) * 1000);}
        catch (Exception ignored) {}
        //genome.setFitness((int)Math.ceil(Math.random() * 9));
        System.out.println("Genome " + genome.getId() + " Done, Thread: " + Thread.currentThread().getName());
        return (int) Math.ceil(Math.random() * 9);
    }

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
        // System.out.println("fitness = " + fitness);
        return (fitness * fitness);
    }

}
