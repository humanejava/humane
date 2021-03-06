package com.offbeatmind.humane.java.layout;

import com.github.javaparser.ast.Node;
import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeElement;
import com.offbeatmind.humane.java.SourceElement;

public class SingleItemPerLineViolation extends JavaViolation {

    private final NodeElement<Node> preexistingNode;

    public SingleItemPerLineViolation(
        SourceElement violatingElement,
        NodeElement<Node> preexistingNode
    ) {
        super(violatingElement);
        this.preexistingNode = preexistingNode;
    }

    @Override
    public String getMessage() {
        return "There should be no more than one item per line and another is already there at "
            +
            preexistingNode.getRange().get();
    }

}
