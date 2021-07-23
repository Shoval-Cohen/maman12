package projects.Maman15.nodes.nodeImplementations;

import projects.Maman15.nodes.messages.BfsMessage;
import projects.Maman15.nodes.messages.ChosenNumberMessage;
import projects.Maman15.nodes.messages.DataMessage;
import projects.Maman15.nodes.messages.DecideMessage;
import projects.defaultProject.nodes.timers.MessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDGNode extends Node {
    final java.util.Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();

    // MIS args
    int misRounds;
    boolean isActive = true;
    int randomNumber = -1;
    int counter = 1;

    final List<Integer> biggerNeighbors = new ArrayList<>();
    final List<Integer> biggerDecidedNeighbors = new ArrayList<>();

    // BFS args
    final Map<Integer, Node> routingTable = new HashMap<>();

    public void setMisRounds(int misRounds) {
        this.misRounds = misRounds;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<Integer, Node> getRoutingTable() {
        return routingTable;
    }

    @Override
    public void preStep() {
    }

    @Override
    public String toString() {
        return "UDGNode{" +
                "misRounds=" + misRounds +
                ", isActive=" + isActive +
                ", randomNumber=" + randomNumber +
                ", counter=" + counter +
                ", biggerNeighbors=" + biggerNeighbors +
                ", biggerDecidedNeighbors=" + biggerDecidedNeighbors +
                ", routingTable=" + routingTable +
                '}';
    }

    @Override
    public void handleMessages(Inbox inbox) {
        // The first round is for init and send the random numbers of the nodes
        if (counter == 1) {
            return;
        }

        boolean biggest = true;
        boolean numbersArrived = false;

        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof DecideMessage) {
                if (isActive && ((DecideMessage) m).isInMis()) {
                    determiningMisState(false);
                }
                if (this.ID < inbox.getSender().ID) {
                    biggerDecidedNeighbors.add(inbox.getSender().ID);
                }
            } else if (m instanceof ChosenNumberMessage) {
                numbersArrived = true;
                if (!isActive) {
                    // Ignore this message
                    continue;
                }

                // Checks the random number that sent at the end of the previous round
                // with the random number of each neighbors that sent his number at the end of
                // the previous round
                if (randomNumber <= ((ChosenNumberMessage) m).getNumber()) {
                    biggest = false;
                }
            } else if (m instanceof BfsMessage) {
                // Checks if it's the first time that this node gets BFS message from this root node
                if (this.ID != ((BfsMessage) m).getRootId()
                        && !routingTable.containsKey(((BfsMessage) m).getRootId())) {
                    // Puts only the node in the shortest way to the root of this message, aka the
                    // first one.
                    routingTable.put(((BfsMessage) m).getRootId(), inbox.getSender());
                    // Resend this message to all of this node neighbors
                    broadcast(m);
                }
            } else if (m instanceof DataMessage) {
                this.highlight(true);
                DataMessage dataMessage = (DataMessage) m;

                // If the message arrived to the destination node, print the message and finish
                if (this.ID == dataMessage.getDestinationNodeId()) {
                    Tools.showMessageDialog("Message arrived successfully to node with ID #" + this.ID
                            + " and with data: [" + dataMessage.getData() + "]");
                }
                // If the message arrived to the mis node,
                // it should send the message to the destination node that should be his direct neighbor
                else if (this.ID == dataMessage.getMisNodeId()) {
                    Node destinationNode = new UDGNode();
                    for (Edge edge : this.outgoingConnections) {
                        if (edge.endNode.ID == dataMessage.getDestinationNodeId()) {
                            destinationNode = edge.endNode;
                            break;
                        }
                    }
                    send(dataMessage, destinationNode);
                } else {
                    // Routing the message towards the MIS node
                    send(dataMessage, this.routingTable.get(dataMessage.getMisNodeId()));
                }
            }
        }

        // One more round then t due to 1 round lag of sending the first number
        if (counter <= misRounds + 1) {
            if (isActive && numbersArrived && biggest) {
                determiningMisState(true);
            }
        }

        // End of random rounds.
        else if (isActive) {
            // coloring the nodes at this stage in blue
            setColor(Color.BLUE);

            // Check if all bigger IDs aren't active, already decides
            if (biggerDecidedNeighbors.containsAll(biggerNeighbors)) {
                // Got here just if there is no MIS neighbor.
                // If there is MIS neighbor he will send @DecideMessage to this node
                determiningMisState(true);
            }
        }
    }

    private void determiningMisState(boolean inMis) {
        this.isActive = false;
        setColor(inMis ? Color.GREEN : Color.RED);
        if (outgoingConnections.size() > 0) {
            broadcast(new DecideMessage(inMis));
        }

        if (inMis) {
            // Start BFS
            if (outgoingConnections.size() > 0) {
                broadcast(new BfsMessage(this.ID));
            }
        }
    }

    @Override
    public void init() {
        if (outgoingConnections.size() == 0) {
            determiningMisState(true);
        } else {
            counter = 1;
            isActive = true;
            setColor(Color.BLACK);

            biggerNeighbors.clear();
            biggerDecidedNeighbors.clear();

            for (Edge edge : outgoingConnections) {
                if (edge.endNode.ID > this.ID) {
                    biggerNeighbors.add(edge.endNode.ID);
                }
            }
        }
    }

    @Override
    public void neighborhoodChange() {

    }

    @Override
    public void postStep() {
        counter++;

        if (counter <= misRounds) {
            randomNumber = rand.nextInt((int) (1 + (Math.pow(misRounds, 10))));

            // sends the chosen number to all neighbours
            broadcast(new ChosenNumberMessage(randomNumber));
        }

    }

    /*
     * initiated from node and selects one node and his MIS neighbor
     */
    @NodePopupMethod(menuText = "Route message")
    public void routeMessage() {
        Tools.getNodeSelectedByUser(destinationNode -> {
            if (destinationNode == null) {
                return; // the user aborted
            }
            Tools.getNodeSelectedByUser(misNode -> {
                if (misNode == null) {
                    return; // the user aborted
                }
                routeMessage(misNode, destinationNode);
            }, "Select a MIS node to which you want to send the MSG.");
        }, "Select a node to which you want to send the MSG.");

    }

    void routeMessage(Node misNode, Node destinationNode) {

        if (Tools.getNumberOfMessagesSentInThisRound() > 0) {
            Tools.showMessageDialog("The preprocess haven't finished yet, try again later");
            return;
        }
        if (!this.routingTable.containsKey(misNode.ID)) {
            Tools.showMessageDialog("There is no path to the MIS node with ID #" + misNode.ID);
            return;
        }

        this.highlight(true);
        String data = Tools.showQueryDialog("Enter the data you want to route");

        // Sends msg to to the MIS node via routing
        DataMessage dataMessage = new DataMessage(misNode.ID, destinationNode.ID, data);
        System.out.print("this.ID = " + this.ID);
        System.out.println(" dataMessage = " + dataMessage);
        System.out.println("this.routingTableToSrc.get(misNode.ID) = " + this.routingTable.get(misNode.ID));
        sendDataMessage(dataMessage, this.routingTable.get(misNode.ID));
    }


    /**
     * Sends a data message to a neighbor.
     *
     * @param dataMessage The data message.
     * @param to          Receiver node.
     */
    private void sendDataMessage(DataMessage dataMessage, Node to) {

        // In Synchronous mode, a node is only allowed to send messages during the
        // execution of its step. We can easily schedule to send this message during the
        // next step by setting a timer. The MessageTimer from the default project already
        // implements the desired functionality.
        new MessageTimer(dataMessage, to).startRelative(Tools.getRandomNumberGenerator().nextDouble(), this);

    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {

    }


}
