/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.Maman15;


import projects.Maman15.nodes.nodeImplementations.UDGNode;
import projects.defaultProject.models.connectivityModels.UDG;
import projects.defaultProject.models.distributionModels.Random;
import projects.defaultProject.models.interferenceModels.NoInterference;
import projects.defaultProject.models.mobilityModels.NoMobility;
import projects.defaultProject.models.reliabilityModels.ReliableDelivery;
import sinalgo.configuration.CorruptConfigurationEntryException;
import sinalgo.models.ConnectivityModelHelper;
import sinalgo.models.InterferenceModel;
import sinalgo.models.MobilityModel;
import sinalgo.models.ReliabilityModel;
import sinalgo.nodes.Node;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.runtime.Runtime;
import sinalgo.tools.Tools;

import javax.swing.*;
import java.util.stream.StreamSupport;

/**
 * This class holds customized global state and methods for the framework.
 * The only mandatory method to overwrite is
 * <code>hasTerminated</code>
 * <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 *
 * @see AbstractCustomGlobal for more details.
 * <br>
 * In addition, this class also provides the possibility to extend the framework with
 * custom methods that can be called either through the menu or via a button that is
 * added to the GUI.
 */
public class CustomGlobal extends AbstractCustomGlobal {

    /* (non-Javadoc)
     * @see runtime.AbstractCustomGlobal#hasTerminated()
     */
    public boolean hasTerminated() {
        return false;
    }

    /**
     * An example of a method that will be available through the menu of the GUI.
     */
    @GlobalMethod(menuText = "Echo")
    public void echo() {
        // Query the user for an input
        String answer = JOptionPane.showInputDialog(null, "This is an example.\nType in any text to echo.");
        // Show an information message
        JOptionPane.showMessageDialog(null, "You typed '" + answer + "'", "Example Echo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Used to create the UDG graph with given nodes.
     */
    @CustomButton(buttonText = "Create UDG", toolTipText = "Create UDG graph with given num of nodes")
    public void createUDG() throws CorruptConfigurationEntryException {
        int nodesNum = Integer.parseInt(Tools.showQueryDialog("Number of nodes"));
        buildUDG(nodesNum);
        runMIS();
    }

    public void runMIS() {
        int MISRounds = Integer.parseInt(Tools.showQueryDialog("MIS Rounds"));
        Runtime.nodes.forEach(node -> {
            ((UDGNode) node).setMisRounds(MISRounds);
            node.init();
        });
    }

    /**
     * Return the max degree
     */
    @CustomButton(buttonText = "Max degree", toolTipText = "Return the max degree")
    public void maxDegree() {
        Tools.getTextOutputPrintStream().println("Max degree = " + getMaxDegree());
    }

    private int getMaxDegree() {
        return StreamSupport.stream(Tools.getNodeList().spliterator(), false)
                .mapToInt(node -> node.outgoingConnections.size())
                .max()
                .orElse(0);
    }

    /**
     * Return the max routing table
     */
    @CustomButton(buttonText = "Routing table", toolTipText = "Return the max routing table")
    public void maxRoutingTable() {
        int maxRoutingTable = StreamSupport.stream(Tools.getNodeList().spliterator(), false)
                .filter(node -> node instanceof UDGNode)
                .mapToInt(node -> ((UDGNode) node).getRoutingTable().size())
                .max()
                .orElse(0);

        Tools.getTextOutputPrintStream().println("Max routing table = " + maxRoutingTable);
    }

    /**
     * Return the max routing table
     */
    @CustomButton(buttonText = "Statistic", toolTipText = "Full statistic")
    public void stats() {
        Tools.getTextOutputPrintStream().println("Num of nodes = " + Runtime.nodes.size());
        maxDegree();
        maxRoutingTable();
    }


    private void buildUDG(int nodesNum) throws CorruptConfigurationEntryException {
        if (nodesNum <= 0) {
            Tools.showMessageDialog("The number of nodes needs to be at least 1.\nCreation of UDG aborted.");
            return;
        }
        Tools.getTextOutputPrintStream().println("~~~~~~~~~~~~~~~~~~~");
        Tools.getTextOutputPrintStream().println("Creating UDG graph with " + nodesNum + " nodes.");

        // remove all nodes (if any)
        Runtime.clearAllNodes();

        Random random = new Random();
        ConnectivityModelHelper udg = new UDG();
        MobilityModel noMobility = new NoMobility();
        ReliabilityModel reliableDelivery = new ReliableDelivery();
        InterferenceModel noInterference = new NoInterference();

        // create nodes
        for (int i = 0; i < nodesNum; i++) {
            Node node = new UDGNode();
            // sets random position
            node.setPosition(random.getNextPosition());
            // set connectivity model to be static UDG
            node.setConnectivityModel(udg);

            node.setInterferenceModel(noInterference);
            node.setMobilityModel(noMobility);
            node.setReliabilityModel(reliableDelivery);

            Runtime.addNode(node);
        }


        // Showing the edges on beginning
        Tools.reevaluateConnections();

        // Repaint the GUI as we have added some nodes
        Tools.repaintGUI();
    }
}
