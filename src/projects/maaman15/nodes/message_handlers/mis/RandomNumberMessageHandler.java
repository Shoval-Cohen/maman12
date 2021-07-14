package projects.maaman15.nodes.message_handlers.mis;

import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.messages.mis.MisRandomNumberMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.messages.Message;

public class RandomNumberMessageHandler extends AbstractHandler {

    public RandomNumberMessageHandler(UDGNode node) {
        super(node);
    }

    @Override
    public void handle(Message message) {
        MisRandomNumberMessage misRandomNumberMessage = (MisRandomNumberMessage) message;

        node.getMISAlgorithmState().getNeighborsRandomRoundNumbers().add(misRandomNumberMessage.getRndNumber()); // update state
    }

    @Override
    public boolean shouldHandleMessage(Message message) {
        return (message instanceof MisRandomNumberMessage);
    }
}
