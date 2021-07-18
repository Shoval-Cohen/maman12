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
import java.util.List;

public class UDGNode extends Node {
    int misRounds;
    boolean isActive = false;
    boolean inMis = false;
    int randomNumber = -1;
    int counter = 0;
    boolean firstRound;

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
        boolean biggest = inbox.hasNext();

        printMessages(inbox);
        if (firstRound) {
            firstRound = false;
            return;
        }
        while (isActive && inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof DecideMessage) {
                inMis = false;
                isActive = false;
                biggest = false;
                setColor(Color.RED);
                break;
            } else if (m instanceof ChosenNumberMessage) {
                if (randomNumber <= ((ChosenNumberMessage) m).getNumber()) {
                    System.out.println("this ID " + ID + " Should not be GREEN");
                    biggest = false;
                }
            }
        }


        if (isActive && biggest) {
            inMis = true;
            isActive = false;
            System.out.println("Setting " + ID + " color to true");
            printMessages(inbox);
            setColor(Color.GREEN);
            broadcast(new DecideMessage());
        }

        if (isActive && counter >= misRounds) {
            setColor(Color.BLUE);
        }


        counter++;
    }

    private void printMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message m1 = inbox.next();
            if (m1 instanceof ChosenNumberMessage) {
                System.out.println("((ChosenNumberMessage) m1).getNumber() = " + ((ChosenNumberMessage) m1).getNumber());
                System.out.println("this.randomNumber = " + this.randomNumber);
                System.out.println("this rand num is " + (randomNumber < ((ChosenNumberMessage) m1).getNumber() ? "not" : ""
                ) + " bigger");
            }
        }
        inbox.reset();
    }


    @Override
    public void init() {
        firstRound = true;
        setColor(Color.BLACK);
        isActive = true;
        counter = 0;
        for (Edge edge : outgoingConnections) {
            if (edge.endNode.ID > this.ID) {
                biggerNeighbors.add(edge.endNode.ID);
            }
        }
        System.out.println("this.ID = " + this.ID);
        System.out.println("biggerNeighbors = " + biggerNeighbors.toString());
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
