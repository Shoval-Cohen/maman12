package projects.Maman12.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class DecideMessage extends IdMessage {

	public DecideMessage(int ID) {
		super(ID);
	}

	@Override
	public Message clone() {
		return this; // read-only policy 
	}

}
