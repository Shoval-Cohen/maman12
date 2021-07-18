package projects.Maman15.nodes.messages;

import projects.Maman12.nodes.messages.IdMessage;
import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class DecideMessage extends Message {
	boolean inMis;

	public boolean isInMis() {
		return inMis;
	}

	public void setInMis(boolean inMis) {
		this.inMis = inMis;
	}

	public DecideMessage() {
	}

	public DecideMessage(boolean inMis) {
		this.inMis = inMis;
	}

	@Override
	public Message clone() {
		return this; // read-only policy 
	}
}
