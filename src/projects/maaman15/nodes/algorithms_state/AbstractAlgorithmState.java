package projects.maaman15.nodes.algorithms_state;

/**
 * This is an abstract class that stores essential variables and data (states) that is used for running the different algorithms.
 */
public abstract class AbstractAlgorithmState {

    protected boolean isActive = true; // is node active
    protected boolean isFirstRound = true; // if it is first round
    protected final int numberOfNodes; // number of total nodes

    public AbstractAlgorithmState(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    /**
     * Initialize default values for all of the non final variables
     */
    public void initValues() {
        isFirstRound = true;
        isActive = true;
    }

    // getters and setters

    public boolean isActive() {
        return isActive;
    }

    public AbstractAlgorithmState setActive(boolean active) {
        isActive = active;
        return this;
    }

    public boolean isFirstRound() {
        return isFirstRound;
    }

    public void setFirstRound(boolean firstRound) {
        isFirstRound = firstRound;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }
}
