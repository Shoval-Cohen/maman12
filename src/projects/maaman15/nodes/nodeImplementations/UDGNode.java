package projects.maaman15.nodes.nodeImplementations;

import projects.maaman15.nodes.algorithms.BfsRouteAlgorithm;
import projects.maaman15.nodes.algorithms.MisAlgorithm;
import projects.maaman15.nodes.algorithms_state.bfs_routing.BfsRoutingAlgorithmState;
import projects.maaman15.nodes.algorithms_state.mis.MisAlgorithmState;
import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.message_handlers.bfs_routing.BfsBackMessageHandler;
import projects.maaman15.nodes.message_handlers.bfs_routing.BfsNextMassageHandler;
import projects.maaman15.nodes.message_handlers.mis.RandomNumberMessageHandler;
import projects.maaman15.nodes.message_handlers.mis.StopMisMessageHandler;
import projects.maaman15.nodes.message_handlers.routing.RoutingMessageHandler;
import projects.maaman15.nodes.messages.bfs_routing.BfsBackMessage;
import projects.maaman15.nodes.messages.bfs_routing.BfsNextMessage;
import projects.maaman15.nodes.messages.mis.MisRandomNumberMessage;
import projects.maaman15.nodes.messages.mis.MisStopMessage;
import projects.maaman15.nodes.messages.routing.RoutingMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

import java.util.*;
import java.util.stream.Collectors;

public class UDGNode extends Node {

    private Map<Class<?>, AbstractHandler> messageIHandlerMap; // message handlers map
    private final MisAlgorithm misAlgorithm; // mis algorithm
    private final BfsRouteAlgorithm bfsRoutingAlgorithm; // bfs routing algorithm
    private List<UDGNode> biggerIdNeighbors; // neighbors with bigger id
    private final Map<Integer, Node> neighborsIdToNodeMap; // neighbors id to node map

    public UDGNode(int totalNumberOfNodes, int numberOfRandomRounds) {
        misAlgorithm = new MisAlgorithm(this, totalNumberOfNodes, numberOfRandomRounds);
        bfsRoutingAlgorithm = new BfsRouteAlgorithm(this, totalNumberOfNodes);
        biggerIdNeighbors = new ArrayList<>();
        neighborsIdToNodeMap = new HashMap<>();
        initMessageHandlerMap();
    }

    /**
     * Initialize handlers map (message class as key to handler)
     */
    private void initMessageHandlerMap() {
        this.messageIHandlerMap = new HashMap<>();

        this.messageIHandlerMap.put(MisRandomNumberMessage.class, new RandomNumberMessageHandler(this));
        this.messageIHandlerMap.put(MisStopMessage.class, new StopMisMessageHandler(this));
        this.messageIHandlerMap.put(BfsBackMessage.class, new BfsBackMessageHandler(this));
        this.messageIHandlerMap.put(BfsNextMessage.class, new BfsNextMassageHandler(this));
        this.messageIHandlerMap.put(RoutingMessage.class, new RoutingMessageHandler(this));
    }

    @Override
    public void handleMessages(Inbox inbox) {

        while (inbox.hasNext()) {
            Message m = inbox.next(); // read message
            AbstractHandler handler = messageIHandlerMap.get(m.getClass()); // get handler from map
            Optional.ofNullable(handler).ifPresent(abstractHandler -> abstractHandler.handleMessage(m)); // handle message if handler exist
        }

        // run the different algorithms
        misAlgorithm.runIfNeeded();
        bfsRoutingAlgorithm.runIfNeeded();
    }

    /**
     * Set color according to MIS state (only if state has changed)
     */
    public void setMisColor() {
        if (misAlgorithm.getState().isChangedMISState()) {
            misAlgorithm.getState().setChangedMISState(false);
            setColor(misAlgorithm.getState().getIndependentSetState().getColor());
        }
    }

    /**
     * Update neighbor id to neighbor map
     */
    private void updateNeighborsMap() {
        outgoingConnections.forEach(edge -> {
            Node otherNode = edge.endNode;
            neighborsIdToNodeMap.put(otherNode.ID, otherNode);
        });
    }

    /**
     * @return a list of neighbor nodes with bigger id
     */
    private List<UDGNode> buildBiggerIdNeighborsList() {
        return neighborsIdToNodeMap.keySet().stream() // go over local neighbors map
                .filter(neighborsId -> neighborsId > ID) // only bigger id nodes
                .map(id -> (UDGNode) neighborsIdToNodeMap.get(id)) // map to UDGNode
                .collect(Collectors.toList()); // return list
    }


    // message senders

    /**
     * Send message to all of the neighbors
     *
     * @param m the message to send
     */
    public void sendToAllNeighbors(Message m) {
        for (Edge e : outgoingConnections) {
            send(m, e.endNode);
        }
    }

    // overrides

    @Override
    public void neighborhoodChange() {
        // add bidirectional connection
        outgoingConnections.forEach(edge -> edge.endNode.addConnectionTo(this));
        // update mis biggerIdNeighbors state
        biggerIdNeighbors = buildBiggerIdNeighborsList();
    }


    @Override
    public void preStep() {
        // update neighbor id to neighbor map
        updateNeighborsMap();
    }

    @Override
    public void init() {
    }

    @Override
    public void postStep() {
        setMisColor();
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
    }

    // getters

    public MisAlgorithmState getMISAlgorithmState() {
        return misAlgorithm.getState();
    }

    public BfsRoutingAlgorithmState getBfsRoutingState() {
        return bfsRoutingAlgorithm.getState();
    }

    public List<UDGNode> getBiggerIdNeighbors() {
        return biggerIdNeighbors;
    }

    public Map<Integer, Node> getNeighborsIdToNodeMap() {
        return neighborsIdToNodeMap;
    }
}
