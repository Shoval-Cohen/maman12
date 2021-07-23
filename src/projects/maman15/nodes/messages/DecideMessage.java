package projects.maman15.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class DecideMessage extends Message {
    boolean inMis;

    public DecideMessage() {
    }

    public DecideMessage(boolean inMis) {
        this.inMis = inMis;
    }

    public boolean isInMis() {
        return inMis;
    }

    public void setInMis(boolean inMis) {
        this.inMis = inMis;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }
}
