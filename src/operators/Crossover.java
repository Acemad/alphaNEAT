package operators;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import encoding.NodeType;
import innovation.Innovations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crossover {

    /**
     * Apply the crossover operator on two parents and generate an offspring genome.
     *
     * @param parentA
     * @param parentB
     * @return
     */
    public static Genome crossover(Innovations innovations, Genome parentA, Genome parentB) {

        // 1. Create the offspring receptacle genome, which will receive the crossed-over link genes
        // 2. Apply crossover between genomes, taking into account the more fit parent and the disabled genes
        // 3. Add the resulting chromosome (link genes) to the receptacle genome
        // 4. Add the missing hidden nodes
        // 5. Return the offspring

        System.out.println("Parent A: " + parentA);
        System.out.println("Parent B: " + parentB);

        Genome offspring = new Genome(innovations); // Genome without links or hidden nodes

        List<LinkGene> parentALinks = parentA.getLinkGenes();
        List<LinkGene> parentBLinks = parentB.getLinkGenes();
        List<LinkGene> offspringLinks = new ArrayList<>();

        Set<LinkGene> linkGenes = new HashSet<>(parentALinks);
        linkGenes.addAll(parentBLinks);

        for (LinkGene linkGene : linkGenes) {

            LinkGene selectedGene = null; // Just for initialization,
                                          // null links cannot be added to the link genes of the offspring.

            boolean linkInParentA = parentALinks.contains(linkGene);
            boolean linkInParentB = parentBLinks.contains(linkGene);

            if (linkInParentA && linkInParentB) { // Matched genes

                LinkGene geneVersionA = parentALinks.get(parentALinks.indexOf(linkGene));
                LinkGene geneVersionB = parentBLinks.get(parentBLinks.indexOf(linkGene));

                if (innovations.getRandomBoolean()) // Chose one version at random
                    selectedGene = new LinkGene(geneVersionA);
                else
                    selectedGene = new LinkGene(geneVersionB);

                // If in either version the gene is disabled, there's a chance that it stays disabled in the selected
                // gene.
                if (geneVersionA.isEnabled() != geneVersionB.isEnabled()) {
                    if (innovations.getRandomDouble() < 0.75)
                        selectedGene.disable();
                    else
                        selectedGene.enable();
                }

            } else if (parentA.getFitness() == parentB.getFitness()) { // Disjoint/Excess genes with equal fitness

                // Take genes from the shortest genome
                if (linkInParentA && parentALinks.size() < parentBLinks.size() ||
                    linkInParentB && parentBLinks.size() < parentALinks.size()) {
                    selectedGene = new LinkGene(linkGene);
                } // Parents have the same number of genes, randomly determine whether to take the gene or not
                else if (parentALinks.size() == parentBLinks.size() && innovations.getRandomBoolean()) {
                    selectedGene = new LinkGene(linkGene);
                }

            } else // Take the genes from the parent with the highest fitness
                if (parentA.getFitness() > parentB.getFitness() && linkInParentA ||
                    parentB.getFitness() > parentA.getFitness() && linkInParentB)
                    selectedGene = new LinkGene(linkGene);
                else // Drop the gene because it's from the weaker/longest parent
                    continue;


            // Add missing nodes:
            if (selectedGene != null) {
                if (!offspring.getNodeGenesIds().contains(selectedGene.getSourceNodeId()))
                    offspring.addNewNode(new NodeGene(selectedGene.getSourceNodeId(), NodeType.HIDDEN));
                if (!offspring.getNodeGenesIds().contains(selectedGene.getDestinationNodeId()))
                    offspring.addNewNode(new NodeGene(selectedGene.getDestinationNodeId(), NodeType.HIDDEN));
            }

            // Add the new link:
            offspring.addNewLink(selectedGene);

        }

        System.out.println("Offspring: " + offspring);

        return offspring;
    }

}
