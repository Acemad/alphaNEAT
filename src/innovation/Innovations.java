package innovation;

import encoding.NodeType;
import util.Pair;

import java.util.*;

public class Innovations {

    private static Random random = new Random();

    private int nodeCount = 0;
    private int linkCount = 0;

    private final Set<Integer> inputNodeIds = new LinkedHashSet<>();
    private final int biasNodeId;
    private final Set<Integer> outputNodeIds = new LinkedHashSet<>();

    private Set<Integer> hiddenNodeIds = new HashSet<>();

    // A set containing all the possible links that can be made between the nodes. Should not contain illegal links
    // as this is the original birthplace of all new links.
    // Illegal links: output to input, output to bias, hidden to input, hidden to bias
    private Set<Pair<Integer, Integer>> availableLinks = new HashSet<>();
    // Links currently in use, maps a pair of node ids (source and destination) to an innovation id
    private Map<Pair<Integer, Integer>, Integer> linksInUse = new HashMap<>();
    // The links that were interrupted by a new node. This map maps the old interrupted link to the id of the new node.
    private Map<Pair<Integer, Integer>, Integer> interruptedLinks = new HashMap<>();

    public Innovations(int numInput, int numOutput, boolean includeBias) {

        // Assign ids to input nodes
        for (int i = 0; i < numInput; i++) inputNodeIds.add(nodeCount++);
        // Assign id to bias node
        if (includeBias) biasNodeId = nodeCount++; else biasNodeId = -1;
        // Assign ids to output nodes
        for (int i = 0; i < numOutput; i++) outputNodeIds.add(nodeCount++);

        // All possible input to output links
        for (Integer inputNodeId : inputNodeIds) {
            for (Integer outputNodeId : outputNodeIds) {
                availableLinks.add(new Pair<>(inputNodeId, outputNodeId));
            }
        }

        // All possible bias to output links
        if (includeBias)
            for (Integer outputNodeId : outputNodeIds) {
                availableLinks.add(new Pair<>(biasNodeId, outputNodeId));
            }

        // All possible recurrent links
        for (Integer outputNodeIdA : outputNodeIds) {
            for (Integer outputNodeIdB : outputNodeIds) {
                availableLinks.add(new Pair<>(outputNodeIdA, outputNodeIdB));
            }
        }
    }

    public int getNewLinkId(int sourceNodeId, int destinationNodeId) {

        // The new link
        Pair<Integer, Integer> newLink = new Pair<>(sourceNodeId, destinationNodeId);

        Integer id = linksInUse.get(newLink); // Check if the new link is already in use.
        if (id == null) { // The link is not in use.
            //- if (availableLinks.remove(newLink)) { // Remove from the set of available links
                id = linkCount++; // Assign an id (the innovation id)
                linksInUse.put(newLink, id); // Add to the links in use
            //- } else
                //- id = -1;
        }
        return id; // Return the new id, (or, in case the link was in use, the current id)
    }

    public int newHiddenNode(int sourceNodeId, int destinationNodeId) {

        Pair<Integer, Integer> interruptedLink = new Pair<>(sourceNodeId, destinationNodeId);
        Integer newNodeId = interruptedLinks.get(interruptedLink);

        if (newNodeId == null) {

            // The link is interrupted for the first time. Create a new id for the interrupting node
            newNodeId = nodeCount++;
            // Add to the interrupted links map.
            interruptedLinks.put(interruptedLink, newNodeId);

            // Add all possible incoming links to this node
            for (Integer inputNodeId : inputNodeIds)
                availableLinks.add(new Pair<>(inputNodeId, newNodeId));

            if (biasNodeId > 0) availableLinks.add(new Pair<>(biasNodeId, newNodeId));

            // Add all possible incoming and outgoing links to and from this node
            for (Integer outputNodeId : outputNodeIds) {
                availableLinks.add(new Pair<>(outputNodeId, newNodeId));
                availableLinks.add(new Pair<>(newNodeId, outputNodeId));
            }

            for (Integer hiddenNodeId : hiddenNodeIds) {
                availableLinks.add(new Pair<>(hiddenNodeId, newNodeId));
                availableLinks.add(new Pair<>(newNodeId, hiddenNodeId));
            }

            // Add the loop connection
            availableLinks.add(new Pair<>(newNodeId, newNodeId));

            // Add the node to the hidden nodes
            hiddenNodeIds.add(newNodeId);
        }

        return newNodeId;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public Set<Pair<Integer, Integer>> getAvailableLinks() {
        return availableLinks;
    }

    public Map<Pair<Integer, Integer>, Integer> getLinksInUse() {
        return linksInUse;
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

    public double getRandomWeight() {
        return 2 * random.nextDouble() - 1;
    }

    public double getRandomDouble() {
        return random.nextDouble();
    }

    public int getRandomInt(int bound) {
        return random.nextInt(bound);
    }

}
