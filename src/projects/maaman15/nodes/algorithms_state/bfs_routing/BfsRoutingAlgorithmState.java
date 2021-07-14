package projects.maaman15.nodes.algorithms_state.bfs_routing;

import projects.maaman15.nodes.algorithms_state.AbstractAlgorithmState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BfsRoutingAlgorithmState extends AbstractAlgorithmState {

    protected Map<Integer, Integer> routingTable; // the routing tables
    protected Map<Integer, Integer> ackTable; // ack table
    protected boolean started; // has started bfs or not

    public BfsRoutingAlgorithmState(int numberOfNodes) {
        super(numberOfNodes);
        initValues();
    }

    @Override
    public void initValues() {
        super.initValues();
        routingTable = new ConcurrentHashMap<>();
        ackTable = new ConcurrentHashMap<>();
        started = false;
        isActive = false;
    }

    /**
     * First if the entry for the key rootId doesn't exist add new entry with zero.
     * Then, increase ack table entry value.
     *
     * @param rootId the root id (map key)
     * @return new counter value (increased by 1)
     */
    public int increaseAndGetAckCounter(int rootId) {
        ackTable.putIfAbsent(rootId, 0);
        int counter = ackTable.get(rootId) + 1;
        ackTable.put(rootId, counter);
        return counter;
    }

    // getters and setters

    public Map<Integer, Integer> getRoutingTable() {
        return routingTable;
    }

    public Map<Integer, Integer> getAckTable() {
        return ackTable;
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Set {@link BfsRoutingAlgorithmState#started}. If (started == true) update isActive as well
     *
     * @param started the value to update
     * @return this
     */
    public BfsRoutingAlgorithmState setStarted(boolean started) {
        this.started = started;

        if (started) { // update active as well
            setActive(true);
        }
        return this;
    }
}
