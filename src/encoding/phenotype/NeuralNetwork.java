package encoding.phenotype;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;

import java.util.*;
import java.util.function.IntFunction;

public class NeuralNetwork {

    Map<Integer, NeuralNode> neurons = new HashMap<>();
    List<NeuralNode> inputNeurons = new ArrayList<>();
    List<NeuralNode> hiddenNeurons = new ArrayList<>();
    List<NeuralNode> outputNeurons = new ArrayList<>();
    NeuralNode bias;

    List<NeuralLink> neuralLinks = new ArrayList<>();

    public NeuralNetwork(Genome genome) {
        for (NodeGene inputNodeGene : genome.getInputNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(inputNodeGene);
            inputNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }

        if (genome.getBiasNodeGene() != null) {
            bias = new NeuralNode(genome.getBiasNodeGene());
            neurons.put(bias.getId(), bias);
        }

        for (NodeGene hiddenNodeGene : genome.getHiddenNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(hiddenNodeGene);
            hiddenNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }

        for (NodeGene outputNodeGene : genome.getOutputNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(outputNodeGene);
            outputNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }
        
        for (LinkGene linkGene : genome.getLinkGenes())
            if (linkGene.isEnabled())
                neuralLinks.add(new NeuralLink(linkGene));

        // Build the interconnections. First set up the links with the correct source and destination nodes
        for (NeuralLink neuralLink : neuralLinks) {
            neuralLink.setSourceNode(neurons.get(neuralLink.getSourceNodeId()));
            neuralLink.setDestinationNode(neurons.get(neuralLink.getDestinationNodeId()));
        }

        // Second, set up the neurons with the correct outgoing and incoming links
        for (NeuralLink neuralLink : neuralLinks) {
            neurons.get(neuralLink.getSourceNodeId()).addOutgoingLink(neuralLink);
            neurons.get(neuralLink.getDestinationNodeId()).addIncomingLink(neuralLink);
        }
    }

    public void activate(double[] input, int passes) {

        if (input.length != inputNeurons.size()) {
            System.err.println("Input data size doesn't match the input layer of the neural network");
            return;
        }

        int i = 0;
        for (NeuralNode inputNeuron : inputNeurons) {
            inputNeuron.setValue(input[i++]);
        }

        List<NeuralNode> activableNeurons = new ArrayList<>(hiddenNeurons);
        activableNeurons.addAll(outputNeurons);
        activableNeurons.sort(Comparator.comparingInt(NeuralNode::getInactiveIncomingLinks));
        System.out.println("Sorted: \n"  + neuronsToString(activableNeurons, false));

        for (i = 0; i < passes; i++) { // TODO Rewrite to enable re-sorting after each node activation
            for (NeuralNode activableNeuron : activableNeurons)
                 activableNeuron.activate();
            System.out.println("Pass " + i + ": " + Arrays.toString(getOutputValue()));
        }
    }

    public double[] getOutputValue() {
        double[] output = new double[outputNeurons.size()];
        int i = 0;
        for (NeuralNode outputNeuron : outputNeurons)
            output[i++] = outputNeuron.getValue();
        return output;
    }



    @Override
    public String toString() {
        return "(NeuralNetwork:\n" + neuronsToString(new ArrayList<>(neurons.values()), true) + ')';
    }

    public static String neuronsToString(List<NeuralNode> neuralNodes, boolean sort) {
        StringBuilder stringBuilder = new StringBuilder();
        if (sort) neuralNodes.sort(null);
        for (NeuralNode neuralNode : neuralNodes)
            stringBuilder.append(neuralNode).append("\n");
        return stringBuilder.toString();
    }

    public List<NeuralNode> getInputNeurons() {
        return inputNeurons;
    }
}
