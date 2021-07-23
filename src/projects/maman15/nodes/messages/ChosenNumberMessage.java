package projects.maman15.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class ChosenNumberMessage extends Message {

    final int number;

    public ChosenNumberMessage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }

}
