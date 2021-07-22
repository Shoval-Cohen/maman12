package projects.Maman15.nodes.messages;

import javafx.scene.Node;
import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class DataMessage extends Message {

    int misNodeId;
    Node destNode;
    String data;

    DataMessage(int misNodeId, Node destNode, String data) {
        this.misNodeId = misNodeId;
        this.destNode = destNode;
        this.data = data;
    }

    public int getMisNodeId() {
        return misNodeId;
    }

    public Node getDestNode() {
        return destNode;
    }

    public String getData() {
        return data;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }
}
