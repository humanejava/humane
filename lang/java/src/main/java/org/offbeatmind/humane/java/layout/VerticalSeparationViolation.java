package org.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.Violation;

public class VerticalSeparationViolation extends Violation {

    private final NodeSourceElement<?> previousNode;

    protected VerticalSeparationViolation(NodeSourceElement<?> violatingNode, NodeSourceElement<?> previousNode) {
        super(violatingNode);
        this.previousNode = previousNode;
    }

    @Override
    public String getMessage() {
        return "Insufficient vertical separation from "
            +
            previousNode.getNode().getClass().getSimpleName() +
            " at " + previousNode.getRange().get();
    }

}
