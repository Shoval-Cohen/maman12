package projects.maaman15.nodes.message_handlers.bfs_routing;

import projects.maaman15.nodes.algorithms_state.bfs_routing.BfsRoutingAlgorithmState;
import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.messages.bfs_routing.BfsBackMessage;
import projects.maaman15.nodes.messages.bfs_routing.BfsNextMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class BfsNextMassageHandler extends AbstractHandler {

    public BfsNextMassageHandler(UDGNode node) {
        super(node);
    }

    @Override
    protected void handle(Message message) {
        BfsNextMessage bfsNextMessage = (BfsNextMessage) message;
        BfsRoutingAlgorithmState routingState = node.getBfsRoutingState();

        if (!routingState.getRoutingTable().containsKey(bfsNextMessage.getRootId())) { // if that is a new bfs initiator
            routingState.getRoutingTable().put(bfsNextMessage.getRootId(), bfsNextMessage.getParentId()); // create new entry in routing table

            BfsNextMessage newMessageToNonParentNeighbors = new BfsNextMessage(bfsNextMessage.getRootId(), node.ID);

            AtomicInteger countMessages = new AtomicInteger(); // count outgoing messages

            // send to neighbors except the parent
            node.getNeighborsIdToNodeMap().forEach((neighborId, neighborNode) -> {
                if (neighborId != bfsNextMessage.getParentId()) { // check not parent
                    node.send(newMessageToNonParentNeighbors, neighborNode); // send to neighbors
                    countMessages.getAndIncrement();
                }
            });

            if (countMessages.get() > 0) { // only if at least 1 message was sent return
                return;
            }
        }
        // otherwise (i.e. already exist routing to initiator or no neighbors except parent): send ack back massage to parent
        Node parentNode = node.getNeighborsIdToNodeMap().get(bfsNextMessage.getParentId());
        BfsBackMessage newMessage = new BfsBackMessage(bfsNextMessage.getRootId());
        node.send(newMessage, parentNode);
    }

    @Override
    public boolean shouldHandleMessage(Message message) {
        return (message instanceof BfsNextMessage);
    }
}
