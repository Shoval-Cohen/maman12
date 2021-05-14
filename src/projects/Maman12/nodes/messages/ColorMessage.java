package projects.Maman12.nodes.messages;

import sinalgo.nodes.messages.Message;

/**
 * A message sent to children that should be marked.
 */
public class ColorMessage extends Message {

	int color;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public ColorMessage() {
		super();
	}

	public ColorMessage(int color) {
		this.color = color;
	}

	@Override
	public Message clone() {
		return this; // read-only policy 
	}

}
