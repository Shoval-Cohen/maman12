package projects.Maman15.nodes.nodeImplementations;

import projects.Maman15.nodes.messages.ChosenNumberMessage;
import projects.Maman15.nodes.messages.DecideMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

public class UDGNode extends Node {
    int misRounds;
    boolean isActive = false;
    boolean inMis = false;
    int randomNumber;
    int counter = 0;
    java.util.Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();

    public void setMisRounds(int misRounds) {
        this.misRounds = misRounds;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void handleMessages(Inbox inbox) {

        boolean biggest = true;
        if (counter == misRounds){
            inMis = false;
            isActive = false;
        }
        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof DecideMessage) {
                inMis = false;
                isActive = false;
                break;
            } else if (this.isActive && m instanceof ChosenNumberMessage) {
                if (randomNumber < ((ChosenNumberMessage) m).getNumber()) {
                    biggest = false;
                    break;
                }
            }
        }

        if (biggest) {
            inMis = true;
            isActive = false;
            broadcast(new DecideMessage());
        }
    }

    @Override
    public void preStep() {
        randomNumber = rand.nextInt((int) (1 + (Math.pow(misRounds, 10))));

        //sends the chosen number to all neighbours
        broadcast(new ChosenNumberMessage(randomNumber));

    }

    @Override
    public void init() {

    }

    @Override
    public void neighborhoodChange() {

    }

    @Override
    public void postStep() {

    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {

    }


}
