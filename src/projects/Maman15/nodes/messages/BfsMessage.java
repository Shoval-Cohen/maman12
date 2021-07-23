package projects.Maman15.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class BfsMessage extends Message {

    final int rootId;

    public BfsMessage(int rootId) {
        this.rootId = rootId;
    }

    public int getRootId() {
        return rootId;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }

}
