package projects.maaman15;


import projects.defaultProject.models.connectivityModels.UDG;
import projects.defaultProject.models.distributionModels.Random;
import projects.defaultProject.models.interferenceModels.NoInterference;
import projects.defaultProject.models.mobilityModels.NoMobility;
import projects.defaultProject.models.reliabilityModels.ReliableDelivery;
import projects.maaman15.custom_config.CustomConfig;
import projects.maaman15.nodes.algorithms_state.mis.ENodeMisState;
import projects.maaman15.nodes.message_handlers.routing.RoutingMessageHandler;
import projects.maaman15.nodes.messages.routing.RoutingMessage;
import projects.maaman15.nodes.nodeImplementations.UDGNode;
import sinalgo.models.ConnectivityModelHelper;
import sinalgo.models.InterferenceModel;
import sinalgo.models.MobilityModel;
import sinalgo.models.ReliabilityModel;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.messages.Packet;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.runtime.Runtime;
import sinalgo.tools.Tools;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


// todo: check all todos
// todo: check all docs
// todo: check warnings and cleanup code

public class CustomGlobal extends AbstractCustomGlobal {

    private int numberOfNodes; // number of nodes in UDG
    private int maxNumberOfRandomRounds; // maximum number of random rounds in MIS algorithm.
    private final Map<Integer, UDGNode> nodeIdToNodeMap = new ConcurrentHashMap<>(); // id to node map

    public boolean hasTerminated() {
        return false;
    }

    /**
     * Button to create a UDG graph.
     */
    @CustomButton(buttonText = "Create UDG", toolTipText = "Builds a new unit disc graph")
    public void createUDGButton() {
        int numberOfNodes = Integer.parseInt(Tools.showQueryDialog("Number of nodes:"));
        int maxNumberOfRandomRounds = Integer.parseInt(Tools.showQueryDialog("Maximum rounds for random mis algorithm stage:"));
        buildUDGGraph(numberOfNodes, maxNumberOfRandomRounds);
    }

    /**
     * Build new graph with same properties as the old graph (in terms of #nodes and max random rounds)
     */
    @CustomButton(buttonText = "Reset", toolTipText = "Create new graph with the same size")
    public void resetGraph() {
        buildUDGGraph(numberOfNodes, maxNumberOfRandomRounds);
    }

    /**
     * Button to send a message via the routing tables within the UDG graph.
     * Please notice that this can be done only after the MIS and routing is done.
     * Checkout {@link CustomGlobal#sendMessageViaRoute(int, int, int, String)} for more details.
     */
    @CustomButton(buttonText = "Send message", toolTipText = "Send a message via routing tables")
    public void createRouteButton() {
        // reset gui to original state
        resetHighlightAndMISColors();
        Tools.repaintGUI();

        if (!isDonePreviousStepsForRoutingAMessage()) return; // check previous steps are done

        int sourceNodeId = Integer.parseInt(Tools.showQueryDialog("Source node:"));
        int destinationId = Integer.parseInt(Tools.showQueryDialog("Destination node:"));
        int adjacentId = Integer.parseInt(Tools.showQueryDialog("Adjacent nodeId:"));
        String messageToSend = Tools.showQueryDialog("The message to send:");

        if (messageToSend == null) {
            messageToSend = "";
        }

        sendMessageViaRoute(sourceNodeId, destinationId, adjacentId, messageToSend);
    }

    // tools
    @CustomButton(buttonText = "MIS Nodes", toolTipText = "Prints to output box the MIS node ids")
    public void printIdForMIS() {
        List<Integer> misIds = nodeIdToNodeMap.values().stream()
                .filter(udgNode -> udgNode.getMISAlgorithmState().isInIndependentSet())
                .map(node -> node.ID).collect(Collectors.toList());
        Tools.appendToOutput("\n### MIS ids: (size = " + misIds.size() + "):\n" + misIds.toString());
    }

    @CustomButton(buttonText = "Biggest routing table", toolTipText = "Prints to output box the node with the biggest routing table")
    public void printRoutingTableData() {
        Optional<UDGNode> maxSize = nodeIdToNodeMap.values().stream()
                .max(Comparator.comparingInt(node -> node.getBfsRoutingState().getRoutingTable().size()));
        maxSize.ifPresent(node ->
                Tools.appendToOutput("\n#" + node.ID + " is the node with biggest routing table -> Size = " +
                        node.getBfsRoutingState().getRoutingTable().size()));
    }

    @CustomButton(buttonText = "Max degree", toolTipText = "Prints to output box the node with the biggest degree")
    public void printMaxDegree() {
        Optional<UDGNode> maxSize = nodeIdToNodeMap.values().stream()
                .max(Comparator.comparingInt(node -> node.getNeighborsIdToNodeMap().size()));
        maxSize.ifPresent(node ->
                Tools.appendToOutput("\n#" + node.ID + " is the node the must neighbors, degree = " +
                        node.getNeighborsIdToNodeMap().size()));
    }

    // graph implementations

    /**
     * Build new unit disc graph for the specified number of nodes (remove old ones if exist).
     * First, the method will check the correctness of the parameters with {@link CustomGlobal#isInputValidForUDG(int, int)},
     * if invalid it will not change anything.
     * <p>
     * By hitting the run button, two operations will be executed:
     * </p>
     * 1. MIS algorithm (random and then deterministic stages)
     * 2. Then, each node in the MIS will initiate routing table build with BFS algorithm.
     *
     * @param numberOfNodes           the number of nodes in the graph
     * @param maxNumberOfRandomRounds maximal number of MIS random rounds
     */
    public void buildUDGGraph(int numberOfNodes, int maxNumberOfRandomRounds) {
        // input check
        if (!isInputValidForUDG(numberOfNodes, maxNumberOfRandomRounds)) return;

        // remove all nodes (if any)
        Runtime.clearAllNodes();
        nodeIdToNodeMap.clear();

        // update variables values
        this.numberOfNodes = numberOfNodes;
        this.maxNumberOfRandomRounds = maxNumberOfRandomRounds;

        // initializing random position object and other project nodes settings
        Random rndPosition = new Random();
        ConnectivityModelHelper udg = new UDG(CustomConfig.getInstance().getMaxUDGRadius());
        MobilityModel mobilityModel = new NoMobility();
        ReliabilityModel reliabilityModel = new ReliableDelivery();
        InterferenceModel interferenceModel = new NoInterference();

        // create the nodes
        for (int i = 0; i < numberOfNodes; i++) {
            UDGNode currNode = new UDGNode(numberOfNodes, maxNumberOfRandomRounds);

            // assign random position
            currNode.setPosition(rndPosition.getNextPosition());
            // set other project settings
            currNode.setConnectivityModel(udg);
            currNode.setMobilityModel(mobilityModel);
            currNode.setReliabilityModel(reliabilityModel);
            currNode.setInterferenceModel(interferenceModel);

            // save and add to runtime
            Runtime.addNode(currNode);
            nodeIdToNodeMap.put(currNode.ID, currNode);
        }

        // add connections, i.e. add edges for nodes that are insides each other radius
        Runtime.reevaluateConnections();
        // Repaint the GUI as we have added some nodes
        Tools.repaintGUI();
    }

    /**
     * Send a message from node A to node B through an adjacent node S that is in the MIS.
     * First, the method will check the correctness of the parameters with {@link CustomGlobal#isInputValidForRouting(int, int, int)},
     * if invalid it will not change anything.
     * <p>
     * By hitting the run button the message will be transmitted from source node to destination node by using the routing tables.
     * When there isn't a route between the nodes, the user will by informed (checkout {@link RoutingMessageHandler#handleMessage(Message)}).
     * </p>
     *
     * @param sourceNodeId  the source id (should exist)
     * @param destinationId the destination id (should exist)
     * @param adjacentId    the adjacent id (should exist and be in MIS)
     */
    public void sendMessageViaRoute(int sourceNodeId, int destinationId, int adjacentId, String messageToSend) {
        // check input
        if (!isInputValidForRouting(sourceNodeId, destinationId, adjacentId)) return;

        setColorsToRoutingAMessageNodes(sourceNodeId, destinationId, adjacentId);
        // Repaint the GUI
        Tools.repaintGUI();

        // send mock message to source to start the routing
        UDGNode sourceNode = nodeIdToNodeMap.get(sourceNodeId);
        RoutingMessage routingMessage = new RoutingMessage(sourceNodeId, destinationId, adjacentId, messageToSend);
        Packet packet = Packet.fabricatePacket(routingMessage);
        sourceNode.getInboxPacketBuffer().addPacket(packet);
    }

    /**
     * Check if the input is valid for building a UDG graph.
     * If invalid show the user a message with details.
     *
     * @param numNodes                the required number of nodes
     * @param maxNumberOfRandomRounds the maximum random rounds of MIS
     * @return is valid indicator
     */
    private boolean isInputValidForUDG(int numNodes, int maxNumberOfRandomRounds) {
        int minimumNumNodes = CustomConfig.getInstance().getMinimumNodes();

        if (numNodes < minimumNumNodes) {
            Tools.showMessageDialog("The number of nodes needs to be at least " + minimumNumNodes + ".\nCreation of unit disc graph aborted.");
            return false;
        }

        if (maxNumberOfRandomRounds < 1) {
            Tools.showMessageDialog("The number of rounds needs to be at least 1.\nCreation of unit disc graph aborted.");
            return false;
        }
        return true;
    }


    /**
     * Check if the input is valid for routing message within the graph.
     * If invalid show the user a message with details.
     *
     * @param sourceNodeId  the source id (should exist)
     * @param destinationId the destination id (should exist)
     * @param adjacentId    the adjacent id (should exist and be in MIS)
     * @return is valid indicator
     */
    public boolean isInputValidForRouting(int sourceNodeId, int destinationId, int adjacentId) {

        for (int nodeId : new int[]{sourceNodeId, destinationId, adjacentId}) {
            if (!nodeIdToNodeMap.containsKey(nodeId)) {
                Tools.showMessageDialog("Node #" + sourceNodeId + " isn't in the graph.\nSend message via routing tables aborted.");
                return false;
            }
        }
        UDGNode adjacentNode = nodeIdToNodeMap.get(adjacentId);
        if (!adjacentNode.getMISAlgorithmState().isInIndependentSet()) {
            Tools.showMessageDialog("Adjacent node (#" + adjacentId + ") isn't in MIS.\nSend message via routing tables aborted.");
            return false;
        }
        if (!adjacentNode.getNeighborsIdToNodeMap().containsKey(destinationId)) {
            Tools.showMessageDialog("Adjacent node (#" + adjacentId + ") isn't a neighbor of destination node (#" + destinationId + ").\nSend message via routing tables aborted.");
            return false;
        }
        return true;
    }

    /**
     * Check and return if all of the nodes have finished MIS and routing.
     * If not a message will be shown to the user.
     */
    public boolean isDonePreviousStepsForRoutingAMessage() {
        if (nodeIdToNodeMap.size() == 0 || !isDoneMIS() || !isDoneRouting()) {
            Tools.showMessageDialog("Please wait for the MIS and routing to be finished (Press the \"run\" button).");
            return false;
        }
        return true;
    }

    /**
     * Return if all of the nodes finished their MIS algorithm.
     */
    public boolean isDoneMIS() {
        for (UDGNode node : nodeIdToNodeMap.values()) {
            if (node.getMISAlgorithmState().getIndependentSetState() == ENodeMisState.NON_FINAL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return if all of the nodes finished building their routing tables.
     */
    public boolean isDoneRouting() {
        for (UDGNode node : nodeIdToNodeMap.values()) {
            if (node.getMISAlgorithmState().isInIndependentSet() &&
                    node.getBfsRoutingState().isActive()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Set color to those nodes. The colors will be as such:
     * 1. source - CYAN
     * 2. destination - ORANGE
     * 3. adjacent - PINK
     * 4. route node - will be highlighted (only after running the routing)
     *
     * @param sourceNodeId  the source id (should exist)
     * @param destinationId the destination id (should exist)
     * @param adjacentId    the adjacent id (should exist and be in MIS)
     */
    public void setColorsToRoutingAMessageNodes(int sourceNodeId, int destinationId, int adjacentId) {
        nodeIdToNodeMap.get(sourceNodeId).setColor(Color.CYAN);
        nodeIdToNodeMap.get(destinationId).setColor(Color.ORANGE);
        nodeIdToNodeMap.get(adjacentId).setColor(Color.PINK);
    }

    /**
     * Reset the colors and highlight for nodes in the graph.
     */
    public void resetHighlightAndMISColors() {
        // set MIS color
        Runtime.nodes.forEach(node -> {
            // reset color
            ((UDGNode) node).getMISAlgorithmState().setChangedMISState(true);
            ((UDGNode) node).setMisColor();
            // highlight
            node.highlight(false);
        });
    }
}
