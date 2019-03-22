package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeElement;

public class VerticalSeparationViolation extends JavaViolation {

    private final NodeElement<?> previousNode;

    protected VerticalSeparationViolation(NodeElement<?> violatingNode, NodeElement<?> previousNode) {
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
