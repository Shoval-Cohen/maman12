package projects.Maman15.nodes.nodeImplementations;

import projects.Maman15.nodes.messages.BfsMessage;
import projects.Maman15.nodes.messages.ChosenNumberMessage;
import projects.Maman15.nodes.messages.DecideMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDGNode extends Node {
    java.util.Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();


    // MIS args
    int misRounds;
    boolean isActive;
    boolean inMis = false;
    int randomNumber = -1;
    int counter = 1;
    boolean finishedMisStage = false;
    List<Integer> biggerNeighbors = new ArrayList<>();
    List<Integer> biggerDecidedNeighbors = new ArrayList<>();

    // BFS args
    Map<Integer, Integer> routingTableToSrc = new HashMap<>();
    boolean finishedBfsStage;


    public void setMisRounds(int misRounds) {
        this.misRounds = misRounds;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void preStep() {
    }

    @Override
    public String toString() {
        return "Node #" + ID + " {" +
                "misRounds=" + misRounds +
                ", isActive=" + isActive +
                ", inMis=" + inMis +
                ", randomNumber=" + randomNumber +
                ", counter=" + counter +
                ", finishedMisStage=" + finishedMisStage +
                ", biggerDecidedNeighbors=" + biggerDecidedNeighbors +
                ", biggerNeighbors=" + biggerNeighbors +
                '}';
    }

    @Override
    public void handleMessages(Inbox inbox) {
        // The first round is for init and send the random numbers of the nodes
        if (counter == 1) {
            return;
        }

        // True for any candidates to talk with them. not lonely nodes
        // If there is no messages maybe all other neighbors already determined
        // -> it does not mean that this node is the biggest in his neighborhood
        boolean biggest = inbox.hasNext();


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
                if (!isActive) {
                    // Ignore this message
                    continue;
                }

                // Checks the random number that sent at the end of the previous round
                // with the random number of each neighbors that sent his number at the end of the previous round
                if (randomNumber <= ((ChosenNumberMessage) m).getNumber()) {
                    biggest = false;
                }
            } else if (m instanceof BfsMessage) {
                if (this.ID != ((BfsMessage) m).getRootId()
                        && !routingTableToSrc.containsKey(((BfsMessage) m).getRootId())) {
                    // Puts only the node in the shortest way to the root of this message, aka the first one.
                    routingTableToSrc.put(((BfsMessage) m).getRootId(), inbox.getSender().ID);
                    if (routingTableToSrc.keySet().size() == outgoingConnections.size()) {
                        System.out.print("this.ID: " + this.ID + " routingTableToSrc = " + routingTableToSrc);
                        System.out.println(" this.outgoingConnections.size() = " + this.outgoingConnections.size());
                        finishedBfsStage = true;
                    }
                    // Resend this message to all of this node neighbors
                    broadcast(m);
                }
            }
        }

        // One more round then t due to 1 round lag of sending the first number
        if (counter <= misRounds + 1) {
            if (isActive && biggest) {
                determiningMisState(true);
            }
        }

        // End of random rounds.
        else if (isActive) {
            // print this node's state
            System.out.println(this);

            setColor(Color.BLUE);

            // Check if all bigger IDs aren't active, already decides
            if (biggerDecidedNeighbors.containsAll(biggerNeighbors)) {
                System.out.println("biggerNeighbors = " + biggerNeighbors);
                System.out.println("biggerDecidedNeighbors = " + biggerDecidedNeighbors);
                // If no bigger neighbors is in MIS S add this to MIS.
                determiningMisState(true);
            }
        }
    }

    private void determiningMisState(boolean inMis) {
        System.out.println("Setting this.ID " + ID + " to be " + (inMis? "in": "out") + " MIS");
        this.inMis = inMis;
        isActive = false;
        setColor(inMis ? Color.GREEN : Color.RED);
        if (outgoingConnections.size() > 0) {
            broadcast(new DecideMessage(inMis));
        }
        finishedMisStage = true;

        if (inMis) {
            // Start BFS
            if (outgoingConnections.size() > 0) {
                broadcast(new BfsMessage(this.ID));
            }
            routingTableToSrc.put(this.ID, this.ID);
        }
    }


    private void printMessages(Inbox inbox) {
        inbox.reset();
        while (inbox.hasNext()) {
            Message m1 = inbox.next();
            System.out.println(ID + " got MSG: " + m1);
        }
        inbox.reset();
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

            System.out.println("this.ID = " + this.ID);
            System.out.println("biggerDecidedNeighbors = " + biggerDecidedNeighbors);
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

    @Override
    public void checkRequirements() throws WrongConfigurationException {

    }


}
