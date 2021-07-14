package projects.maaman15.nodes.algorithms;

import projects.maaman15.nodes.algorithms_state.AbstractAlgorithmState;
import projects.maaman15.nodes.nodeImplementations.UDGNode;

/**
 * This class is the base for implementing different algorithms
 *
 * @param <T> the state class
 */
public abstract class AbstractAlgorithm<T extends AbstractAlgorithmState> {

    protected UDGNode node; // the node
    protected T state; // algorithm state

    public AbstractAlgorithm(UDGNode node) {
        this.node = node;
    }

    /**
     * @return if should run steps
     */
    protected abstract boolean shouldRun();

    /**
     * Run single round of the algorithm
     */
    protected abstract void runStep();


    /**
     * First checks if should run, otherwise returns false (with {@link AbstractAlgorithm#shouldRun()}).
     * Otherwise runs {@link AbstractAlgorithm#runStep()} and returns true.
     */
    public boolean runIfNeeded() {
        if (shouldRun()) {
            runStep();
            return true;
        }
        return false;
    }

    /**
     * @return {@link AbstractAlgorithm#state}
     */
    public T getState() {
        return state;
    }
}
