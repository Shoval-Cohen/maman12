package projects.Maman15.nodes.nodeImplementations;

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
    int misRounds;
    boolean isActive;
    boolean inMis = false;
    int randomNumber = -1;
    int counter = 1;
    boolean firstRound;
    boolean finishedMisStage = false;

    Map<Integer, Boolean> biggerNeighborsMisStatus = new HashMap<>();

    List<Integer> biggerNeighbors = new ArrayList<>();
    java.util.Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();

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
    public void handleMessages(Inbox inbox) {
        // True for any candidates to talk with them.
        // If there is no messages maybe all other neighbors already determinated
        // -> it does not mean that this node is the biggest in his neighborhood
        boolean biggest = inbox.hasNext();

        if (firstRound) {
            firstRound = false;
            return;
        }

        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof DecideMessage) {
                if (this.ID < inbox.getSender().ID) {
                    biggerNeighborsMisStatus.put(inbox.getSender().ID, ((DecideMessage) m).isInMis());
                }
                if (((DecideMessage) m).isInMis()) {
                    determiningMisState(false);
                }
            } else if (m instanceof ChosenNumberMessage && isActive) {
                if (randomNumber <= ((ChosenNumberMessage) m).getNumber()) {
                    biggest = false;
                }
            }
        }


        if (isActive && biggest) {
            determiningMisState(true);
        }

        if (isActive && counter >= misRounds) {
            printMessages(inbox);
            setColor(Color.BLUE);
            // check if all bigger IDs aren't active
            if (biggerNeighborsMisStatus.keySet().containsAll(biggerNeighbors)) {
                // if no bigger neighbors is in MIS S add this to MIS. opposite it there is bigger neighbors in S
                determiningMisState(biggerNeighborsMisStatus.values().stream().noneMatch(x -> x));
            }
        }


        counter++;
    }

    private void determiningMisState(boolean inMis, boolean lonelyNode) {
        System.out.println("Setting this.ID " + ID + " color");
        this.inMis = inMis;
        isActive = false;
        setColor(inMis ? Color.GREEN : Color.RED);
        if (!lonelyNode) {
            broadcast(new DecideMessage(inMis));
        }
        finishedMisStage = true;
    }

    private void determiningMisState(boolean inMis) {
        determiningMisState(inMis, false);

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
            determiningMisState(true, true);
        } else {
            firstRound = true;
            setColor(Color.BLACK);
            isActive = true;
            biggerNeighbors.clear();
            biggerNeighborsMisStatus.clear();
            counter = 0;
            for (Edge edge : outgoingConnections) {
                if (edge.endNode.ID > this.ID) {
                    biggerNeighbors.add(edge.endNode.ID);
                }
            }
            System.out.println("this.ID = " + this.ID);
            System.out.println("biggerNeighborsMisStatus = " + biggerNeighborsMisStatus.keySet());
        }
    }

    @Override
    public void neighborhoodChange() {

    }

    @Override
    public void postStep() {
        if (counter <= misRounds) {
            randomNumber = rand.nextInt((int) (1 + (Math.pow(misRounds, 10))));

            //sends the chosen number to all neighbours
            broadcast(new ChosenNumberMessage(randomNumber));
        }

    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {

    }


}
