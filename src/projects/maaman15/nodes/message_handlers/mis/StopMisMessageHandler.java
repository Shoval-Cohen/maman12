package projects.maaman15.nodes.message_handlers.mis;

import projects.maaman15.nodes.algorithms_state.mis.ENodeMisState;
import projects.maaman15.nodes.message_handlers.AbstractHandler;
import projects.maaman15.nodes.messages.mis.MisStopMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.nodes.messages.Message;


public class StopMisMessageHandler extends AbstractHandler {

    public StopMisMessageHandler(UDGNode node) {
        super(node);
    }

    @Override
    protected void handle(Message message) {
        // stop running and notify neighbors
        node.getMISAlgorithmState()
                .setIndependentSetState(ENodeMisState.OUT_MIS)
                .setActive(false);
    }

    @Override
    public boolean shouldHandleMessage(Message message) {
        return (message instanceof MisStopMessage);
    }
}
