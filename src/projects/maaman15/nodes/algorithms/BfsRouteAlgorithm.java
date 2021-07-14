package projects.maaman15.nodes.algorithms;

import projects.maaman15.nodes.algorithms_state.bfs_routing.BfsRoutingAlgorithmState;
import projects.maaman15.nodes.messages.bfs_routing.BfsNextMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;

/**
 * This class creates routing tables using parallel BFS that starts from nodes that are in MIS to all the reachable nodes in the graph.
 */
public class BfsRouteAlgorithm extends AbstractAlgorithm<BfsRoutingAlgorithmState> {

    public BfsRouteAlgorithm(UDGNode node, int numberOfNodes) {
        super(node);
        state = new BfsRoutingAlgorithmState(numberOfNodes);
    }

    /**
     * Will run <b>one time</b> for <b>MIS nodes only</b>
     *
     * @return if should run steps
     */
    @Override
    protected boolean shouldRun() {
        return node.getMISAlgorithmState().isInIndependentSet() && !state.isStarted();
    }

    /**
     * Start parallel bfs from node and update state
     */
    @Override
    protected void runStep() {
        state.setStarted(true); // update state

        if (node.getNeighborsIdToNodeMap().isEmpty()) { // finish if the degree is 1
            state.setActive(false);
            return;
        }

        // otherwise send to neighbors
        BfsNextMessage message = new BfsNextMessage(node.ID, node.ID);
        node.sendToAllNeighbors(message); // send message to neighbors
    }

}
