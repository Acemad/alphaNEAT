package encoding;

import activations.ActivationFunction;
import activations.Sigmoid;

public class NodeGene implements Comparable<NodeGene> {

    private final int id;
    private final NodeType type;
    ActivationFunction activationFunction;

    public NodeGene(int id, NodeType type) {
        this.id = id;
        this.type = type;
        if (type != NodeType.INPUT && type != NodeType.BIAS)
            activationFunction = new Sigmoid();
    }

    public NodeGene(NodeGene nodeGene) { // Copy constructor
        this.id = nodeGene.id;
        this.type = nodeGene.type;

        if (nodeGene.activationFunction != null)
            this.activationFunction = nodeGene.activationFunction.newCopy();
        else
            this.activationFunction = null;
    }

    @Override
    public String toString() {
        return "(" +
                id +
                ", " + type +
                ", " + (activationFunction != null ? activationFunction.getClass().getSimpleName() : "NA") +
                ')';
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(NodeGene nodeGene) {
        return Integer.compare(id, nodeGene.id);
    }
}
