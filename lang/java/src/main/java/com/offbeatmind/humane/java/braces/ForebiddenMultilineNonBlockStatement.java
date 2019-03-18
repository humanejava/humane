package com.offbeatmind.humane.java.braces;

import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.Violation;

public class ForebiddenMultilineNonBlockStatement extends Violation {

    protected ForebiddenMultilineNonBlockStatement(NodeSourceElement<?> violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Statement must be enclosed in {...} due to its multiline context: " + getRange();
    }
}
