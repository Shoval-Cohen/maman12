package projects.maaman15.nodes.message_handlers.bfs_routing;

import projects.maaman15.nodes.algorithms_state.bfs_routing.BfsRoutingAlgorithmState;
import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.messages.bfs_routing.BfsBackMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;


public class BfsBackMessageHandler extends AbstractHandler {

    public BfsBackMessageHandler(UDGNode node) {
        super(node);
    }

    @Override
    protected void handle(Message message) {
        BfsBackMessage backMessage = (BfsBackMessage) message;
        BfsRoutingAlgorithmState bfsState = node.getBfsRoutingState();

        int rootId = backMessage.getRoot();
        int currCount = bfsState.increaseAndGetAckCounter(rootId); // update counter and get new value
        int numNeighbors = node.getNeighborsIdToNodeMap().size();

        if (node.ID == rootId) { // if current node is the initiator of the bfs
            if (currCount == numNeighbors) { // check if all of the neighbors are done
                node.getBfsRoutingState().setActive(false); // update finished routing table creation with bfs
            }
            return;
        }
        // else
        if (currCount == numNeighbors - 1) { // check if all of the neighbors expect parent are done
            BfsBackMessage ackMessageToParent = new BfsBackMessage(rootId);

            // update parent that this node and all his sons are done
            int parentId = node.getBfsRoutingState().getRoutingTable().get(rootId);
            Node parentNode = node.getNeighborsIdToNodeMap().get(parentId); // get parent
            node.send(ackMessageToParent, parentNode);
        }
    }

    @Override
    public boolean shouldHandleMessage(Message message) {
        return (message instanceof BfsBackMessage);
    }
}
