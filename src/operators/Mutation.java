package operators;

import activations.ActivationType;
import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import encoding.NodeType;
import engine.NRandom;
import innovation.InnovationDB;
import util.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutation operators are hosted in this class as a collection of static methods.
 *
 * @author Acemad
 */
public class Mutation {

    /**
     * Mutates a given genome by adding a random link that connects previously unconnected nodes.*
     *
     * @param genome The genome to mutate
     * @param innovationDB The innovationDB keeping track of link ids
     * @return A mutated Genome with a new id
     */
    public static Genome addNewLink(Genome genome, InnovationDB innovationDB) {

        // 1. Obtain the list of possible connection between the nodes in the Genome
        List<Link> possibleLinks = new ArrayList<>(genome.generatePossibleLinks());

        // 2. In case no linking is possible, jut return the genome
        if (possibleLinks.size() == 0)
            return genome;

        // 3. Clone the genome
        Genome mutatedGenome = new Genome(genome);
        // 4. Select a random link from the list of possible links
        Link selectedLink = possibleLinks.get(NRandom.getRandomInt(possibleLinks.size()));
        // 5. Generate a new LinkGene using the selected link and add it to the genome
        mutatedGenome.addNewLink(new LinkGene(selectedLink.getSource(), selectedLink.getDestination(), innovationDB));

        // 6. Return the mutated genome
        return mutatedGenome;
    }

    /**
     * Apply the add node mutation. Clones the genome and adds a node in place of a previous connection, disabling the
     * connection and creating two new links joining the previously connected nodes with the new node.
     *
     * @param genome The genome to mutate
     * @param innovationDB The innovation database keeping track of the node and link ids
     * @return A mutated genome with a new id
     */
    public static Genome addNewNode(Genome genome, InnovationDB innovationDB) {

        // 1. Clone the genome
        Genome mutatedGenome = new Genome(genome);

        // 2. Find a valid link to interrupt.
        List<LinkGene> links = mutatedGenome.getLinkGenes();

        boolean found = false;
        LinkGene selectedLink;

        // TODO This should prioritize older links
        do {
            selectedLink = links.get(NRandom.getRandomInt(links.size()));
            // Selected link must be enabled and not starting from a BIAS node.
            if (selectedLink.isEnabled() && selectedLink.getSourceNodeId() != innovationDB.getBiasNodeId())
                found = true;

        } while (!found);

        // 3. Disable the selected link
        selectedLink.disable();

        // 4. Retrieve a new id for the new node from the innovation database
        int newId = innovationDB.requestInterruptingNodeId(selectedLink.getSourceNodeId(),
                selectedLink.getDestinationNodeId(), mutatedGenome.getNodeGenesIds());

        // 5. Create the new node
        NodeGene newNode = new NodeGene(newId, NodeType.HIDDEN, innovationDB.getDefaultActivationType());

        // 6. Create the first link going from the source of the disabled link to the new node, set its weight to 1
        LinkGene firstLink = new LinkGene(selectedLink.getSourceNodeId(), newNode.getId(), innovationDB);
        firstLink.setWeight(1);

        // 7. Create the second link going from the new node to the destination of the disabled node, set its weight
        // to the weight of the disabled link
        LinkGene secondLink = new LinkGene(newNode.getId(), selectedLink.getDestinationNodeId(), innovationDB);
        secondLink.setWeight(selectedLink.getWeight());

        // 8. Add the new node and the new links to the mutated genome
        mutatedGenome.addNewHiddenNode(newNode);
        mutatedGenome.addNewLink(firstLink);
        mutatedGenome.addNewLink(secondLink);

        // Return the mutated genome
        return mutatedGenome;
    }

    /**
     * Mutate the weights of the link genes. For each link gene decide whether to perturb it, replace it, or leave it
     * as it is, depending on predetermined parameters
     *
     * @param genome The genome to mutate
     * @param innovationDB The innovation database
     * @return A mutated genome
     */
    public static Genome mutateWeights(Genome genome, InnovationDB innovationDB, double perturbationStrength) {

        // Chance of perturbing / replacing a connection weight. replacementProbability > perturbationProbability
        double replacementProbability;
        double perturbationProbability;

        // 1. Clone the genome
        Genome mutatedGenome = new Genome(genome);

        // 2. Iterate through all link genes and decide whether to perturb or replace their weights
        // TODO Heavily mutate genes in the tail of the chromosome (less time-tested)
        for (LinkGene linkGene : mutatedGenome.getLinkGenes()) {

            // A 50/50 chance to use more sever parameters. (As used in Stanley's NEAT implementation)
            if (NRandom.getRandomBoolean()) { // Severe
                replacementProbability = 0.9;
                perturbationProbability = 0.7;
            } else { // Normal
                replacementProbability = 1;
                perturbationProbability = 0.9;
            }

            // Roll a dice
            double chance = NRandom.getRandomDouble();

            // Depending on chance, either perturb the weight, replace it entirely by a new weight, or leave it
            if (chance < perturbationProbability)
                // Perturb: Add a fraction of a random weight to the current weight
                linkGene.setWeight(linkGene.getWeight() + (NRandom.getRandomWeight(
                        innovationDB.getWeightRangeMin(), innovationDB.getWeightRangeMax()) * perturbationStrength));
            else if (chance < replacementProbability)
                // Replace weight: generate a new random weight
                linkGene.setWeight(NRandom.getRandomWeight(innovationDB.getWeightRangeMin(),
                        innovationDB.getWeightRangeMax()));
        }

        // Return the mutated genome
        return mutatedGenome;
    }

    /**
     * Mutate a genome by toggling the 'enabled' switch of a random link gene within the genome.
     *
     * @param genome The genome to mutate
     * @return A mutated genome
     */
    public static Genome mutateToggleEnable(Genome genome) {

        // 1. clone the genome
        Genome mutatedGenome = new Genome(genome);

        // 2. Retrieve a random link gene
        LinkGene gene = mutatedGenome.getLinkGenes().get(NRandom.getRandomInt(mutatedGenome.getLinkGenes().size()));

        // 3. If the gene is enabled
        if (gene.isEnabled()) {

            // 4. Check whether there are other links besides this one getting out from the source node, this is
            // done to not isolate nodes. Disable the link if the criteria is met
            for (LinkGene linkGene : mutatedGenome.getLinkGenes())
                if (linkGene.isEnabled() && linkGene.getId() != gene.getId() && !linkGene.isLoop() &&
                    linkGene.getSourceNodeId() == gene.getSourceNodeId()) {
                    gene.disable();
                    break;
                }
        } else {
            // 5. if the gene is disabled, enable it
            gene.enable();
        }

        // Return the mutated genome
        return mutatedGenome;
    }

    /**
     * Mutates a genome by re-enabling a random disabled link.
     *
     * @param genome The Genome to mutate
     * @return A mutated genome
     */
    public static Genome mutateReEnable(Genome genome) {

        // 1. clone the genome
        Genome mutatedGenome = new Genome(genome);

        // 2. retrieve all the disabled links in the genome
        List<LinkGene> disabledLinkGenes = mutatedGenome.getDisabledLinkGenes();

        // 3. if there is at least one disabled gene, chose a random one and enable it. Otherwise, just return
        // the genome
        if (!disabledLinkGenes.isEmpty()) {
            disabledLinkGenes.get(NRandom.getRandomInt(disabledLinkGenes.size())).enable();
        } else
            return genome;

        // 4. Return the mutated genome
        return mutatedGenome;
    }

    /**
     * Mutate a Genome by randomly changing the activation function type for a number of node genes within the genome
     *
     * @param genome The genome to mutate
     * @param mutateActivationRate The percent of node genes to mutate within the genome
     */
    public static Genome mutateActivationType(Genome genome, double mutateActivationRate, String allowedActivations) {

        // 1. Clone the genome
        Genome mutatedGenome = new Genome(genome);

        // 2. Retrieve a list of mutable node genes. Hidden + Output
        List<NodeGene> mutableNodeGenes = new ArrayList<>(mutatedGenome.getHiddenNodeGenes());
        mutableNodeGenes.addAll(mutatedGenome.getOutputNodeGenes());

        // 3. Mutate the node genes according to mutateActivationRate
        for (NodeGene mutableNodeGene : mutableNodeGenes) {
            if (NRandom.getRandomDouble() < mutateActivationRate)
                mutableNodeGene.setActivationFunction(ActivationType.getRandomType(allowedActivations));
        }

        // 4. Return mutated genome
        return mutatedGenome;
    }

    // TODO WIP : Deletion mutation for phased evolution
    public static Genome deleteNode(Genome genome) {

        Genome mutatedGenome = new Genome(genome);

        if (mutatedGenome.getHiddenNodeGenes().size() > 0) {
            NodeGene nodeToDelete = mutatedGenome.getHiddenNodeGenes().get(
                    NRandom.getRandomInt(mutatedGenome.getHiddenNodeGenes().size()));
        }

        for (NodeGene hiddenNodeGene : mutatedGenome.getHiddenNodeGenes()) {

        }

        return mutatedGenome;
    }

    // TODO: Re-orient mutation, change the destination of a link from one node to another
}
