package projects.maaman15.nodes.messages.bfs_routing;

import sinalgo.nodes.messages.Message;

/**
 * A node will send this message to his parent for marking that this node and all his "sons" are done.
 */
public class BfsBackMessage extends Message {
    private final int root; // bfs initializer node id (the root)

    public BfsBackMessage(int root) {
        this.root = root;
    }

    public int getRoot() {
        return root;
    }

    @Override
    public Message clone() {
        return new BfsBackMessage(root);
    }
}
