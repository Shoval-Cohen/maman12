package projects.Maman12.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class IdMessage extends Message {

	int ID;

	public int getID() {
		return ID;
	}

	public IdMessage(int ID) {
		this.ID = ID;
	}

	@Override
	public Message clone() {
		return this; // read-only policy 
	}

}
