package encoding;

import innovation.Innovations;

import java.util.Objects;

public class LinkGene implements Comparable<LinkGene> {

    private final int id;
    private final int sourceNodeId;
    private final int destinationNodeId;
    private double weight;
    private boolean enabled = true;
    private final boolean isLoop;

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
        return "(" +
                id + ", " +
                sourceNodeId + "->" + destinationNodeId +
                (enabled ? ", enabled" : ", disabled") +
                (isLoop ? ", loop, " : ", notLoop, ") +
                weight +
                ')';
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

    public void enable() {
        this.enabled = true;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkGene linkGene = (LinkGene) o;
        return id == linkGene.id && sourceNodeId == linkGene.sourceNodeId && destinationNodeId == linkGene.destinationNodeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceNodeId, destinationNodeId);
    }

    @Override
    public int compareTo(LinkGene linkGene) {
        return Integer.compare(id, linkGene.id);
    }
}
