package projects.Maman15.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class DataMessage extends Message {

    final int misNodeId;
    final int destinationNodeId;
    final String data;

    public DataMessage(int misNodeId, int destinationNodeId, String data) {
        this.misNodeId = misNodeId;
        this.destinationNodeId = destinationNodeId;
        this.data = data;
    }

    public int getMisNodeId() {
        return misNodeId;
    }

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public String getData() {
        return data;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }
}
