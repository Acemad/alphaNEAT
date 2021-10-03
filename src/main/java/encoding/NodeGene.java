package encoding;

import activations.ActivationFunction;
import activations.ActivationType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Implementation of a node gene, a gene representing neural network nodes
 * @author Acemad
 */
public class NodeGene implements Comparable<NodeGene>, Serializable {

    private final int id;
    private final NodeType type;
    private ActivationType activationType;
    ActivationFunction activationFunction;

    // Level of the node in the network
    private int level;

    /**
     * Constructs a HIDDEN or OUTPUT NodeGene with a given id, NodeType, and activation type.
     * @param id id of the new NodeGene
     * @param type type of the new NodeGene, must be HIDDEN or OUTPUT
     * @param activationType type of the activation function associated with this node
     */
    public NodeGene(int id, NodeType type, ActivationType activationType) {

        this.id = id;
        this.type = type;

        // Assign an activation function only to HIDDEN and OUTPUT node types
        this.activationType = activationType;
        this.activationFunction = ActivationType.getActivationFunction(this.activationType);
    }

    /**
     * Constructs an INPUT or BIAS NodeGenes. No activation type is required
     * @param id id of the new NodeGene
     * @param type Type of the new NodeGene, must be INPUT or BIAS
     */
    public NodeGene(int id, NodeType type) {
        this(id, type, null);
    }

    /**
     * Copy constructor. Constructs a new NodeGene that is an exact copy of the given NodeGene.
     * @param nodeGene The NodeGene to copy
     */
    public NodeGene(NodeGene nodeGene) {
        this.id = nodeGene.id;
        this.type = nodeGene.type;

        // Create a new copy of the activation function instance if the nodeGene has a valid activation function
        if (nodeGene.activationType != null) {
            this.activationType = nodeGene.activationType;
            this.activationFunction = nodeGene.activationFunction.newCopy();
        }
        this.level = nodeGene.level;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + type +
                ", " + (activationFunction != null ? activationFunction.getClass().getSimpleName() : "NA") + ')';
    }

    /**
     * Constructs a concise String representation of this node gene
     * @return A concise String representation of the node gene
     */
    public String toConciseString() {
        StringBuilder builder = new StringBuilder();
        switch (type) {
            case INPUT -> builder.append("I").append(id);
            case OUTPUT -> builder.append("O").append(id).append(activationFunction.shortCode());
            case HIDDEN -> builder.append("H").append(id).append(activationFunction.shortCode());
            case BIAS -> builder.append("B").append(id);
        }
        // builder.append("[").append(String.format("%3d", (int)(level*100))).append("]");
        return builder.toString();
    }

    /**
     * Compare NodeGenes by their ids, for sorting purposes
     * @param nodeGene The node gene to compare to
     * @return The comparison results
     */
    @Override
    public int compareTo(NodeGene nodeGene) {
        return Integer.compare(id, nodeGene.id);
    }

    /**
     * Two NodeGenes are equal if their ids, types, and activation functions are equal.
     * @param obj The object to compare with
     * @return Result of the comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodeGene gene = (NodeGene) obj;
        return id == gene.id && type == gene.type /*&& Objects.equals(activationFunction, gene.activationFunction)*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type/*, activationFunction*/);
    }

    public int getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationType activationType) {
        this.activationType = activationType;
        this.activationFunction = ActivationType.getActivationFunction(this.activationType);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
