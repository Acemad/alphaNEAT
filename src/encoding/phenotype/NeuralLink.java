package encoding.phenotype;

import encoding.LinkGene;

public class NeuralLink {

    private int id;
    private double weight;
    private int sourceNodeId;
    private int destinationNodeId;

    private NeuralNode sourceNode;
    private NeuralNode destinationNode;

    public NeuralLink(LinkGene linkGene) {
        this.id = linkGene.getId();
        this.weight = linkGene.getWeight();
        this.sourceNodeId = linkGene.getSourceNodeId();
        this.destinationNodeId = linkGene.getDestinationNodeId();
    }

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNode(NeuralNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setDestinationNode(NeuralNode destinationNode) {
        this.destinationNode = destinationNode;
    }

    public NeuralNode getSourceNode() {
        return sourceNode;
    }

    public NeuralNode getDestinationNode() {
        return destinationNode;
    }

    public double getWeight() {
        return weight;
    }
}
