package projects.maaman15.nodes.algorithms_state.mis;

import projects.maaman15.nodes.algorithms_state.AbstractAlgorithmState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MisAlgorithmState extends AbstractAlgorithmState {

    protected final int MAX_ROUNDS_RANDOM; // max random rounds
    protected int roundsLeftRandom; // rounds left for random steps
    protected int rndNumber; // random number (changes each round)
    protected ENodeMisState independentSetState; // the node maximal independent set state
    protected List<Integer> neighborsRandomRoundNumbers; // neighbors numbers for current round
    protected boolean changedMISState; // has changed MIS state in this round (for update coloring in GUI)

    public MisAlgorithmState(int numberOfNodes, int MAX_ROUNDS_RANDOM) {
        super(numberOfNodes);
        this.MAX_ROUNDS_RANDOM = MAX_ROUNDS_RANDOM;
        initValues();
    }

    @Override
    public void initValues() {
        super.initValues();
        roundsLeftRandom = MAX_ROUNDS_RANDOM;
        independentSetState = ENodeMisState.NON_FINAL;
        isActive = true;
        rndNumber = generateNextRandomNumber();
        neighborsRandomRoundNumbers = new ArrayList<>();
        changedMISState = true;
    }

    /**
     * @return new generated random number in the range of [1, numberOfNodes^10]
     */
    public int generateNextRandomNumber() {
        return ThreadLocalRandom.current().
                nextInt(1, (int) Math.pow(numberOfNodes, 10));
    }

    /**
     * Check if the random rounds are done.
     * This will return true if no more rounds left <u>and</u> the state is not final.
     * Otherwise return false.
     *
     * @return boolean indicator for if the random stage is finished
     */
    public boolean isFinishedRandom() {
        return roundsLeftRandom < 0 && !(independentSetState.equals(ENodeMisState.NON_FINAL));
    }

    /**
     * Decrease roundsLeft by 1.
     */
    public void decreaseRounds() {
        roundsLeftRandom--;
    }

    /**
     * Find and return the biggest number out of all neighbors numbers (in this round).
     * If there aren't any neighbors sent their number (i.e. non exist or all inactive) return -1.
     */
    public int findMaximumNeighborsNumber() {

        if (neighborsRandomRoundNumbers.isEmpty()) {
            // if there aren't any neighbors sent their number (i.e. non exist or all inactive) return -1
            return -1;
        }
        // otherwise: return the biggest number out of all neighbors numbers (in thus round)
        return Collections.max(neighborsRandomRoundNumbers);
    }

    // getters and setters

    /**
     * Set current random number variable to the new generated with {@link MisAlgorithmState#generateNextRandomNumber()}.
     *
     * @return this
     */
    public MisAlgorithmState setNextRandomNumber() {
        return setRndNumber(generateNextRandomNumber());
    }

    public int getMaxRoundsRandom() {
        return MAX_ROUNDS_RANDOM;
    }

    public int getRoundsLeftRandom() {
        return roundsLeftRandom;
    }

    public MisAlgorithmState setRoundsLeftRandom(int roundsLeftRandom) {
        this.roundsLeftRandom = roundsLeftRandom;
        return this;
    }

    public int getRndNumber() {
        return rndNumber;
    }

    public MisAlgorithmState setRndNumber(int rndNumber) {
        this.rndNumber = rndNumber;
        return this;
    }

    public boolean isInIndependentSet() {
        return independentSetState == ENodeMisState.IN_MIS;
    }

    public ENodeMisState getIndependentSetState() {
        return independentSetState;
    }

    public MisAlgorithmState setIndependentSetState(ENodeMisState independentSetState) {
        if (this.independentSetState != independentSetState) {
            changedMISState = true;
        }
        this.independentSetState = independentSetState;
        return this;
    }

    public List<Integer> getNeighborsRandomRoundNumbers() {
        return neighborsRandomRoundNumbers;
    }

    public MisAlgorithmState setNeighborsRandomRoundNumbers(List<Integer> neighborsRandomRoundNumbers) {
        this.neighborsRandomRoundNumbers = neighborsRandomRoundNumbers;
        return this;
    }

    public boolean isChangedMISState() {
        return changedMISState;
    }

    public MisAlgorithmState setChangedMISState(boolean changedMISState) {
        this.changedMISState = changedMISState;
        return this;
    }
}
