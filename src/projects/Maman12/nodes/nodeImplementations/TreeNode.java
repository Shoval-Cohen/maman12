package projects.Maman12.nodes.nodeImplementations;


import projects.Maman12.nodes.messages.ColorMessage;
import projects.Maman12.nodes.messages.DecideMessage;
import projects.Maman12.nodes.messages.UndecideMessage;
import projects.defaultProject.nodes.timers.MessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * An internal node (or leaf node) of the tree.
 * Note that the leaves are instances of LeafNode, a subclass of this class.
 */
public class TreeNode extends Node {

    public TreeNode parent = null; // the parent in the tree, null if this node is the root


    public int nodeColor = this.ID;
    Map<Integer, List<Integer>> neighborsInColor = new HashMap<>();

    // Six_VCol variables
    private boolean isFirstStep = true; // if first ever step
    private int numOfColors = Tools.getNodeList().size(); // Max number colors for each Six_VCol round (will reduce by 2log(n) every step)
    private boolean finishedColoring = false; // flag for is VCol_MIS finished

    // VCol_MIS variables
    private int roundColor = 0; // Color round for maximal independent set
    private EMISState state = EMISState.UNDECIDED; // State of node if in or outside the maximal independent set
    private List<Integer> received = new ArrayList<>(); //  mailbox for VCol_MIS
    private boolean finishedMis = false; // finished VCol_MIS


    public int getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(int nodeColor) {
        this.nodeColor = nodeColor;
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
    }

    @Override
    public void handleMessages(Inbox inbox) {
        // Root color is always 0
        if (parent == null) {
            this.setNodeColor(0);
        }

        while (inbox.hasNext()) {
            Message m = inbox.next();
            if (m instanceof ColorMessage && this.getNodeColor() > 8) {
                this.setNodeColor(getNewColorFromFatherColor(((ColorMessage) m).getColor()));
            } else if (m instanceof DecideMessage) {
                state = EMISState.NON_MIS;
                received.add(((DecideMessage) m).getID());
            } else if (m instanceof UndecideMessage) {
                received.add(((UndecideMessage) m).getID());
            }
        }

        if (this.numOfColors >= 8) { // Still coloring
            this.numOfColors = log2(this.numOfColors);
            sendToChildren(new ColorMessage(this.getNodeColor()));
        }
        else if (roundColor <= 8) { // At the MIS stage
            if (this.getNodeColor() == roundColor && state != EMISState.NON_MIS) {
                state = EMISState.IN_MIS;
                sendToNeighbors(new DecideMessage(this.ID));
            } else {
                sendToNeighbors(new UndecideMessage(this.ID));
            }

            if (isReceivedFromAllNeighbors()) {
                if (roundColor < 8) {
                    roundColor++;
                } else {
                    if (state == EMISState.IN_MIS) {
                        setColor(Color.GREEN);
                    } else {
                        setColor(Color.RED);
                    }
                }
                received.clear();
            }
        }
    }

    private boolean isReceivedFromAllNeighbors() {
        for (Edge e : outgoingConnections) {
            if (!received.contains(e.endNode.ID)) {
                return false;
            }
        }
        return true;
    }

    private void sendToNeighbors(Message msg) {
        for (Edge e : outgoingConnections) {
            send(msg, e.endNode);
        }
    }

    private void sendToChildren(Message msg) {
        for (Edge e : outgoingConnections) {
            if (!e.endNode.equals(parent)) {
                send(msg, e.endNode);
            }
        }
    }

    private int log2(int N) {
        return (int) Math.ceil(Math.log(N) / Math.log(2));
    }

    private int getNewColorFromFatherColor(int parentColor) {
        String fatherColor = Integer.toBinaryString(parentColor);
        String nodeColor = Integer.toBinaryString(this.getNodeColor());
        // fix to same length
        fatherColor = padWithZeros(fatherColor, Math.max(fatherColor.length(), nodeColor.length()));
        nodeColor = padWithZeros(nodeColor, Math.max(fatherColor.length(), nodeColor.length()));

        int index = 0;
        char myBitValue = '0';
        for (int k = 0; k < fatherColor.length(); k++) {
            if (fatherColor.charAt(k) != nodeColor.charAt(k)) {
                index = k;
                myBitValue = nodeColor.charAt(k);
                break;
            }
        }
        return Integer.parseInt(Integer.toBinaryString(index) + myBitValue, 2);
    }


    private String padWithZeros(String str, int length) {
        char[] paddingZeros = new char[length - str.length()];
        Arrays.fill(paddingZeros, '0');
        return new String(paddingZeros) + str;
    }

    @Override
    public void init() {
    }

    @Override
    public void neighborhoodChange() {
    }

    @Override
    public void preStep() {
    }

    @Override
    public void postStep() {
    }

    @NodePopupMethod(menuText = "Color children")
    public void colorKids() {
        ColorMessage msg = new ColorMessage();
        MessageTimer timer = new MessageTimer(msg);
        timer.startRelative(1, this);
    }

}
