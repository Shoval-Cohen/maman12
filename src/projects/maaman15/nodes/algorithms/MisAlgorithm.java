package projects.maaman15.nodes.algorithms;

import projects.maaman15.nodes.algorithms_state.mis.ENodeMisState;
import projects.maaman15.nodes.algorithms_state.mis.MisAlgorithmState;
import projects.maaman15.nodes.messages.mis.MisRandomNumberMessage;
import projects.maaman15.nodes.messages.mis.MisStopMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.tools.Tools;

import java.util.ArrayList;

/**
 * This class implements both randomized and determined stages of the given MIS algorithm on an UDG graph.
 */
public class MisAlgorithm extends AbstractAlgorithm<MisAlgorithmState> {

    public MisAlgorithm(UDGNode node, int numberOfNodes, int maxNumberOfRandomRounds) {
        super(node);
        this.state = new MisAlgorithmState(numberOfNodes, maxNumberOfRandomRounds);
    }

    @Override
    protected boolean shouldRun() {
        return state.isActive();
    }

    @Override
    protected void runStep() {

        if (state.isFirstRound()) { // in first round just send messages and than finish
            // send random number to neighbors and mark first round is finished
            node.sendToAllNeighbors(new MisRandomNumberMessage(state.getRndNumber()));
            state.setFirstRound(false);
            state.decreaseRounds(); // update rounds
            return;
        }

        // else, i.e. not first round: run maximal independent set algorithm if not finished
        if (!state.isFinishedRandom()) {
            randomMISStep();
            state.decreaseRounds(); // update rounds
            return;
        }

        // else, if random stage of MIS algorithm is finished and still active run deterministic step
        deterministicMISStep();
    }

    /**
     * Do a random mis algorithm step: (only for active nodes)
     * <p>If the random number of the node is the biggest number in the neighborhood, join MIS and notify neighbors</p>
     * <p>Otherwise, random pick a new number and notify neighbors</p>
     */
    private void randomMISStep() {
        if (state.getRndNumber() > state.findMaximumNeighborsNumber()) {
            // if this node holds the biggest number, join to mis and de-activate him and neighbors
            state.setActive(false);
            state.setIndependentSetState(ENodeMisState.IN_MIS);
            node.sendToAllNeighbors(new MisStopMessage());
            return;
        }
        // else, generate new random number and send it to neighbors
        state
                .setNextRandomNumber()
                .setNeighborsRandomRoundNumbers(new ArrayList<>())
                .setFirstRound(false);

        node.sendToAllNeighbors(new MisRandomNumberMessage(state.getRndNumber()));
    }

    /**
     * Do a deterministic mis algorithm step: (only for active nodes)
     * <p>1. Wait for all of the neighbors with bigger id to finish, i.e. all of them should have a finite mis state (in\out)</p>
     * <p>2. Then, if one of them is inside MIS this node is out, otherwise enter MIS</p>
     * <p>3. Update state </p>
     */
    private void deterministicMISStep() {

        if (!didAllBiggerIdNeighborsDone()) {
            // wait for all of the neighbors with bigger id to finish
            return;
        }

        for (UDGNode neighbor : node.getBiggerIdNeighbors()) {
            if (neighbor.getMISAlgorithmState().isInIndependentSet()) {
                state
                        .setIndependentSetState(ENodeMisState.OUT_MIS)
                        .setActive(false);
                return;
            }
        }

        // From this point all of the bigger id neighbors are inactive and out of the MIS
        state
                .setIndependentSetState(ENodeMisState.IN_MIS)
                .setActive(false);
    }

    /**
     * Check and return if all of the neighbors with bigger id to finish, i.e. all of them should have a finite mis state
     */
    protected boolean didAllBiggerIdNeighborsDone() {
        for (UDGNode biggerIdNeighbor : node.getBiggerIdNeighbors()) {
            if (biggerIdNeighbor.getMISAlgorithmState().isActive()) {
                return false;
            }
        }
        return true;
    }
}
