import activations.ActivationType;
import encoding.NodeGene;
import encoding.NodeType;
import encoding.phenotype.NeuralNetwork;
import engine.ANEAT;
import engine.NEATConfig;
import engine.PRNG;
import engine.Population;
import encoding.Genome;
import innovation.InnovationDB;
import operators.Mutation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import util.Link;

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

        // util.Visualizer.showGStream(mutated);

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
        // InnovationDB innovations = new InnovationDB(3,2,true, ActivationType.SIGMOID_STEEP, -1, 1);

        //
        // Genome g1 = new Genome(innovations, 1, 1);
        // g1 = Mutation.addNewNode(g1, innovations);
        // g1 = Mutation.addNewNode(g1, innovations);
        // g1.setFitness(0);
        // Mutation.mutateActivationType(g1, 0.5, "TANH, SIGMOID_STEEP");
        //
        // Genome g2 = new Genome(innovations, 1, 1);
        // g2 = Mutation.addNewNode(g2, innovations);
        // g2 = Mutation.addNewNode(g2, innovations);
        //
        // System.out.println("g1: " + g1.toConciseString());
        // System.out.println("g2: " + g2.toConciseString());
        //
        // Genome g3 = Crossover.multipointCrossover(g1, g2, new NEATConfig("configs"));
        // System.out.println("\ng3: " + g3.toConciseString());

        String parentDir = System.getProperty("user.home") + "\\Desktop\\xor\\";
        String configPath = parentDir + "configs.cfg";
        String baseName = parentDir + "xor";



        InnovationDB innovations = new InnovationDB(2,1,true, ActivationType.SIGMOID_STEEP, -1, 1);


        List<NodeGene> hiddenNodes = new ArrayList<>();
        hiddenNodes.add(new NodeGene(4, NodeType.HIDDEN, ActivationType.SIGMOID_STEEP));
        hiddenNodes.add(new NodeGene(5, NodeType.HIDDEN, ActivationType.SIGMOID_STEEP));

        List<Link> customLinks = new ArrayList<>();
        customLinks.add(new Link(0,4));
        customLinks.add(new Link(1,4));
        customLinks.add(new Link(1,5));
        customLinks.add(new Link(2,3));

        customLinks.add(new Link(4,5));
        customLinks.add(new Link(4,3));
        customLinks.add(new Link(5,3));

        Genome genome = new Genome(innovations, hiddenNodes, customLinks);

        System.out.println("Before:\n" + genome.toConciseString());

        Genome mutated = Mutation.deleteLink(genome, innovations);

        System.out.println("After: \n" + mutated.toConciseString());


        /*for (int i = 0; i < 100; i++) {
            System.out.println(PRNG.nextGaussian(0.01));
        }*/



        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < 200; i++) {
            ANEAT aneat = new ANEAT(configPath /*,baseName + "Pop-6000"*/);
            aneat.run(Main::evalXOR, 500, null);
            // System.out.println("Pop:\n" + aneat.getPopulation().toConciseString());
            System.out.println("BestGenome:\n\t " + aneat.getBestGenome().toConciseString());
            /*System.out.println("SPex\n" +
                    aneat.getEvolutionStats().getSpeciesExistenceStats().get(2));
            System.out.println("SPfi\n" +
                    aneat.getEvolutionStats().getSpeciesFitnessStats().get(2)
                            .stream().map(DescriptiveStatistics::getN).collect(Collectors.toList()));


            System.out.println("Nodes C\n" +
                    Arrays.stream(aneat.getEvolutionStats().getNodeCumulateCountStats().getValues())
                            .sequential().boxed().collect(Collectors.toList()));

            System.out.println("Links C\n" +
                    Arrays.stream(aneat.getEvolutionStats().getLinkCumulateCountStats().getValues())
                            .sequential().boxed().collect(Collectors.toList()));

            System.out.println("Add Node\n" +
                    Arrays.stream(aneat.getEvolutionStats().getAddNodeMutationsStats().getValues())
                            .sequential().boxed().collect(Collectors.toList()));

            System.out.println("GenomeIds: " + aneat.getEvolutionStats().getGenomeIds().size());

            System.out.println("SPc\n" +
                    Arrays.stream(aneat.getEvolutionStats().getSpeciesCountStats().getValues())
                            .sequential().boxed().collect(Collectors.toList()));

            System.out.println("SPcumul\n" +
                    Arrays.stream(aneat.getEvolutionStats().getSpeciesCumulateCountStats().getValues())
                            .sequential().boxed().collect(Collectors.toList()));

            System.out.println("MaxF\n" +
                    aneat.getEvolutionStats().getGenomesLinksStats().stream().map(DescriptiveStatistics::getMean)
                            .collect(Collectors.toList()));*/

            /*System.out.println("MaxFO\n" +
                    Arrays.stream(aneat.getEvolutionStats().getMeanLinksStats().getValues()).boxed()
                            .collect(Collectors.toList()));*/

            System.out.println("Mean Complexity:" + aneat.getEvolutionStats().getMeanComplexityStats().getMean());
            stats.addValue(aneat.getEvolutionStats().getMeanComplexityStats().getMean());
        }

        // NEATConfig conf = new NEATConfig(configPath);
        // System.out.println(conf);
        // conf.switchToSimplifying();
        // System.out.println("\nAfter:\n" + conf);
        // conf.switchToComplexifying();
        // System.out.println("\nAfter2:\n" + conf);


        System.out.println("stats = \n" + stats);



        /*Population population = Population.readFromFile(baseName + "Pop-1000");
        System.out.println(population.toConciseString());

        Genome genome = Genome.readFromFile(baseName+"Best-1000");
        System.out.println("genome.toConciseString() = \n" + genome.toConciseString());

        EvolutionStats stats = EvolutionStats.readFromFile(baseName + "Stats");
        System.out.println(Arrays.stream(stats.getBestGenomeNodesStats().getValues()).boxed().collect(Collectors.toList()));
        */

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
