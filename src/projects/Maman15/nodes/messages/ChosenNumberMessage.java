package projects.Maman15.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to neighbors with the chosen number.
 */
public class ChosenNumberMessage extends Message {

    int number;

    public int getNumber() {
        return number;
    }

    public ChosenNumberMessage(int number) {
        this.number = number;
    }

    @Override
    public Message clone() {
        return this; // read-only policy
    }

}
