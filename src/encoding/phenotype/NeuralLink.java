package encoding.phenotype;

import encoding.LinkGene;

/**
 * Phenotype representation of the LinkGene, with only the essential data that represents a network connection
 * @author Acemad
 */
public class NeuralLink {

    private final double weight;
    private final int sourceNodeId;
    private final int destinationNodeId;

    // In contrast with LinkGene, NeuralLink stores the actual source node and destination node to construct
    // the network.
    private NeuralNode sourceNode;
    private NeuralNode destinationNode;

    /**
     * Construct a NeuralLink from a given LinkGene. Copies the essential parameters:
     * weight, sourceNodeId, destinationNodeId
     * @param linkGene A LinkGene instance
     */
    public NeuralLink(LinkGene linkGene) {
        this.weight = linkGene.getWeight();
        this.sourceNodeId = linkGene.getSourceNodeId();
        this.destinationNodeId = linkGene.getDestinationNodeId();
    }

    /**
     * Set the sourceNode to an actual NeuralNode
     * @param sourceNode Actual source node (NeuralNode)
     */
    public void setSourceNode(NeuralNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    /**
     * Set the destinationNode to an actual NeuralNode
     * @param destinationNode Actual destination node (NeuralNode)
     */
    public void setDestinationNode(NeuralNode destinationNode) {
        this.destinationNode = destinationNode;
    }

    public NeuralNode getSourceNode() {
        return sourceNode;
    }

    public NeuralNode getDestinationNode() {
        return destinationNode;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public double getWeight() {
        return weight;
    }
}
