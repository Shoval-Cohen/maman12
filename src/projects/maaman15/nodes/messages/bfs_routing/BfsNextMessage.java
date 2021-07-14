package projects.maaman15.nodes.messages.bfs_routing;

import sinalgo.nodes.messages.Message;

/**
 * A node will send this message to his "sons" for creating a BFS tree
 */
public class BfsNextMessage extends Message {
    private final int rootId; // bfs initializer node id (the root)
    private final int parentId; // The sender id (the parent node)

    public BfsNextMessage(int rootId, int parentId) {
        this.rootId = rootId;
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    public int getRootId() {
        return rootId;
    }

    @Override
    public Message clone() {
        return new BfsNextMessage(rootId, parentId);
    }
}
