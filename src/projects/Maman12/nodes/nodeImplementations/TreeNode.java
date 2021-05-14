package projects.Maman12.nodes.nodeImplementations;


import projects.Maman12.nodes.messages.ColorMessage;
import projects.defaultProject.nodes.timers.MessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

import java.awt.*;
import java.util.Arrays;

/**
 * An internal node (or leaf node) of the tree.
 * Note that the leaves are instances of LeafNode, a subclass of this class.
 */
public class TreeNode extends Node {

    private int numOfNodes;
    public TreeNode parent = null; // the parent in the tree, null if this node is the root


    public int getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(int nodeColor) {
        this.nodeColor = nodeColor;
    }

    public static int log2(int N) {

        // calculate log2 N indirectly
        // using log() method

        return (int) (Math.log(N) / Math.log(2));
    }

//    private boolean[] toBinary(int nodeColor) {
//        int bitsNumber = (int) Math.ceil(log2(numOfNodes));
//        final boolean[] binaryColor = new boolean[bitsNumber];
//        for (int i = 0; i < bitsNumber; i++) {
//            binaryColor[bitsNumber - 1 - i] = (1 << i & bitsNumber) != 0;
//        }
//        return binaryColor;
//    }

    public int nodeColor;
    public boolean stop = false;

    @Override
    public void checkRequirements() throws WrongConfigurationException {
    }

    @Override
    public void handleMessages(Inbox inbox) {
        if (parent == null) {
            this.setNodeColor(0);
            for (Edge e : outgoingConnections) {
                if (!e.endNode.equals(parent)) { // don't send it to the parent
                    System.out.println("sending from this.ID " + this.ID + " TO " + e.endNode);
                    send(new ColorMessage(this.getNodeColor()), e.endNode);
                }
            }
        }
        System.out.println("this.getNodeColor() = " + this.getNodeColor());
        if (this.getNodeColor() > 8) {
            while (inbox.hasNext()) {
                Message m = inbox.next();
                if (m instanceof ColorMessage) {
                    if (parent == null || !inbox.getSender().equals(parent)) {
                        continue;// don't consider messages sent by children
                    }
                    String fatherColor = Integer.toBinaryString(((ColorMessage) m).getColor());
                    String nodeColor = Integer.toBinaryString(this.getNodeColor());
                    // fix to same length
                    fatherColor = padWithZeros(fatherColor, Math.max(fatherColor.length(), nodeColor.length()));
                    nodeColor = padWithZeros(nodeColor, Math.max(fatherColor.length(), nodeColor.length()));

                    System.out.println("fatherColor = " + fatherColor);
                    System.out.println("nodeColor = " + nodeColor);
                    int index = 0;
                    char myBitValue = '0';
                    for (int i = 0; i < fatherColor.length(); i++) {
                        if (fatherColor.charAt(i) != nodeColor.charAt(i)) {
                            index = i;
                            myBitValue = nodeColor.charAt(i);
                            break;
                        }
                    }
                    String newColor = Integer.toBinaryString(index) + myBitValue;
                    System.out.println("newColor = " + newColor);
                    this.setNodeColor(Integer.parseInt(newColor, 2));


                    switch (this.getNodeColor()) {

                        case 0:
                            this.setColor(Color.RED);
                            break;

                        case 1:
                            this.setColor(Color.YELLOW);
                            break;

                        case 2:
                            this.setColor(Color.GREEN);
                            break;

                        case 3:
                            this.setColor(Color.BLUE);
                            break;
                        default:
                            this.setColor(Color.LIGHT_GRAY);
                            break;


                    }
                    for (Edge e : outgoingConnections) {
                        if (!e.endNode.equals(parent)) { // don't send it to the parent
                            System.out.println("sending from this.ID " + this.ID + " TO " + e.endNode);
                            send(new ColorMessage(this.getNodeColor()), e.endNode);
                        }
                    }
                }
            }
        }
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

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }
}
