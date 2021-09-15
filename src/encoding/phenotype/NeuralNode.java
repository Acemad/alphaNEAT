package encoding.phenotype;

import activations.ActivationFunction;
import encoding.NodeGene;
import encoding.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * The phenotype representation of a NodeGene.
 * @author Acemad
 */
public class NeuralNode implements Comparable<NeuralNode> {

    private final int id;
    private final NodeType type;
    private final ActivationFunction activationFunction;

    private double value; // Value of the node upon activation

    // In contrast with NodeGene, NeuralNode stores the list of incoming and outgoing links in order to form the
    // topology of the network
    List<NeuralLink> incomingLinks = new ArrayList<>();
    List<NeuralLink> outgoingLinks = new ArrayList<>();

    // This stores the number of inactive incoming links. An inactive link is a link that has a hidden node or
    // an output node as a source node. We use this value to sort the nodes before activation, starting with the node
    // with minimal inactive incoming links.
    private int inactiveIncomingLinks = 0;

    /**
     * Create a NeuralNode from a NodeGene. Copies the id, the type and the activation function. Also initializes the
     * value of the node depending on type.
     * @param nodeGene
     */
    public NeuralNode(NodeGene nodeGene) {

        this.id = nodeGene.getId();
        this.type = nodeGene.getType();

        // A BIAS node has a default value of 1, the rest of the nodes are initialized with 0
        if (this.type == NodeType.BIAS)
            this.value = 1;
        else
            this.value = 0;

        // Copying the activation function.
        this.activationFunction = nodeGene.getActivationFunction();
    }

    /**
     * Activate this node by calculating the sum of the incoming activations times link weight and applying
     * the activation function. The result of the activation is stored in the value field.
     */
    public void activate() {

        // Compute sum of incoming activations
        double sum = 0;
        for (NeuralLink incomingLink : incomingLinks)
            sum += incomingLink.getWeight() * incomingLink.getSourceNode().getValue();
        this.value = activationFunction.apply(sum); // Apply the activation function on the sum

        // Decrease the count of inactive incoming links for all destination nodes, since one inactive source is
        // now activated.
        for (NeuralLink outgoingLink : outgoingLinks)
            if (outgoingLink.getDestinationNode().getInactiveIncomingLinks() > 0)
                outgoingLink.getDestinationNode().decreaseInactiveIncomingLinks();
    }

    /**
     * Add an incoming link to the node's list of incoming links
     * @param neuralLink The incoming NeuralLink
     */
    public void addIncomingLink(NeuralLink neuralLink) {

        // Increment the inactive incoming links counter for incoming Hidden and Output links
        if (neuralLink.getSourceNode().getType() == NodeType.HIDDEN ||
                neuralLink.getSourceNode().getType() == NodeType.OUTPUT)
            inactiveIncomingLinks++;

        incomingLinks.add(neuralLink);
    }

    /**
     * Add an outgoing link to the node's list of outgoing links
     * @param neuralLink The outgoing NeuralLink
     */
    public void addOutgoingLink(NeuralLink neuralLink) {
        outgoingLinks.add(neuralLink);
    }

    /**
     * Decreases the number of inactive incoming links by one
     */
    public void decreaseInactiveIncomingLinks() {
        this.inactiveIncomingLinks--;
    }

    @Override
    public String toString() {
        return "(" +
                id +
                ", " + type +
                ", " + (activationFunction != null ? activationFunction.getClass().getSimpleName() : "NA") +
                ", " + value + ", IIL:" + inactiveIncomingLinks + ",\n\t" +
                "incoming:" + nodeLinksToString(incomingLinks) + "\n\t"+
                "outgoing:" + nodeLinksToString(outgoingLinks) + "\n)";
    }

    /**
     * Receives a list of NeuralLinks and return a String representation of the list
     * @param links A list of NeuralLink instances
     * @return A String representation of the given links
     */
    private String nodeLinksToString(List<NeuralLink> links) {
        StringBuilder stringBuilder = new StringBuilder();
        for (NeuralLink link : links) {
            //Eg. 1->3, 1->5
            stringBuilder.append(link.getSourceNodeId()).append("->").append(link.getDestinationNodeId());
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    /**
     * Implementation of the Comparable interface. Compares by node id.
     * @param neuralNode Second node
     * @return Compare result
     */
    @Override
    public int compareTo(NeuralNode neuralNode) {
        return Integer.compare(id, neuralNode.id);
    }

    public int getInactiveIncomingLinks() {
        return inactiveIncomingLinks;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public NodeType getType() {
        return type;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
