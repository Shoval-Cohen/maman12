package projects.maman15.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class DataMessage extends Message {

    final int misNodeId;
    final Node destinationNode;
    final String data;

    public DataMessage(int misNodeId, Node destinationNode, String data) {
        this.misNodeId = misNodeId;
        this.destinationNode = destinationNode;
        this.data = data;
    }

    public int getMisNodeId() {
        return misNodeId;
    }

    public Node getDestinationNode() {
        return destinationNode;
    }

    public String getData() {
        return data;
    }


    @Override
    public String toString() {
        return "DataMessage{" +
                "misNodeId=" + misNodeId +
                ", destinationNode=" + destinationNode +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }
}
