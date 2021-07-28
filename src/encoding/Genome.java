package encoding;

import innovation.Innovations;
import util.Pair;

import java.util.*;

public class Genome {

    private static int lastId = 0;

    private List<NodeGene> nodeGenes = new ArrayList<>();
    private List<LinkGene> linkGenes = new ArrayList<>();

    private List<NodeGene> inputNodeGenes = new ArrayList<>();
    private List<NodeGene> outputNodeGenes = new ArrayList<>();
    private List<NodeGene> hiddenNodeGenes = new ArrayList<>();
    private NodeGene biasNodeGene = null;

    int id = 0;
    // int numInput;
    // int numOutput;

    double fitness;
    double adjustedFitness;
    int offspringToProduce;

    public Genome(Innovations innovations, double connectionProbability, double biasConnectionProbability) {
        initializeNodes(innovations);
        initializeLinks(innovations, connectionProbability, biasConnectionProbability);
        id = lastId++;
    }

    public Genome(Genome genome) { // Copy constructor

        // Copy primitives
        this.id = genome.id;
        this.fitness = genome.fitness;
        this.adjustedFitness = genome.adjustedFitness;
        this.offspringToProduce = genome.offspringToProduce;

        // Deep copy of input nodes genes.
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
    }

    private void initializeNodes(Innovations innovations) {

        System.out.println(innovations.getInputNodeIds() + " " + innovations.getBiasNodeId() + " " + innovations.getOutputNodeIds());

        for (Integer inputNodeId : innovations.getInputNodeIds())
            inputNodeGenes.add(new NodeGene(inputNodeId, NodeType.INPUT));

        if (innovations.getBiasNodeId() > 0)
            biasNodeGene = new NodeGene(innovations.getBiasNodeId(), NodeType.BIAS);

        for (Integer outputNodeId : innovations.getOutputNodeIds())
            outputNodeGenes.add(new NodeGene(outputNodeId, NodeType.OUTPUT));

        nodeGenes.addAll(inputNodeGenes);
        if (biasNodeGene != null) nodeGenes.add(biasNodeGene);
        nodeGenes.addAll(outputNodeGenes);

    }

    private void initializeLinks(Innovations innovations, double connectionProbability, double biasConnectionProbability) {
        for (NodeGene inputNodeGene : inputNodeGenes)
            for (NodeGene outputNodeGene : outputNodeGenes) {
                if (innovations.getRandomDouble() < connectionProbability)
                    linkGenes.add(new LinkGene(inputNodeGene.getId(), outputNodeGene.getId(), innovations));
            }

        if (innovations.getBiasNodeId() > 0)
            for (NodeGene outputNodeGene : outputNodeGenes) {
                if (innovations.getRandomDouble() < biasConnectionProbability)
                    linkGenes.add(new LinkGene(biasNodeGene.getId(), outputNodeGene.getId(),  innovations));
            }
    }

    public void addNewNode(NodeGene nodeGene) {
        hiddenNodeGenes.add(nodeGene);
        nodeGenes.add(nodeGene);
    }

    public void addNewLink(LinkGene linkGene) {
        linkGenes.add(linkGene);
    }

    public Set<Pair<Integer, Integer>> generatePossibleLinks() {
        Set<Pair<Integer, Integer>> possibleLinks = new HashSet<>();

        for (NodeGene inputNodeGene : inputNodeGenes) // input x output
            for (NodeGene outputNodeGene : outputNodeGenes)
                possibleLinks.add(new Pair<>(inputNodeGene.getId(), outputNodeGene.getId()));

        if (biasNodeGene != null) { // Bias(hidden + output)
            for (NodeGene outputNodeGene : outputNodeGenes)
                possibleLinks.add(new Pair<>(biasNodeGene.getId(), outputNodeGene.getId()));
            for (NodeGene hiddenNodeGene : hiddenNodeGenes)
                possibleLinks.add(new Pair<>(biasNodeGene.getId(), hiddenNodeGene.getId()));
        }

        for (NodeGene outputNodeGeneA : outputNodeGenes) // Output²
            for (NodeGene outputNodeGeneB : outputNodeGenes)
                possibleLinks.add(new Pair<>(outputNodeGeneA.getId(), outputNodeGeneB.getId()));

        for (NodeGene hiddenNodeGeneA : hiddenNodeGenes) // Hidden²
            for (NodeGene hiddenNodeGeneB : hiddenNodeGenes)
                possibleLinks.add(new Pair<>(hiddenNodeGeneA.getId(), hiddenNodeGeneB.getId()));

        for (NodeGene inputNodeGene : inputNodeGenes) // input x hidden
            for (NodeGene hiddenNodeGene : hiddenNodeGenes)
                possibleLinks.add(new Pair<>(inputNodeGene.getId(), hiddenNodeGene.getId()));

        for (NodeGene hiddenNodeGene : hiddenNodeGenes) // 2 x output x hidden
            for (NodeGene outputNodeGene : outputNodeGenes) {
                possibleLinks.add(new Pair<>(hiddenNodeGene.getId(), outputNodeGene.getId()));
                possibleLinks.add(new Pair<>(outputNodeGene.getId(), hiddenNodeGene.getId()));
            }

        for (LinkGene linkGene : linkGenes) { // Filter existing
            Pair<Integer, Integer> link = new Pair<>(linkGene.getSourceNodeId(), linkGene.getDestinationNodeId());
            possibleLinks.remove(link);
        }

        return possibleLinks;
    }

    @Override
    public String toString() {
        return "Genome " + id + ": {\n" +
               "- nodeGenes:\n" + geneListToString(nodeGenes) +
               "- linkGenes:\n" + geneListToString(linkGenes) +
               '}';
    }

    /**
     * Convert a List of genes (links or nodes) to a suitable string representation
     * @param geneList A list of genes
     * @return A string representation of the genes list
     */
    private String geneListToString(List<?> geneList) {
        if (geneList == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        // Iterate through the genes in the list, and place each gene in a separate line.
        for (Object gene : geneList)
            stringBuilder.append("\t\t").append(gene.toString()).append("\n");
        return stringBuilder.toString();
    }

    public List<NodeGene> getNodeGenes() {
        return nodeGenes;
    }

    public List<LinkGene> getLinkGenes() {
        return linkGenes;
    }

    public int calculatePossibleLinks() {
        return numberOfPossibleLinks(inputNodeGenes.size(), outputNodeGenes.size(), hiddenNodeGenes.size(),
                biasNodeGene != null);
    }

    /**
     * Computes the number of possible links using the formula:
     *  O² + h² + iO + h(i + 4) + h + O
     * @param numInput (i)
     * @param numOutput (O)
     * @param numHidden (h)
     * @param includeBias
     * @return
     */
    public static int numberOfPossibleLinks(int numInput, int numOutput, int numHidden, boolean includeBias) {
        return
                (numOutput * numOutput) + (numHidden * numHidden) + (numInput * numOutput) + numHidden * (numInput + 2 * numOutput)
                + (includeBias ? numHidden + numOutput : 0);
    }
}
