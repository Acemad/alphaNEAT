package innovation;

import activations.ActivationType;
import util.Link;

import java.io.Serializable;
import java.util.*;

/**
 * The innovation database: holds the ids of nodes and links found throughout the evolution process, in order to
 * prevent the duplication of the same structural innovation through multiple different ids. Also keeps track of the
 * number of Genomes and Species.
 *
 * @author Acemad
 */
public class InnovationDB implements Serializable {

    // Node and Link counters
    private int nodeCount = 0;
    private int linkCount = 0;

    // Keep track of genomes and species count to assign their ids.
    private int genomeCount;
    private int speciesCount;

    // Nodes Ids, by type
    private final Set<Integer> inputNodeIds = new HashSet<>();
    private final int biasNodeId;
    private final Set<Integer> outputNodeIds = new HashSet<>();
    private final Set<Integer> hiddenNodeIds = new HashSet<>();

    // Links currently in use, maps a pair of node ids (source and destination) to an innovation id
    private final Map<Link, Integer> existingLinks = new HashMap<>();

    // The links that were interrupted by a new node. Maps the old interrupted link to a list of node ids interrupting
    // the link.
    // TODO Contrib: The same link can be interrupted by more than one node.
    // We store the ids of the interrupting nodes as a list
    private final Map<Link, List<Integer>> interruptedLinks = new HashMap<>();

    // The type of activation function to use for new nodes
    private final ActivationType defaultActivationType;

    private final double weightRangeMin;
    private final double weightRangeMax;

    /**
     * Create the innovation database using the given basic network parameters.
     *
     * @param numInput Number of input nodes
     * @param numOutput Number of output nodes
     * @param includeBias Presence/Absence of bias node
     * @param defaultActivationType Type of the default activation function
     */
    public InnovationDB(int numInput, int numOutput, boolean includeBias, ActivationType defaultActivationType,
                        double weightRangeMin, double weightRangeMax) {

        // Assign ids to input nodes
        for (int i = 0; i < numInput; i++) inputNodeIds.add(nodeCount++);
        // Assign id to bias node
        if (includeBias) biasNodeId = nodeCount++; else biasNodeId = -1;
        // Assign ids to output nodes
        for (int i = 0; i < numOutput; i++) outputNodeIds.add(nodeCount++);
        // Assign default activation type
        this.defaultActivationType = defaultActivationType;
        // Assign maximum and minimum link weights
        this.weightRangeMin = weightRangeMin;
        this.weightRangeMax = weightRangeMax;
        // initialize static genome and species counters
        genomeCount = 0;
        speciesCount = 0;
    }

    /**
     * Retrieve the id of a given link (source -> destination) or create a new link id if the link does not exist
     *
     * @param sourceNodeId Ths source node of the link
     * @param destinationNodeId The destination node of the link
     * @return Id of the new or existing link.
     */
    public int requestLinkId(int sourceNodeId, int destinationNodeId) {

        Link newLink = new Link(sourceNodeId, destinationNodeId);
        // Check if the new link is already in use.
        Integer id = existingLinks.get(newLink);

        // The link is not in use
        if (id == null) {
            id = linkCount++; // Assign an id (the innovation id)
            existingLinks.put(newLink, id); // Add to the links in use
        }

        // Return the new id, (or, in case the link was in use, the current id)
        return id;
    }

    /**
     * Retrieves a node id for the node interrupting the given link (source -> destination). This method supports the
     * interruption of the same link multiple times, for example in case a disabled link becomes enabled again and
     * a node is requested to be added in its place for a second time. Ids of interrupting nodes are saved in a list
     * as part of a map that maps each interrupted link to the interrupting nodes list.
     *
     * @param sourceNodeId Id of the source node
     * @param destinationNodeId Id of the destination node
     * @param genomeNodeIds Node Ids present in the genome in question
     * @return An integer representing the id of the node to add
     */
    public int requestInterruptingNodeId(int sourceNodeId, int destinationNodeId, Set<Integer> genomeNodeIds) {

        // The interrupted link
        Link interruptedLink = new Link(sourceNodeId, destinationNodeId);

        // Check if the link was previously interrupted
        List<Integer> interruptingNodeIds = interruptedLinks.get(interruptedLink);

        if (interruptingNodeIds == null) { // The link was not interrupted previously
            // The link is interrupted for the first time, create the list that holds interrupting nodes,
            // and add the new node
            interruptingNodeIds = new ArrayList<>();
            // Create nd add a new id for the interrupting node
            interruptingNodeIds.add(nodeCount);
            // Add the list to the interrupted links map.
            interruptedLinks.put(interruptedLink, interruptingNodeIds);
            // Add the node to the hidden nodes and increment the global node counter
            hiddenNodeIds.add(nodeCount++);
        }

        // Return the first occurrence of an interrupting node id that does not exist in the given genome node ids.
        for (Integer nodeId : interruptingNodeIds)
            if (!genomeNodeIds.contains(nodeId))
                return nodeId;

        // In case the interrupting node ids are already present in the genome, we request a new node id, add it to the
        // list and return it, incrementing the global node count. Intuitively, this will add a new interrupting node
        // to a previously interrupted link.
        interruptingNodeIds.add(nodeCount);
        hiddenNodeIds.add(nodeCount);
        return nodeCount++;
    }

    /**
     * Generate a genome id and increase the genomes count
     * @return an integer to be used as an id for a genome
     */
    public int getNewGenomeId() {
        return genomeCount++;
    }

    /**
     * Generate a species id and increase the species count
     * @return an integer to be used as an id for a species
     */
    public int getNewSpeciesId() {
        return speciesCount++;
    }

    public Set<Integer> getInputNodeIds() {
        return inputNodeIds;
    }

    public int getBiasNodeId() {
        return biasNodeId;
    }

    public Set<Integer> getOutputNodeIds() {
        return outputNodeIds;
    }

    public Set<Integer> getHiddenNodeIds() {
        return hiddenNodeIds;
    }

    public ActivationType getDefaultActivationType() {
        return defaultActivationType;
    }

    public double getWeightRangeMin() {
        return weightRangeMin;
    }

    public double getWeightRangeMax() {
        return weightRangeMax;
    }
}
