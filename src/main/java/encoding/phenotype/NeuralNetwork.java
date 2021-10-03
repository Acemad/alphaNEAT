package encoding.phenotype;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;

import java.io.Serializable;
import java.util.*;

/**
 * The phenotype representation of a Genome. This class constructs a neural network out of the data in a given Genome.
 * @author Acemad
 */
public class NeuralNetwork implements Serializable {

    // Maps neuron id to a NeuralNode
    Map<Integer, NeuralNode> neurons = new HashMap<>();

    List<NeuralNode> inputNeurons = new ArrayList<>();
    List<NeuralNode> hiddenNeurons = new ArrayList<>();
    List<NeuralNode> outputNeurons = new ArrayList<>();
    NeuralNode bias;

    List<NeuralLink> neuralLinks = new ArrayList<>();

    /**
     * Constructs a NeuralNetwork object of the given Genome
     * @param genome Genome to use as an input
     */
    public NeuralNetwork(Genome genome) {

        // Convert input NodeGenes to NeuralNodes, add to inputNeurons and neurons
        for (NodeGene inputNodeGene : genome.getInputNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(inputNodeGene);
            inputNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }

        // Create the bias NeuralNode if it exists in the Genome
        if (genome.getBiasNodeGene() != null) {
            bias = new NeuralNode(genome.getBiasNodeGene());
            neurons.put(bias.getId(), bias);
        }

        // Convert hidden NodeGenes to NeuralNodes, add to hiddenNeurons and neurons
        for (NodeGene hiddenNodeGene : genome.getHiddenNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(hiddenNodeGene);
            hiddenNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }

        // Convert output NodeGenes to NeuralNodes, add to outputNeurons and neurons
        for (NodeGene outputNodeGene : genome.getOutputNodeGenes()) {
            NeuralNode neuralNode = new NeuralNode(outputNodeGene);
            outputNeurons.add(neuralNode);
            neurons.put(neuralNode.getId(), neuralNode);
        }

        // Convert enabled LinkGenes to NeuralLinks and add to neuralLinks list
        for (LinkGene linkGene : genome.getLinkGenes())
            if (linkGene.isEnabled())
                neuralLinks.add(new NeuralLink(linkGene));

        // Build the interconnections.
        // First set up the links with the correct source and destination nodes
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

    /**
     * Activates the networks using the given input, for a number of passes. Activation is done by calculating the
     * activation value of each hidden and output neuron. The output neurons values will contain the final output of
     * the network
     * @param input An array that contains the desired input, should be the same size as the number of input neurons
     * @param passes How many passes should we make throughout the network
     */
    public void activate(double[] input, int passes) {

        // input size check
        if (input.length != inputNeurons.size()) {
            System.err.println("Input data size doesn't match the input layer of the neural network");
            return;
        }

        // Setting up the input neurons with the values given in the input array
        int i = 0;
        for (NeuralNode inputNeuron : inputNeurons)
            inputNeuron.setValue(input[i++]);

        // Create a list of activable neurons, consisting of hidden and output neurons
        List<NeuralNode> activableNeurons = new ArrayList<>(hiddenNeurons);
        activableNeurons.addAll(outputNeurons);

        // Sort the activable neurons according to the number of inactive incoming links, neurons without inactive
        // incoming links come first
        activableNeurons.sort(Comparator.comparingInt(NeuralNode::getInactiveIncomingLinks));

        // For a given number of passes, activate the network by iterating through each activable neuron and calculating
        // the activation
        for (i = 0; i < passes; i++)
            for (NeuralNode activableNeuron : activableNeurons)
                 activableNeuron.activate();
    }

    /**
     * Returns the values in the output neurons as an array
     * @return A double array containing output neurons values
     */
    public double[] getOutputValue() {

        // The output array
        double[] output = new double[outputNeurons.size()];

        // Copy output values to the array
        int i = 0;
        for (NeuralNode outputNeuron : outputNeurons)
            output[i++] = outputNeuron.getValue();
        return output;
    }

    @Override
    public String toString() {
        return "(NeuralNetwork:\n" + neuronsToString(new ArrayList<>(neurons.values()), true) + ')';
    }

    /**
     * Outputs a String representation of the given NeuralNode list.
     * @param neuralNodes A list of NeuralNodes
     * @param sort If true, the list of NeuralNodes is sorted beforehand
     * @return A String representing the list of NeuralNodes
     */
    public static String neuronsToString(List<NeuralNode> neuralNodes, boolean sort) {
        StringBuilder stringBuilder = new StringBuilder();
        if (sort) neuralNodes.sort(null);
        for (NeuralNode neuralNode : neuralNodes)
            stringBuilder.append(neuralNode).append("\n");
        return stringBuilder.toString();
    }

}
