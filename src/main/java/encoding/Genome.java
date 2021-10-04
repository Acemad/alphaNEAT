package encoding;

import encoding.phenotype.NeuralNetwork;
import engine.NEATConfig;
import engine.PRNG;
import innovation.InnovationDB;
import util.Link;
import util.ObjectSaver;
import util.Visualizer;

import java.io.*;
import java.util.*;

/**
 * The Genome class, implements the NEAT genome as described in Stanley's paper.
 * @author Acemad
 */
public class Genome implements Comparable<Genome>, Serializable {

    @Serial
    private final static long serialVersionUID = 1L;

    // List of all NodeGenes within the Genome
    private final List<NodeGene> nodeGenes = new ArrayList<>();
    // Lists of NodeGenes by type
    private final List<NodeGene> inputNodeGenes = new ArrayList<>();
    private final List<NodeGene> outputNodeGenes = new ArrayList<>();
    private final List<NodeGene> hiddenNodeGenes = new ArrayList<>();
    private NodeGene biasNodeGene = null;

    // List of all LinkGenes withing the Genome
    private final List<LinkGene> linkGenes = new ArrayList<>();
    private final Set<Integer> nodeGenesIds = new HashSet<>();

    int id;
    double fitness;
    double adjustedFitness;
    double spawnAmount; // The number of offspring this Genome should spawn

    /**
     * Constructs a new Genome using the parameters given.
     *
     * @param innovationDB The innovations DB, contains information about the basic structure of the desired network
     * @param connectionProbability The probability of connecting the input nodes with the outputs
     *                              (1: connect all, 0: no connections)
     * @param biasConnectionProbability The probability of connecting the bias node to the output nodes
     */
    public Genome(InnovationDB innovationDB, double connectionProbability, double biasConnectionProbability) {
        initializeNodes(innovationDB);
        initializeLinks(innovationDB, connectionProbability, biasConnectionProbability);
        id = innovationDB.getNewGenomeId();
    }

    /**
     * Creates a Genome with no connections. Meant to be used as a receptacle for the offspring resulting from
     * crossover.
     * @param innovationDB The innovations DB
     */
    public Genome(InnovationDB innovationDB) {
        initializeNodes(innovationDB);
        id = innovationDB.getNewGenomeId();
    }

    /**
     * Manually create a Genome with the given hidden nodes and links. Input/output/bias nodes are derived from the
     * innovation database, the rest must be provided.
     *
     * @param innovationDB The innovations database
     * @param hiddenNodes The hidden nodes list that constitutes the genome
     * @param links The links composing the genome
     */
    public Genome(InnovationDB innovationDB, List<NodeGene> hiddenNodes,  List<Link> links) {
        initializeNodes(innovationDB);

        hiddenNodeGenes.addAll(hiddenNodes);
        nodeGenes.addAll(hiddenNodes);
        for (NodeGene hiddenNode : hiddenNodes) nodeGenesIds.add(hiddenNode.getId());

        for (Link link : links)
            linkGenes.add(new LinkGene(link.getSource(), link.getDestination(), innovationDB));

        id = innovationDB.getNewGenomeId();
    }

    /**
     * Using the basic structure data in the innovations DB, initialize the input, bias, and output NodeGenes.
     * @param innovationDB The innovations DB
     */
    private void initializeNodes(InnovationDB innovationDB) {

        // Initialize input NodeGenes
        for (Integer inputNodeId : innovationDB.getInputNodeIds())
            inputNodeGenes.add(new NodeGene(inputNodeId, NodeType.INPUT));

        // Initialize Bias NodeGene
        if (innovationDB.getBiasNodeId() > 0)
            biasNodeGene = new NodeGene(innovationDB.getBiasNodeId(), NodeType.BIAS);

        // Initialize output NodeGenes
        for (Integer outputNodeId : innovationDB.getOutputNodeIds())
            outputNodeGenes.add(new NodeGene(outputNodeId, NodeType.OUTPUT, innovationDB.getDefaultActivationType()));

        // Add all nodes to the nodeGenes list
        nodeGenes.addAll(inputNodeGenes);
        if (biasNodeGene != null) nodeGenes.add(biasNodeGene);
        nodeGenes.addAll(outputNodeGenes);

        //+
        for (NodeGene nodeGene : nodeGenes)
            nodeGenesIds.add(nodeGene.getId());

    }

    /**
     * Initializes the first links of the genome according to given probabilities.
     *
     * @param innovationDB The innovation database
     * @param connectionProbability The probability of connecting input nodes to output nodes
     * @param biasConnectionProbability The probability of connecting bias node to output nodes
     */
    private void initializeLinks(InnovationDB innovationDB, double connectionProbability, double biasConnectionProbability) {

        // Link input nodes to output nodes following the given probability. Creates the corresponding LinkGenes and adds
        // them to the linkGenes list
        for (NodeGene inputNodeGene : inputNodeGenes)
            for (NodeGene outputNodeGene : outputNodeGenes) {
                if (PRNG.nextDouble() < connectionProbability)
                    linkGenes.add(new LinkGene(inputNodeGene.getId(), outputNodeGene.getId(), innovationDB));
            }

        // If the bias node is included, link it to the output nodes, according to the given probability. Creates the
        // corresponding LinkGenes and adds them to the linkGenes list.
        if (innovationDB.getBiasNodeId() > 0)
            for (NodeGene outputNodeGene : outputNodeGenes) {
                if (PRNG.nextDouble() < biasConnectionProbability)
                    linkGenes.add(new LinkGene(biasNodeGene.getId(), outputNodeGene.getId(), innovationDB));
            }
    }

    /**
     * Copy Constructor. Creates an exact copy of the given Genome
     * @param genome The Genome to copy
     * @param innovationDB The innovation database, for retrieving a new id
     */
    public Genome(Genome genome, InnovationDB innovationDB) {

        // Copy primitives
        this.fitness = genome.fitness;
        this.adjustedFitness = genome.adjustedFitness;
        this.spawnAmount = genome.spawnAmount;

        // Deep copy input nodes genes.
        for (NodeGene inputNodeGene : genome.inputNodeGenes)
            this.inputNodeGenes.add(new NodeGene(inputNodeGene));

        // Copy of bias node.
        if (genome.biasNodeGene != null)
            this.biasNodeGene = new NodeGene(genome.biasNodeGene);
        else
            this.biasNodeGene = null;

        // Deep copy of output nodes genes
        for (NodeGene outputNodeGene : genome.outputNodeGenes)
            this.outputNodeGenes.add(new NodeGene(outputNodeGene));

        // Deep copy of hidden nodes genes
        for (NodeGene hiddenNodeGene : genome.hiddenNodeGenes)
            this.hiddenNodeGenes.add(new NodeGene(hiddenNodeGene));

        // Populate the node genes array list.
        this.nodeGenes.addAll(this.inputNodeGenes);
        if (this.biasNodeGene != null) this.nodeGenes.add(this.biasNodeGene);
        this.nodeGenes.addAll(this.hiddenNodeGenes);
        this.nodeGenes.addAll(this.outputNodeGenes);

        // Deep copy of link genes
        for (LinkGene linkGene : genome.linkGenes)
            this.linkGenes.add(new LinkGene(linkGene));

        //+
        this.nodeGenesIds.addAll(genome.getNodeGenesIds());

        // The copied Genome has a new id
        id = innovationDB.getNewGenomeId();
        // this.id = genome.id;
    }

    /**
     * Generates all possible links between nodes, including recurrent links (loops, hidden->hidden and output->hidden)
     * The generated set does not include links already made between nodes.
     *
     * @return A Set of Integer Pairs representing possible links
     */
    public Set<Link> generatePossibleLinks(NEATConfig config) {

        // The set of possible links. A link is represented by a pair of integers (source -> destination)
        Set<Link> possibleLinks = new HashSet<>();

        // Generate all input to output links. (input x output)
        for (NodeGene inputNodeGene : inputNodeGenes)
            for (NodeGene outputNodeGene : outputNodeGenes)
                possibleLinks.add(new Link(inputNodeGene.getId(), outputNodeGene.getId()));

        // Generate all possible bias to hidden, bias to output links. (bias x (hidden + output))
        if (biasNodeGene != null) {
            for (NodeGene outputNodeGene : outputNodeGenes)
                possibleLinks.add(new Link(biasNodeGene.getId(), outputNodeGene.getId()));
            for (NodeGene hiddenNodeGene : hiddenNodeGenes)
                possibleLinks.add(new Link(biasNodeGene.getId(), hiddenNodeGene.getId()));
        }

        // Generate all possible links between output nodes, including loops. (output x output)
        for (NodeGene outputNodeGeneA : outputNodeGenes)
            for (NodeGene outputNodeGeneB : outputNodeGenes)
                possibleLinks.add(new Link(outputNodeGeneA.getId(), outputNodeGeneB.getId()));

        // Generate all possible links between hidden nodes, including loops, and backwards links. (hidden x hidden)
        for (NodeGene hiddenNodeGeneA : hiddenNodeGenes)
            for (NodeGene hiddenNodeGeneB : hiddenNodeGenes)
                possibleLinks.add(new Link(hiddenNodeGeneA.getId(), hiddenNodeGeneB.getId()));

        // Generate all possible links from input nodes to hidden nodes. (input x hidden)
        for (NodeGene inputNodeGene : inputNodeGenes)
            for (NodeGene hiddenNodeGene : hiddenNodeGenes)
                possibleLinks.add(new Link(inputNodeGene.getId(), hiddenNodeGene.getId()));

        // Generate all possible links from/to hidden nodes to/from output nodes.
        // Includes backward links (2 x output x hidden)
        for (NodeGene hiddenNodeGene : hiddenNodeGenes)
            for (NodeGene outputNodeGene : outputNodeGenes) {
                possibleLinks.add(new Link(hiddenNodeGene.getId(), outputNodeGene.getId()));
                possibleLinks.add(new Link(outputNodeGene.getId(), hiddenNodeGene.getId()));
            }

        // Remove links already present in the linkGenes list from the list of all possible links. Keep only
        // non-existing links.
        for (LinkGene linkGene : linkGenes) {
            Link link = new Link(linkGene.getSourceNodeId(), linkGene.getDestinationNodeId());
            possibleLinks.remove(link);
        }

        if (config.linkTypeFiltering()) filterLinks(possibleLinks, config);

        return possibleLinks;
    }

    /**
     * Given a list of links, remove of keep the specific link types depending on the predefined rates given in the
     * configuration
     *
     * @param possibleLinks The links to filter
     * @param config The configuration file containing all parameter values
     */
    private void filterLinks(Set<Link> possibleLinks, NEATConfig config) {

        // Filter all links between hidden nodes
        // (Keep a single hidden layer: input->hidden, hidden->output only)
        if (PRNG.nextDouble() < 1 - config.linksBetweenHiddenNodesProportion())
            possibleLinks.removeIf(link ->
                    getNodeGeneById(link.getSource()).getType() == NodeType.HIDDEN &&
                            getNodeGeneById(link.getDestination()).getType() == NodeType.HIDDEN);

        // Filter hidden node loops
        if (PRNG.nextDouble() < 1 - config.hiddenLoopLinksProportion())
            possibleLinks.removeIf(link -> (link.getSource() == link.getDestination() &&
                    getNodeGeneById(link.getSource()).getType() == NodeType.HIDDEN));

        // Filter output node loops
        if (PRNG.nextDouble() < 1 - config.outputLoopLinksProportion())
            possibleLinks.removeIf(link -> link.getSource() == link.getDestination() &&
                    getNodeGeneById(link.getSource()).getType() == NodeType.OUTPUT);

        // Filter output to hidden
        if (PRNG.nextDouble() < 1 - config.outputToHiddenLinksProportion())
            possibleLinks.removeIf(link -> getNodeGeneById(link.getSource()).getType() == NodeType.OUTPUT &&
                    getNodeGeneById(link.getDestination()).getType() == NodeType.HIDDEN);

        // Filter output to output
        if (PRNG.nextDouble() < 1 - config.outputToOutputLinksProportion())
            possibleLinks.removeIf(link -> getNodeGeneById(link.getSource()).getType() == NodeType.OUTPUT &&
                    getNodeGeneById(link.getDestination()).getType() == NodeType.OUTPUT &&
                    link.getSource() != link.getDestination());

        // Filter hidden to hidden recurrent
        if (PRNG.nextDouble() < 1 - config.hiddenToHiddenBackwardLinksProportion())
            possibleLinks.removeIf(link -> {
                NodeGene source = getNodeGeneById(link.getSource());
                NodeGene destination = getNodeGeneById(link.getDestination());

                return ((source.getType() == NodeType.HIDDEN && destination.getType() == NodeType.HIDDEN) &&
                        distanceToOutput(source) < distanceToOutput(destination));
            });

        // Filter hidden to hidden, same distance from output (same level)
        if (PRNG.nextDouble() < 1 - config.hiddenToHiddenSameLevelLinksProportion())
            possibleLinks.removeIf(link -> {
                NodeGene source = getNodeGeneById(link.getSource());
                NodeGene destination = getNodeGeneById(link.getDestination());

                return ((source.getType() == NodeType.HIDDEN && destination.getType() == NodeType.HIDDEN) &&
                        distanceToOutput(source) == distanceToOutput(destination) &&
                        link.getSource() != link.getDestination());
            });
    }

    /**
     * Calculates the distance of a given node to the closest output node using BFS. This is used to determine whether
     * a link between two hidden nodes is backward or not. If the distance to output of node A is lower than that of
     * node B, the link A->B is a backward link. We assume that two links of different distance from output are in
     * different layers.
     *
     * The distance represents the number of links between the node and the closest output node
     * If there is no path from the given node to an output node, we assume the distance is equal to one (the minimum
     * distance possible)
     *
     * @param nodeGene The NodeGene to calculate the distance for
     * @return The distance of the node gene to the closest output
     */
    public int distanceToOutput(NodeGene nodeGene) {

        // Distance of output from output is zero, and distance of an input/bias from output is +inf
        if (nodeGene.getType() == NodeType.OUTPUT) return 0;
        if (nodeGene.getType() == NodeType.INPUT || nodeGene.getType() == NodeType.BIAS)
            return Integer.MAX_VALUE;

        // We use a queue of lists of nodes to determines the distance
        Deque<List<NodeGene>> nodeQueue = new ArrayDeque<>();
        // Insert the next nodes list to the queue
        List<NodeGene> initialNodes = new ArrayList<>(getNextNodesConnectedTo(nodeGene));
        if (!initialNodes.isEmpty()) nodeQueue.addLast(initialNodes);
        // Record visited nodes, to avoid cycles
        Set<NodeGene> visited = new HashSet<>();
        visited.add(nodeGene);
        // initial distance
        int distance = 1;

        // Iterate through the queue (BFS)
        while (!nodeQueue.isEmpty()) {
            // Get the node list from the front of the queue
            List<NodeGene> currentNodes = nodeQueue.removeFirst();
            // Iterate through the nodes
            for (NodeGene currentNode : currentNodes) {
                // Process non-visited nodes
                if (!visited.contains(currentNode)) {
                    // Mark node as visited
                    visited.add(currentNode);
                    // An output node is found, return the distance
                    if (currentNode.getType() == NodeType.OUTPUT)
                        return distance;
                    else { // No output node, add the list of next nodes to the queue
                        List<NodeGene> nextNodes = getNextNodesConnectedTo(currentNode);
                        if (!nextNodes.isEmpty()) nodeQueue.addLast(nextNodes);
                    }
                }
            }
            // no output node yet, increment distance
            distance++;
        }

        // Output is unreachable
        return 1;
        // return Integer.MAX_VALUE - 1;
    }

    /**
     * Decides whether this Genome is compatible with a given Genome. The decision is made after computing
     * the compatibility measure with the other Genome and comparing it with a given genome. In contrast with the
     * original implementation, this function does distinguish between disjoint and excess genes, because of doing so
     * is not meaningful. Instead, disjoint and excess genes are commonly named unmatched genes.
     *
     * @param genome The Genome to test compatibility with
     * @param unmatchedCoeff Coefficient of unmatched genes
     * @param weightDiffCoeff Coefficient of weigh difference in matched genes
     * @param compatibilityThreshold The threshold after which the Genome is considered non-compatible
     * @return The decision whether this Genome is compatible with the given Genome or not.
     */
    public boolean isCompatibleWith(Genome genome, double unmatchedCoeff, double weightDiffCoeff,
                                    double activationDiffCoeff, double compatibilityThreshold) {

        // Assemble all LinkGenes (for both Genomes) in a single set
        Set<LinkGene> linkGenes = new HashSet<>(this.getLinkGenes());
        linkGenes.addAll(genome.getLinkGenes());

        // Get the size of the longest Genome
        double maxLength = Math.max(this.getLinkGenes().size(), genome.getLinkGenes().size());

        // Initialize the variables for matched genes, unmatched genes, and total weight difference.
        int matchedLinks = 0;
        int unmatchedLinks = 0;
        double totalWeightDiff = 0;

        int matchedNodes = 0;
        double activationDiff = 0;

        // Calculate the number of matched and unmatched genes, and the total weight difference for matched genes
        for (LinkGene linkGene : linkGenes) {
            if (this.getLinkGenes().contains(linkGene) && genome.getLinkGenes().contains(linkGene)) { // Matching genes
                matchedLinks++;
                totalWeightDiff += Math.abs(this.getLinkGenes().get(this.getLinkGenes().indexOf(linkGene)).getWeight() -
                        genome.getLinkGenes().get(genome.getLinkGenes().indexOf(linkGene)).getWeight());
            } else // Non-matching genes
                unmatchedLinks++;
        }

        // Computes the difference in activation function type between matched nodes.
        // This is an original addition.
        if (activationDiffCoeff > 0) {
            Set<NodeGene> nodeGenes = new HashSet<>(this.getNodeGenes());
            nodeGenes.addAll(genome.getNodeGenes());
            for (NodeGene nodeGene : nodeGenes) {
                if (nodeGene.getType() == NodeType.INPUT || nodeGene.getType() == NodeType.BIAS)
                    continue;
                // A matched node
                if (this.getNodeGenes().contains(nodeGene) && genome.getNodeGenes().contains(nodeGene)) {
                    matchedNodes++;
                    // Increment the difference when the activation function types do not match
                    if (!(this.getNodeGenes().get(this.getNodeGenes().indexOf(nodeGene)).getActivationFunction()
                            .equals(genome.getNodeGenes().get(genome.getNodeGenes().indexOf(nodeGene)).getActivationFunction())))
                        activationDiff++;
                }
            }
        }

        // Compute the final compatibility score, and return the decision.
        // Note: Compat score range should be: [0, C1 + 2C2] when weights are in the range [-1, 1], and with normalization
        double score = unmatchedCoeff * (unmatchedLinks/* / maxLength*/) + weightDiffCoeff * (totalWeightDiff / matchedLinks);

        // Add the activation difference term if enabled. Max score would become C1+2C2+C3
        if (activationDiffCoeff > 0)
             score += activationDiffCoeff * (activationDiff / matchedNodes);

        // System.out.println("CompatScore: " + score + " UnmatchedLinks: " + unmatchedLinks + " TotWeightDiff: " + (totalWeightDiff / matchedLinks));
        return score < compatibilityThreshold;
    }

    /**
     * Add a new NodeGene to the Genome. The new NodeGene represents a hidden node and is added to the list of hidden
     * nodes.
     * @param nodeGene The new NodeGene
     */
    public void addNewHiddenNode(NodeGene nodeGene) {
        if (nodeGene != null) {
            hiddenNodeGenes.add(nodeGene);
            nodeGenes.add(nodeGene);
            //++
            nodeGenesIds.add(nodeGene.getId());
        }
    }

    /**
     * Adds a new node gene to the list of nodes, if it does not exist.
     * This is to be used for filling out Genome receptacles, especially in crossover
     *
     * @param nodeGene The node gene to add
     */
    public void addMissingNode(NodeGene nodeGene) {
        // First, check if the genome does not contain a node with the same id
        if (!nodeGenesIds.contains(nodeGene.getId())) {
            nodeGenes.add(nodeGene); // Add to the principal list
            switch (nodeGene.getType()) { // Add to the correct type list
                case INPUT -> inputNodeGenes.add(nodeGene);
                case BIAS -> biasNodeGene = nodeGene;
                case HIDDEN -> hiddenNodeGenes.add(nodeGene);
                case OUTPUT -> outputNodeGenes.add(nodeGene);
            }
            // Add its id to the ids list
            nodeGenesIds.add(nodeGene.getId());
        }
    }

    /**
     * Add a new LinkGene to the Genome. The LinkGene is added to the list of link genes
     * @param linkGene The link to add
     */
    public void addNewLink(LinkGene linkGene) {
        if (linkGene != null) linkGenes.add(linkGene);
    }

    /**
     * Returns all the LinkGene Ids currently in use in this Genome as a Set
     * @return A Set containing the LinkGenes Ids
     */
    public Set<Integer> getLinkGeneIds() {

        // linkGenes.stream().map(LinkGene::getId).collect(Collectors.toSet());

        Set<Integer> linkGeneIds = new HashSet<>();
        for (LinkGene linkGene : linkGenes)
            linkGeneIds.add(linkGene.getId());
        return linkGeneIds;
    }

    /**
     * Returns all the NodeGene Ids currently in use in this Genome as a Set
     * @return A Set containing the NodeGenes Ids
     */
    public Set<Integer> getNodeGenesIds() {
        /*Set<Integer> nodeGeneIds = new HashSet<>();
        for (NodeGene nodeGene : nodeGenes)
            nodeGeneIds.add(nodeGene.getId());*/
        return nodeGenesIds;
    }

    /**
     * Retrieves all disabled LinkGenes and returns them in a List.
     * @return A List of disabled LinkGenes in the Genome
     */
    public List<LinkGene> getDisabledLinkGenes() {
        List<LinkGene> disabledLinkGenes = new ArrayList<>();
        for (LinkGene linkGene : linkGenes)
            if (!linkGene.isEnabled()) disabledLinkGenes.add(linkGene);
        return disabledLinkGenes;
    }

    /**
     * Retrieves all enabled LinkGenes and return them in a List
     * @return A List of enabled LinkGenes in the Genome
     */
    public List<LinkGene> getEnabledLinkGenes() {
        List<LinkGene> enabledLinkGenes = new ArrayList<>();
        for (LinkGene linkGene : linkGenes)
            if (linkGene.isEnabled()) enabledLinkGenes.add(linkGene);
        return enabledLinkGenes;
    }

    /**
     * Computes the number of possible links this Genome can have.
     * @return The number of possible links.
     */
    public int calculatePossibleLinks() {
        return numberOfPossibleLinks(inputNodeGenes.size(), outputNodeGenes.size(), hiddenNodeGenes.size(),
                biasNodeGene != null);
    }

    /**
     * Computes the number of possible links using the formula:
     *  O² + h² + i*O + h(i + 2*O) + b(h + O) {b=1 if includeBias, b=0 otherwise}
     * @param numInput (i) Number of input nodes
     * @param numOutput (O) Number of output nodes
     * @param numHidden (h) Number of hidden nodes
     * @param includeBias (b) Bias existence
     * @return The number of possible links
     */
    public static int numberOfPossibleLinks(int numInput, int numOutput, int numHidden, boolean includeBias) {
        return (numOutput * numOutput) + (numHidden * numHidden) + (numInput * numOutput) +
                numHidden * (numInput + 2 * numOutput) + (includeBias ? numHidden + numOutput : 0);
    }

    /**
     * Builds a NeuralNetwork object (Phenotype) out of this Genome
     * @return A NeuralNetwork instance corresponding to this Genome
     */
    public NeuralNetwork buildNetwork() {
        return new NeuralNetwork(this);
    }

    /**
     * Verify the consistency of the genome by running a series of checks to determine whether the genome is correctly
     * formed or not. First, it checks for the number of input, output, and bias nodes. Second, it checks for node/link
     * duplication. Third, it checks for the presence of link source/destination nodes in the genome nodes. Fourth, it
     * checks for the presence of all nodes in at least one connection.
     *
     * @param innovationDB The innovation database
     */
    public void checkGenomeConsistency(InnovationDB innovationDB) {

        // Check the number of input/output nodes, and bias node:
        if (getInputNodeGenes().size() != innovationDB.getInputNodeIds().size())
            System.err.println("Input: Number of input nodes do not match the parameter number");

        if (getOutputNodeGenes().size() != innovationDB.getOutputNodeIds().size())
            System.err.println("Output: Number of output nodes do not match the parameter number");

        if (getBiasNodeGene() != null && getBiasNodeGene().getId() != innovationDB.getBiasNodeId()
                || getBiasNodeGene() == null && innovationDB.getBiasNodeId() != -1)
            System.err.println("Bias: Presence of bias node not consistent with parameters");

        // Check for duplicate nodes/links:
        Set<NodeGene> nodeGeneSet = new HashSet<>(getNodeGenes());
        if (nodeGeneSet.size() != getNodeGenes().size())
            System.err.println("Duplication: Duplicate nodes exist");

        Set<LinkGene> linkGeneSet = new HashSet<>(getLinkGenes());
        if (linkGeneSet.size() != getLinkGenes().size())
            System.err.println("Duplication: Duplicate links exist");

        // Check the presence of all link source/destination nodes in genome nodes
        for (LinkGene linkGene : getLinkGenes()) {
            if (!getNodeGenesIds().contains(linkGene.getSourceNodeId())) {
                System.err.println("Links: A source node does not exist in the Genome");
                break;
            }
            if (!getNodeGenesIds().contains(linkGene.getDestinationNodeId())) {
                System.err.println("Links: A destination node does not exist in the Genome");
            }
        }

        // Check if all nodes are present in at least one link:
        for (NodeGene nodeGene : getNodeGenes()) {

            boolean nodeFound = false;

            for (LinkGene linkGene : getLinkGenes())
                if (linkGene.getSourceNodeId() == nodeGene.getId() ||
                        linkGene.getDestinationNodeId() == nodeGene.getId())
                {
                    nodeFound = true;
                    break;
                }

            if (!nodeFound) {
                System.err.println("Nodes: A node is left without any connection\n" + this.toConciseString());
                break;
            }
        }
    }

    /**
     * Removes a hidden node gene from the lists of node genes if it is present, and also removes all its related
     * connections
     *
     * @param nodeGene The nodeGene to remove
     */
    public void removeHiddenNode(NodeGene nodeGene, boolean removeRelatedLinks) {

        // First, check existence
        if (hiddenNodeGenes.contains(nodeGene)) {
            nodeGenes.remove(nodeGene);
            hiddenNodeGenes.remove(nodeGene);
            nodeGenesIds.remove(nodeGene.getId());

            // Remove related links
            if (removeRelatedLinks)
                linkGenes.removeIf(linkGene ->
                        linkGene.getDestinationNodeId() == nodeGene.getId() ||
                        linkGene.getSourceNodeId() == nodeGene.getId());
        }
    }

    /**
     * Repair genomes with dangling nodes that could result from crossover. A node is a dangling node if it has no
     * inbound or outbound connection. We fix these genomes by reconnecting the nodes without a source to a random
     * input node, and nodes without a destination to a random output node. We may also remove the dangling node if
     * the probability permits. This is an original enhancement.
     *
     * @param innovationDB The innovation database
     * @param removeProbability The probability of removing the dangling node and its related links from the genome
     */
    public int fixDanglingNodes(InnovationDB innovationDB, double removeProbability) {

        // Lists to hold dangling nodes and their types
        List<NodeGene> danglingNodes = new ArrayList<>();
        List<NodeGene> nonSourceNodes = new ArrayList<>(); // Nodes that are not sources to any other nodes
        List<NodeGene> nonDestinationNodes = new ArrayList<>(); // Node that are not destinations to any other nodes

        // Check each hidden node
        for (NodeGene hiddenNode : hiddenNodeGenes) {

            // Assume the nodes are neither sources nor destinations
            boolean isSource = false;
            boolean isDestination = false;

            // Check all links
            for (LinkGene linkGene : linkGenes) {

                // The node is a source in a link
                if (!isSource && linkGene.getSourceNodeId() == hiddenNode.getId())
                    isSource = true;
                // The node is a destination in a link
                if (!isDestination && linkGene.getDestinationNodeId() == hiddenNode.getId())
                    isDestination = true;

                // The node is both a source and a destination, move on to the next node
                if (isSource && isDestination) break;
            }

            // The node ie either a source or a destination, (or neither)
            if (!isSource || !isDestination) {

                // Add to the correct list. A hidden node without any connections will be added to both lists
                if (!isSource) nonSourceNodes.add(hiddenNode);
                if (!isDestination) nonDestinationNodes.add(hiddenNode);

                // Update the global list
                danglingNodes.add(hiddenNode);
            }
        }

        // Either remove the dangling nodes, and their connections, or reconnect them.
        if (PRNG.nextDouble() < removeProbability) {
            // Remove dangling nodes
            for (NodeGene danglingNode : danglingNodes)
                removeHiddenNode(danglingNode, true);

        } else {

            // Reconnect dangling nodes with random output nodes for non-source nodes
            for (NodeGene nonSourceNode : nonSourceNodes) { // Non-source
                // First check if there is a disabled link going from this node to an output node, if there is, enable it
                boolean disabledLinkExists = false;
                for (LinkGene disabledLinkGene : getDisabledLinkGenes())
                    if (disabledLinkGene.getSourceNodeId() == nonSourceNode.getId() &&
                            getNodeGeneById(disabledLinkGene.getDestinationNodeId()).getType() == NodeType.OUTPUT) {
                        disabledLinkGene.enable();
                        disabledLinkExists = true;
                    }

                // if not, create a link to a random output node
                if (!disabledLinkExists) {
                    NodeGene randomOutput = outputNodeGenes.get(PRNG.nextInt(outputNodeGenes.size()));
                    LinkGene newLink = new LinkGene(nonSourceNode.getId(), randomOutput.getId(), innovationDB);
                    addNewLink(newLink);
                }
            }

            // Reconnect dangling nodes with random input nodes for non-destination nodes
            for (NodeGene nonDestinationNode : nonDestinationNodes) { // Non-destination
                // First check if there is a disabled link going from an input node to this node, if there is, enable it
                boolean disabledLinkExists = false;
                for (LinkGene disabledLinkGene : getDisabledLinkGenes())
                    if (disabledLinkGene.getDestinationNodeId() == nonDestinationNode.getId() &&
                            getNodeGeneById(disabledLinkGene.getSourceNodeId()).getType() == NodeType.INPUT) {
                        disabledLinkGene.enable();
                        disabledLinkExists = true;
                    }

                // if not, create a link to a random output node
                if (!disabledLinkExists) {
                    NodeGene randomInput = inputNodeGenes.get(PRNG.nextInt(inputNodeGenes.size()));
                    LinkGene newLink = new LinkGene(randomInput.getId(), nonDestinationNode.getId(), innovationDB);
                    addNewLink(newLink);
                }
            }
        }

        return danglingNodes.size();
    }

    public void updateNodeLevelsFrom(NodeGene nodeGene) {

        Deque<List<NodeGene>> nodeQueue = new ArrayDeque<>();
        nodeQueue.addLast(inputNodeGenes);

        Set<NodeGene> visited = new HashSet<>();

        int level = 0;

        while (!nodeQueue.isEmpty()) {

            List<NodeGene> currentNodes = nodeQueue.removeFirst();
            List<NodeGene> nextNodes = new ArrayList<>();
            boolean oneVisited = false;

            for (NodeGene currentNode : currentNodes) {

                if (!visited.contains(currentNode)) {
                    oneVisited = true;
                    visited.add(currentNode);
                    currentNode.setLevel(level);
                    // todo do not add empty lists
                    nextNodes.addAll(getNextNodesConnectedTo(currentNode));
                }
            }

            nodeQueue.addLast(nextNodes);

            if (oneVisited) level++;
        }

    }

    /**
     * Save the Genome to a file
     * @param filePath Path of the file to save to
     */
    public void saveToFile(String filePath) {
        ObjectSaver.saveObjectToFile(this, filePath);
    }

    /**
     * Loads a Genome instance from a file
     * @param filePath Path of the Genome file
     * @return The Genome instance
     */
    public static Genome readFromFile(String filePath) {
        return ObjectSaver.loadFromFile(filePath, Genome.class);
    }

    /**
     * Return the node gene represented by the given node id
     * @param nodeId The id of the node to retrieve
     * @return A NodeGene of the same id
     */
    public NodeGene getNodeGeneById(int nodeId) {
        if (nodeGenesIds.contains(nodeId)) {
            for (NodeGene nodeGene : nodeGenes)
                if (nodeGene.getId() == nodeId)
                    return nodeGene;
        }
        return null;
    }

    /**
     * Returns a list of node genes directly connected to the given gene, as destinations. Ignore loops and disabled
     * links
     *
     * @param nodeGene The concerned node gene
     * @return A list of node genes that represent link destinations to the given node
     */
    public List<NodeGene> getNextNodesConnectedTo(NodeGene nodeGene) {

        List<NodeGene> nextNodes = new ArrayList<>();

        for (LinkGene linkGene : linkGenes) {
            if (linkGene.isEnabled() && !linkGene.isLoop() && linkGene.getSourceNodeId() == nodeGene.getId())
                nextNodes.add(getNodeGeneById(linkGene.getDestinationNodeId()));
        }

        return nextNodes;
    }

    /**
     * Returns a list of node genes directly connected to the given gene, as sources. Ignore loops and disabled
     * links
     *
     * @param nodeGene The concerned node gene
     * @return A list of node genes that represent link sources to the given node
     */
    public List<NodeGene> getPreviousNodesConnectedTo(NodeGene nodeGene) {

        List<NodeGene> previousNodes = new ArrayList<>();

        for (LinkGene linkGene : linkGenes) {
            if (linkGene.isEnabled() && !linkGene.isLoop() && linkGene.getDestinationNodeId() == nodeGene.getId())
                previousNodes.add(getNodeGeneById(linkGene.getSourceNodeId()));
        }

        return previousNodes;
    }

    /**
     * Retrieves the list of outgoing link genes from the given node gene.
     *
     * @param nodeGene A node gene in this genome
     * @param enabledOnly If true, return only enabled outgoing links
     * @return A list of outgoing link genes
     */
    public List<LinkGene> getOutgoingLinksFrom(NodeGene nodeGene, boolean enabledOnly) {

        List<LinkGene> outgoingLinks = new ArrayList<>();

        for (LinkGene linkGene : linkGenes)
            if (linkGene.getSourceNodeId() == nodeGene.getId())
                if (enabledOnly) {
                    if (linkGene.isEnabled()) outgoingLinks.add(linkGene);
                } else
                    outgoingLinks.add(linkGene);

        return outgoingLinks;
    }

    /**
     * Retrieves a list of incoming link genes relative to the given node gene.
     *
     * @param nodeGene A node gene in this genome
     * @param enabledOnly If true, return only enabled incoming links
     * @return A list of incoming link genes
     */
    public List<LinkGene> getIncomingLinksTo(NodeGene nodeGene, boolean enabledOnly) {

        List<LinkGene> incomingLinks = new ArrayList<>();

        for (LinkGene linkGene : linkGenes)
            if (linkGene.getDestinationNodeId() == nodeGene.getId())
                if (enabledOnly) {
                    if (linkGene.isEnabled()) incomingLinks.add(linkGene);
                } else
                    incomingLinks.add(linkGene);

        return incomingLinks;
    }

    /**
     * Retrieve the link gene represented by the given simple link if it exists in this genome. Returns null
     * if the link doesn't exist.
     *
     * @param link A simple link representing source and destination of the wanted link
     * @return The link gene represented by the simple link or null if it does not exist
     */
    public LinkGene getLinkGeneFromLink(Link link) {

        for (LinkGene linkGene : linkGenes) {
            if (linkGene.getSourceNodeId() == link.getSource()
                    && linkGene.getDestinationNodeId() == link.getDestination())
                return linkGene;
        }

        return null;
    }

    /**
     * Computes the complexity of the genome. In this version, complexity is simply the number of link genes in the
     * genome
     * @return Integer value representing complexity
     */
    public int complexity() {
        return linkGenes.size();
    }

    @Override
    public String toString() {
        nodeGenes.sort(null);
        linkGenes.sort(null);
        return "Genome " + id + ", Fitness: " + fitness + ", AdjustedFitness: " + adjustedFitness + " {\n" +
               "- nodeGenes (" + nodeGenes.size() + ") :\n" + geneListToString(nodeGenes) +
               "- linkGenes (" + linkGenes.size() + ") :\n" + geneListToString(linkGenes) + '}';
    }

    /**
     * Convert a List of genes (links or nodes) to a suitable string representation
     * @param genes A list of genes
     * @return A string representation of the genes list
     */
    public static String geneListToString(Collection<?> genes) {
        if (genes == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        // Iterate through the genes in the list, and place each gene in a separate line.
        for (Object gene : genes)
            stringBuilder.append("\t\t").append(gene.toString()).append("\n");
        return stringBuilder.toString();
    }

    /**
     * Generate a concise String representation of this Genome
     * @return A concise String representation of this Genome
     */
    public String toConciseString() {
        nodeGenes.sort(null);
        linkGenes.sort(null);
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("G%-4d (", id));
        for (NodeGene nodeGene : nodeGenes) {
            builder.append(nodeGene.toConciseString());
            if (nodeGenes.indexOf(nodeGene) < nodeGenes.size() - 1) builder.append(", ");
        }
        builder.append(") (");
        for (LinkGene linkGene : linkGenes) {
            builder.append(linkGene.toConciseString());
            if (linkGenes.indexOf(linkGene) < linkGenes.size() - 1) builder.append(", ");
        }
        builder.append(String.format(") -> f:% 7.2f", fitness))
                .append(String.format(", af:% 7.2f", adjustedFitness))
                .append(String.format(", sa:% 7.2f", spawnAmount))
                .append(String.format("\t <- G%-3d", id));

        return builder.toString();
    }

    /**
     * Genomes are compared by their fitness value first, and in case fitness is equal, we compare the number of link
     * Genes of each genome, where a lower number is better.
     *
     * TODO Contrib: perhaps find a better way to measure complexity of a network?
     *
     * @param genome The Genome to compare with
     * @return Result of comparison
     */
    @Override
    public int compareTo(Genome genome) {

        int fitnessCompareResult = Double.compare(fitness, genome.getFitness());
        if (fitnessCompareResult == 0) // Equal fitness
            // Compare network complexity (number of linkGenes), lower is better
            return - Integer.compare(complexity(), genome.complexity());
        else
            return fitnessCompareResult;

        //return Double.compare(fitness, genome.getFitness());
    }

    /**
     * Two Genomes are equal if their NodeGenes and LinkGenes are equal.
     * @param obj The object to compare with
     * @return true if both Genomes are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Genome genome = (Genome) obj;
        return nodeGenes.equals(genome.nodeGenes) && linkGenes.equals(genome.linkGenes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeGenes, linkGenes);
    }

    /**
     * Visualize the Genome using the Visualizer utility class
     */
    public void show() {
        Visualizer.showGStream(this);
    }

    public List<NodeGene> getNodeGenes() {
        return nodeGenes;
    }

    public List<LinkGene> getLinkGenes() {
        return linkGenes;
    }

    public List<NodeGene> getInputNodeGenes() {
        return inputNodeGenes;
    }

    public List<NodeGene> getHiddenNodeGenes() {
        return hiddenNodeGenes;
    }

    public List<NodeGene> getOutputNodeGenes() {
        return outputNodeGenes;
    }

    public NodeGene getBiasNodeGene() {
        return biasNodeGene;
    }

    public int getId() {
        return id;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getAdjustedFitness() {
        return adjustedFitness;
    }

    public void setAdjustedFitness(double adjustedFitness) {
        this.adjustedFitness = adjustedFitness;
    }

    public double getSpawnAmount() {
        return spawnAmount;
    }

    public void setSpawnAmount(double spawnAmount) {
        this.spawnAmount = spawnAmount;
    }

}
