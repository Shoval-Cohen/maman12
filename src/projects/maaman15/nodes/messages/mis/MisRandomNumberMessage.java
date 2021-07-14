package projects.maaman15.nodes.messages.mis;

import sinalgo.nodes.messages.Message;

/**
 * A node will send this message to all of his neighbors as a part of the random steps in the MIS algorithm.
 */
public class MisRandomNumberMessage extends Message {
    private final int rndNumber;

    public MisRandomNumberMessage(int rndNumber) {
        this.rndNumber = rndNumber;
    }

    public int getRndNumber() {
        return rndNumber;
    }

    @Override
    public Message clone() {
        return new MisRandomNumberMessage(rndNumber);
    }
}
