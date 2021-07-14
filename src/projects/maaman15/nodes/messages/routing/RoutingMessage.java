package projects.maaman15.nodes.messages.routing;

import sinalgo.nodes.messages.Message;

/**
 * A message a node A needs to pass to node B through an adjacent node S that is in the MIS
 */
public class RoutingMessage extends Message {
    private final int sourceNodeId; // source node id
    private final int destinationNodeId; // destination node id
    private final int adjacentNodeId; // adjacent node id (in the MIS and a neighbor of destination node)
    private final String message; // the message

    public RoutingMessage(int sourceNodeId, int destinationNodeId, int adjacentNodeId, String message) {
        this.sourceNodeId = sourceNodeId;
        this.destinationNodeId = destinationNodeId;
        this.adjacentNodeId = adjacentNodeId;
        this.message = message;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public int getAdjacentNodeId() {
        return adjacentNodeId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Message clone() {
        return new RoutingMessage(sourceNodeId, destinationNodeId, adjacentNodeId, message);
    }

    @Override
    public String toString() {
        return "RoutingMessage{" +
                "sourceNodeId=" + sourceNodeId +
                ", destinationNodeId=" + destinationNodeId +
                ", adjacentNodeId=" + adjacentNodeId +
                ", message='" + message + '\'' +
                '}';
    }
}
