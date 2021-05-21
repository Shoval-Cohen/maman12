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


    private int nodeColor = this.ID;

    // Coloring Algorithm variables
    private int numOfColors = Tools.getNodeList().size(); // Max number colors for each Six_VCol round (will reduce by 2log(n) every step)

    // MIS Algorithm variables
    private int roundColor = 0;
    private EMISState state = EMISState.UNDECIDED; // State of node at the maximal independent set
    private final List<Integer> received = new ArrayList<>(); //  mailbox for VCol_MIS


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
                // Get new color with diff between color and father color
                this.setNodeColor(getNewColorFromFatherColor(((ColorMessage) m).getColor()));
            } else if (m instanceof DecideMessage) {
                // If one of the neighbors entered the MIS, this node will not be in the MIS.
                state = EMISState.NON_MIS;
                received.add(((DecideMessage) m).getID());
            } else if (m instanceof UndecideMessage) {
                received.add(((UndecideMessage) m).getID());
            }
        }

        if (this.numOfColors > 0b111) { // Still coloring
            // reduce number. index of binary view of the color is at range from 0 to log2N.
            // The another bit of the index value multiply all at 2.
            this.numOfColors = 2 * log2(this.numOfColors);
            // Informing children the new color
            sendToChildren(new ColorMessage(this.getNodeColor()));
        } else if (roundColor <= 0b111) { // MIS stage
            // My round and I didn't decided yet
            if (this.getNodeColor() == roundColor && state != EMISState.NON_MIS) {
                // I'm in!
                state = EMISState.IN_MIS;
                // All of the neighbors should know it.
                sendToNeighbors(new DecideMessage(this.ID));
            } else {
                // I didn't decided yet
                sendToNeighbors(new UndecideMessage(this.ID));
            }

            if (isReceivedFromAllNeighbors()) {
                // The alg didn't finished
                if (roundColor < 0b111) {
                    // Let's get to the new round!
                    roundColor++;
                } else { // Alg finished
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

    /**
     * Send the @msg to all neighbors
     * @param msg - message to send
     */
    private void sendToNeighbors(Message msg) {
        for (Edge e : outgoingConnections) {
            send(msg, e.endNode);
        }
    }

    /**
     * Send the @msg to all children
     * @param msg - message to send
     */
    private void sendToChildren(Message msg) {
        for (Edge e : outgoingConnections) {
            if (!e.endNode.equals(parent)) {
                send(msg, e.endNode);
            }
        }
    }

    /**
     * @param N - num
     * @return the log base 2 ceil up
     */
    private int log2(int N) {
        return (int) Math.ceil(Math.log(N) / Math.log(2));
    }


    /**
     * Finds the new color by the algorithm method.
     * Get's the first index of different bit of the colors, and concat it to this bit value
     *
     * @param parentColor - the color from the parent
     * @return the new color
     */
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


    /**
     * @param str String to pad
     * @param length full length wanted
     * @return the string padded with zeros if needed to fullLength wanted
     */
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
