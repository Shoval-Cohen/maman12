package projects.Maman15.nodes.messages;

import projects.Maman12.nodes.messages.IdMessage;
import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class DecideMessage extends Message {
	@Override
	public Message clone() {
		return this; // read-only policy 
	}
}
