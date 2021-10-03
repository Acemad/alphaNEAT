package encoding;

import engine.PRNG;
import innovation.InnovationDB;

import java.io.Serializable;
import java.util.Objects;

/**
 * The implementation of a link gene, a gene that represents neural network links
 * @author Acemad
 */
public class LinkGene implements Comparable<LinkGene>, Serializable {

    private final int id;
    private final int sourceNodeId;
    private final int destinationNodeId;
    private double weight;
    private boolean enabled = true;
    private final boolean isLoop;

    /**
     * Construct a LinkGene using the ids of the source and destination nodes, and given the innovations database
     *
     * @param sourceNodeId ID of the source node
     * @param destinationNodeId ID of the destination node
     * @param innovationDB Reference to the shared innovations database
     */
    public LinkGene(int sourceNodeId, int destinationNodeId, InnovationDB innovationDB) {

        // The ID of the link is retrieved from the innovations DB. A new ID is generated only if this is the first
        // time a link is created between the given source and destination nodes, otherwise the old ID is retrieved
        this.id = innovationDB.requestLinkId(sourceNodeId, destinationNodeId);

        // Set the remaining fields
        this.sourceNodeId = sourceNodeId;
        this.destinationNodeId = destinationNodeId;

        // The weight is obtained from the innovations DB's centralised random object
        this.weight = PRNG.nextWeight(innovationDB.getWeightRangeMin(), innovationDB.getWeightRangeMax());

        isLoop = (sourceNodeId == destinationNodeId);
    }


    /**
     * Copy constructor, creates an identical copy of the given LinkGene, with a different reference
     * @param linkGene LinkGene to copy
     */
    public LinkGene(LinkGene linkGene) {
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

    /**
     * Creates a concise string representation of the LinkGene
     * @return A concise String representation
     */
    public String toConciseString() {
        return id + ":" + sourceNodeId + "->" + destinationNodeId + (enabled ? "" : "d") + (isLoop ? "l" : "") +
                String.format("[% 2.2f]", weight);
    }

    /**
     * Two LinkGenes are equal if their ids, sourceNodeIds, and destinationNodeIds are equal
     * @param o The object to compare to
     * @return comparison results
     */
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

    /**
     * We compare LinkGenes using their ids. For sorting purposes
     * @param linkGene The LinkGene to compare to
     * @return comparison results
     */
    @Override
    public int compareTo(LinkGene linkGene) {
        return Integer.compare(id, linkGene.id);
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

    public int getId() {
        return id;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public double getWeight() {
        return weight;
    }

}
