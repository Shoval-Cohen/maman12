package projects.maaman15.nodes.message_handlers;

import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.messages.Message;

/**
 * This is an interface for handling specific type of messages.
 */
public abstract class AbstractHandler {

    protected UDGNode node; // current node

    public AbstractHandler(UDGNode node) {
        this.node = node;
    }

    /**
     * Checks if should handle message, if so calls {@link AbstractHandler#handle(Message)}.
     * Otherwise, ignores message.
     *
     * @param message the message that was received
     */
    public void handleMessage(Message message) {
        if (this.shouldHandleMessage(message)) {
            handle(message);
        }
    }

    /**
     * This is the main method - it will define what to do when receiving the message
     *
     * @param message the message that was received
     */
    protected abstract void handle(Message message);

    /**
     * This method will check and return if the message should be handled
     *
     * @param message the message that was received
     * @return boolean indicator for if the message should be handled or not
     */
    public abstract boolean shouldHandleMessage(Message message);

}
