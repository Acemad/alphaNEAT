package engine.stats;

/**
 * This class is used to capture the reproduction statistics for a single generation
 * @author Acemad
 */
public class ReproductionStats {

    private final Counter mutationOnlyReproductions = new Counter();
    private final Counter mutations = new Counter();
    private final Counter matingOnlyReproductions = new Counter();
    private final Counter matings = new Counter();
    private final Counter matingPlusMutationReproductions = new Counter();
    private final Counter totalReproductions = new Counter();

    private final Counter addNodeMutations = new Counter();
    private final Counter addLinkMutations = new Counter();
    private final Counter weightMutations = new Counter();
    private final Counter toggleEnableMutations = new Counter();
    private final Counter reEnableMutations = new Counter();
    private final Counter activationMutations = new Counter();

    private final Counter deleteLinkMutations = new Counter();
    private final Counter deleteNodeMutations = new Counter();
    private final Counter reOrientLinkMutations = new Counter();

    public Counter mutationOnlyReproductions() {
        return mutationOnlyReproductions;
    }

    public Counter mutations() {
        return mutations;
    }

    public Counter matingOnlyReproductions() {
        return matingOnlyReproductions;
    }

    public Counter matings() {
        return matings;
    }

    public Counter matingPlusMutationReproductions() {
        return matingPlusMutationReproductions;
    }

    public Counter totalReproductions() {
        return totalReproductions;
    }

    public Counter addNodeMutations() {
        return addNodeMutations;
    }

    public Counter addLinkMutations() {
        return addLinkMutations;
    }

    public Counter weightMutations() {
        return weightMutations;
    }

    public Counter toggleEnableMutations() {
        return toggleEnableMutations;
    }

    public Counter reEnableMutations() {
        return reEnableMutations;
    }

    public Counter activationMutations() {
        return activationMutations;
    }

    public Counter deleteLinkMutations() {
        return deleteLinkMutations;
    }

    public Counter deleteNodeMutations() {
        return deleteNodeMutations;
    }

    public Counter reOrientLinkMutations() {
        return reOrientLinkMutations;
    }
}
