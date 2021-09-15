package operators;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import engine.NEATConfig;
import engine.NRandom;
import innovation.InnovationDB;

import java.util.*;

/**
 * This class hosts the crossover operators as static methods.
 * @author Acemad
 */
public class Crossover {

    /**
     * Apply the multipoint crossover operator on two parents and generate an offspring genome. Depending on the given
     * parameters crossover could either average the weights of matching genes or chose one or the other randomly.
     *
     * @param parentA The first parent
     * @param parentB The second parent
     * @return An offspring resulting from the crossover operation
     */
    public static Genome multipointCrossover(Genome parentA, Genome parentB, NEATConfig config) {

        // Create the offspring receptacle genome, which will receive the crossed-over link genes
        Genome offspring = new Genome();

        // Get the LinkGenes of both parents
        List<LinkGene> parentALinks = parentA.getLinkGenes();
        List<LinkGene> parentBLinks = parentB.getLinkGenes();

        // Assemble all LinkGenes of both parents in a single set of LinkGenes
        Set<LinkGene> linkGenes = new HashSet<>(parentALinks);
        linkGenes.addAll(parentBLinks);

        // Retrieve all parent nodes in these two maps, for adding nodes to the offspring later
        Map<Integer, NodeGene> parentANodes = new HashMap<>();
        Map<Integer, NodeGene> parentBNodes = new HashMap<>();
        for (NodeGene nodeGene : parentA.getNodeGenes()) parentANodes.put(nodeGene.getId(), nodeGene);
        for (NodeGene nodeGene : parentB.getNodeGenes()) parentBNodes.put(nodeGene.getId(), nodeGene);

        // Iterate through the set of LinkGenes
        for (LinkGene linkGene : linkGenes) {

            LinkGene selectedGene = null; // Just for initialization,
                                          // null links cannot be added to the link genes of the offspring.

            // In which parent is this linkGene present?
            boolean linkInParentA = parentALinks.contains(linkGene);
            boolean linkInParentB = parentBLinks.contains(linkGene);

            // The linkGene exists in both parents: Matched genes
            if (linkInParentA && linkInParentB) {

                // Get both parents versions of the gene
                LinkGene geneVersionA = parentALinks.get(parentALinks.indexOf(linkGene));
                LinkGene geneVersionB = parentBLinks.get(parentBLinks.indexOf(linkGene));

                // Chose one version at random, and add the missing nodes related to this gene
                if (NRandom.getRandomBoolean()) {
                    selectedGene = new LinkGene(geneVersionA);
                    addMissingNodes(offspring, selectedGene, parentANodes);
                }
                else {
                    selectedGene = new LinkGene(geneVersionB);
                    addMissingNodes(offspring, selectedGene, parentBNodes);
                }

                // Averaging: compute the average weight of both genes if the probability permits. Otherwise, just keep
                // the weight of the selected gene
                if (NRandom.getRandomDouble() < config.mateAveragingProbability())
                    selectedGene.setWeight((geneVersionA.getWeight() + geneVersionB.getWeight()) / 2);

                // If in either version the gene is disabled, there's a chance that it stays disabled in the selected
                // gene.
                if (geneVersionA.isEnabled() != geneVersionB.isEnabled()) {
                    if (NRandom.getRandomDouble() < config.mateKeepGeneDisabledProbability())
                        selectedGene.disable();
                    else {
                        selectedGene.enable();
                    }
                }

            } else if (parentA.getFitness() == parentB.getFitness()) { // Disjoint/Excess genes with equal fitness

                // Take genes from the shortest genome
                if (linkInParentA && parentALinks.size() < parentBLinks.size() ||
                    linkInParentB && parentBLinks.size() < parentALinks.size()) {
                    selectedGene = new LinkGene(linkGene);
                } // Parents have the same number of genes, randomly determine whether to take the gene or not
                else if (parentALinks.size() == parentBLinks.size() && NRandom.getRandomBoolean())
                    selectedGene = new LinkGene(linkGene);

            } else // Disjoint/excess genes with different fitness: Take the genes from the parent with the highest fitness
                if (parentA.getFitness() > parentB.getFitness() && linkInParentA ||
                    parentB.getFitness() > parentA.getFitness() && linkInParentB)
                    selectedGene = new LinkGene(linkGene);
                else // Drop the gene because it's from the weaker/longer parent
                    continue;

            // Add the missing nodes related to the selected gene, in case of a gene found in only one of the parents
            // (disjoint/excess)
            if (selectedGene != null) {
                if ((linkInParentA && !linkInParentB))
                    addMissingNodes(offspring, selectedGene, parentANodes);
                else if ((linkInParentB && !linkInParentA))
                    addMissingNodes(offspring, selectedGene, parentBNodes);
            }

            // Add the new link:
            offspring.addNewLink(selectedGene);
        }

        // sanitizeGenome(offspring, innovationDB);//+
        return offspring;
    }

    /**
     * Given an offspring with missing nodes, a recently added gene, and the nodes in the source genome of the gene
     * (parent), add the missing nodes to the offspring.
     *
     * @param offspring The offspring to repair
     * @param newGene The newly added gene
     * @param parentNodes Nodes of the parent genome
     */
    private static void addMissingNodes(Genome offspring, LinkGene newGene, Map<Integer, NodeGene> parentNodes) {

        // Add the missing source/destination nodes. If either nodes already exist in the genome, nothing will be added
        offspring.addMissingNode(new NodeGene(parentNodes.get(newGene.getSourceNodeId())));
        offspring.addMissingNode(new NodeGene(parentNodes.get(newGene.getDestinationNodeId())));
    }


}
