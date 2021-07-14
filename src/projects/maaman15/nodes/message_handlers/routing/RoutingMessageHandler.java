package projects.maaman15.nodes.message_handlers.routing;

import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.messages.routing.RoutingMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

public class RoutingMessageHandler extends AbstractHandler {

    public RoutingMessageHandler(UDGNode node) {
        super(node);
    }

    @Override
    protected void handle(Message message) {
        RoutingMessage routingMessage = (RoutingMessage) message;

        node.highlight(true);

        if (routingMessage.getDestinationNodeId() == node.ID) { // this is the destination node
            // print to output window and exit
            Tools.showMessageDialog("Node #" + node.ID + " received message from #" +
                    routingMessage.getSourceNodeId() + " (via #" + routingMessage.getAdjacentNodeId() + ") : " + routingMessage.getMessage());
            return;
        }

        // else (i.e. this is not the destination node)
        if (routingMessage.getAdjacentNodeId() == node.ID) { // this is the adjacent node
            Node destinationNode = node.getNeighborsIdToNodeMap().get(routingMessage.getDestinationNodeId()); // get destination
            if (destinationNode != null) {
                node.send(routingMessage, destinationNode); // send to destination
            } else {
                // otherwise no route exist, print massage and end
                Tools.showMessageDialog("There is no route for " + routingMessage);
            }
            return;
        }

        // else: send to the closest node to the MIS adjacent node (according to routing table)
        Integer parentId = node.getBfsRoutingState().getRoutingTable().get(routingMessage.getAdjacentNodeId()); // get parent id for route to adjacent route
        if (parentId != null) { // if there is a possible route
            Node parentNode = node.getNeighborsIdToNodeMap().get(parentId); // get next node
            if (parentNode != null) {
                node.send(routingMessage, parentNode);
                return;
            }
        }

        // otherwise no route exist, print massage and end
        Tools.showMessageDialog("There is no route for " + routingMessage);
    }

    @Override
    public boolean shouldHandleMessage(Message message) {
        return (message instanceof RoutingMessage);
    }
}
