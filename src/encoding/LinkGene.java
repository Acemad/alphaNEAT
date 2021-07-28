package encoding;

import innovation.Innovations;

public class LinkGene {

    private final int id;
    private final int sourceNodeId;
    private final int destinationNodeId;
    private double weight;
    private boolean enabled = true;
    private boolean isLoop;

    public LinkGene(int sourceNodeId, int destinationNodeId, Innovations innovations) {

        this.id = innovations.newLink(sourceNodeId, destinationNodeId);
        if (this.id == -1)
            System.err.println("Illegal link created: (" + sourceNodeId + ", " + destinationNodeId + ")");

        this.sourceNodeId = sourceNodeId;
        this.destinationNodeId = destinationNodeId;
        this.weight = innovations.getRandomWeight();

        isLoop = (sourceNodeId == destinationNodeId);
    }

    // Weight constructor

    public LinkGene(LinkGene linkGene) { // Copy Constructor
        this.id = linkGene.id;
        this.sourceNodeId = linkGene.sourceNodeId;
        this.destinationNodeId = linkGene.destinationNodeId;
        this.weight = linkGene.weight;
        this.enabled = linkGene.enabled;
        this.isLoop = linkGene.isLoop;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", sourceNodeId=" + sourceNodeId +
                ", destinationNodeId=" + destinationNodeId +
                ", w=" + weight +
                (enabled ? ", enabled" : ", disabled") +
                (isLoop ? ", loop" : ", notLoop") +
                '}';
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getId() {
        return id;
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

    public void disable() {
        this.enabled = false;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
