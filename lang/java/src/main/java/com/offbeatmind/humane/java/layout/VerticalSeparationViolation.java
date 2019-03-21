package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeSourceElement;

public class VerticalSeparationViolation extends JavaViolation {

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
