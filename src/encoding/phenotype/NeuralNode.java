package encoding.phenotype;

import activations.ActivationFunction;
import encoding.NodeGene;
import encoding.NodeType;

import java.util.ArrayList;
import java.util.List;

public class NeuralNode implements Comparable<NeuralNode> {

    private final int id;
    private final NodeType type;
    private final ActivationFunction activationFunction;
    private double value;

    List<NeuralLink> incomingLinks = new ArrayList<>();
    List<NeuralLink> outgoingLinks = new ArrayList<>();

    private int inactiveIncomingLinks = 0;

    public NeuralNode(NodeGene nodeGene) {

        this.id = nodeGene.getId();
        this.type = nodeGene.getType();

        if (this.type == NodeType.BIAS)
            this.value = 1;
        else
            this.value = 0;

        if (nodeGene.getActivationFunction() != null)
            this.activationFunction = nodeGene.getActivationFunction().newCopy();
        else
            this.activationFunction = null;
    }

    public void addIncomingLink(NeuralLink neuralLink) {
        if (neuralLink.getSourceNode().getType() == NodeType.HIDDEN ||
            neuralLink.getSourceNode().getType() == NodeType.OUTPUT)
            inactiveIncomingLinks++;

        incomingLinks.add(neuralLink);
    }

    public void addOutgoingLink(NeuralLink neuralLink) {
        outgoingLinks.add(neuralLink);
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

    public void activate() {
        double sum = 0;
        for (NeuralLink incomingLink : incomingLinks)
            sum += incomingLink.getWeight() * incomingLink.getSourceNode().getValue();
        this.value = sum; //TODO activationFunction.apply(sum);

        for (NeuralLink outgoingLink : outgoingLinks)
            if (outgoingLink.getDestinationNode().getInactiveIncomingLinks() > 0)
                outgoingLink.getDestinationNode().decreaseInactiveIncomingLinks();
    }

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
                "outgoing:" + nodeLinksToString(outgoingLinks) + "\n" +
                ')';
    }


    private String nodeLinksToString(List<NeuralLink> links) {
        StringBuilder stringBuilder = new StringBuilder();
        for (NeuralLink link : links) {
            stringBuilder.append(link.getSourceNodeId()).append("->").append(link.getDestinationNodeId());
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(NeuralNode neuralNode) {
        return Integer.compare(id, neuralNode.id);
    }

    public int getInactiveIncomingLinks() {
        return inactiveIncomingLinks;
    }
}
