package com.offbeatmind.humane.java.layout;

import com.github.javaparser.ast.Node;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class SingleItemPerLineViolation extends Violation {

    private final NodeSourceElement<Node> preexistingNode;

    public SingleItemPerLineViolation(
        SourceElement violatingElement,
        NodeSourceElement<Node> preexistingNode
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
