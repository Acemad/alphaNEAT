package operators;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import encoding.NodeType;
import innovation.Innovations;
import util.Pair;

import java.util.*;

public class Mutation {


    /**
     * Mutates a given genome by adding a random link that connects previously unconnected nodes.
     *  1. clone the genome into a new genome
     *  2. Subtract from the links used in the population and available links, the links in the genome
     *  3. Select a random link from the new set
     *  4. Create the new link in the new genome
     *  5. Return the new genome
     * @param genome
     * @param innovations
     * @return
     */
    public static Genome addNewLink(Genome genome, Innovations innovations) {

        // Copy the genome
        Genome mutatedGenome = new Genome(genome);

        System.out.println("Size (genomeLinks): " + mutatedGenome.getLinkGenes().size());
        System.out.println("Size (Links in use) - Before: " + innovations.getLinksInUse().size());

        List<Pair<Integer, Integer>> possibleLinks = new ArrayList<>(mutatedGenome.generatePossibleLinks());

        System.out.println("Possible links:\n" + possibleLinks + "\nSize (possibleLinks): " + possibleLinks.size() + "\n");
        System.out.println("MUTATED - Before:\n " + mutatedGenome);

        if (possibleLinks.size() == 0)
            return mutatedGenome;

        // Select a random link from the list of possible links, and add it to the link genes of the genome.
        Pair<Integer, Integer> selectedLink = possibleLinks.get(innovations.getRandomInt(possibleLinks.size()));
        mutatedGenome.getLinkGenes().add(new LinkGene(selectedLink.getA(), selectedLink.getB(), innovations));

        System.out.println("MUTATED - After:\n " + mutatedGenome);

        // Return the mutated genome
        return mutatedGenome;
    }

    public static Genome addNewNode(Genome genome, Innovations innovations) {

        // Copy the genome
        Genome mutatedGenome = new Genome(genome);

        // Find a valid link to interrupt. It must be enabled, and not starting from a bias node.
        List<LinkGene> links = mutatedGenome.getLinkGenes();
        boolean found = false;
        LinkGene selectedLink;

        // This should prioritize older links, still I have to figure out how.
        do {
            selectedLink = links.get(innovations.getRandomInt(links.size()));
            if (selectedLink.isEnabled() &&
                    selectedLink.getSourceNodeId() != innovations.getBiasNodeId())
                found = true;

        } while (!found);

        System.out.println("MUTATED (ADD Node) - Before:\n" + mutatedGenome);
        System.out.println("Num Available Links - Before: Available: " + mutatedGenome.generatePossibleLinks().size() +
                 " LinksInUse: " + links.size());

        selectedLink.disable();
        NodeGene newNode = new NodeGene(innovations.newHiddenNode(
                selectedLink.getSourceNodeId(), selectedLink.getDestinationNodeId()), NodeType.HIDDEN);
        LinkGene firstLink = new LinkGene(selectedLink.getSourceNodeId(), newNode.getId(), innovations);
        firstLink.setWeight(1);
        LinkGene secondLink = new LinkGene(newNode.getId(), selectedLink.getDestinationNodeId(), innovations);
        secondLink.setWeight(selectedLink.getWeight());

        mutatedGenome.addNewNode(newNode);
        mutatedGenome.addNewLink(firstLink);
        mutatedGenome.addNewLink(secondLink);

        System.out.println("MUTATED (ADD Node) - After:\n" + mutatedGenome);
        System.out.println("Num Available Links - After: Available: " + mutatedGenome.generatePossibleLinks().size() +
                " LinksInUse: " + mutatedGenome.getLinkGenes().size());

        // Add to genome
        return mutatedGenome;

    }



}
