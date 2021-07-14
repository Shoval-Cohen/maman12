package projects.maaman15.nodes.algorithms_state.mis;

import java.awt.*;

/**
 * MIS algorithm node state (with a matching color)
 */
public enum ENodeMisState {
    NON_FINAL(Color.GRAY), // undecided
    IN_MIS(Color.GREEN), // inside the maximal independent set
    OUT_MIS(Color.RED), // outside the maximal independent set
    ;

    private final Color color;

    ENodeMisState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
