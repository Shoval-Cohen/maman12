package projects.maaman15.nodes.messages.mis;

import sinalgo.nodes.messages.Message;

/**
 * A node will send this message to all of his neighbors to marks them that he entered the MIS (they mustn't be in)
 */
public class MisStopMessage extends Message {
    @Override
    public Message clone() {
        return new MisStopMessage();
    }
}
