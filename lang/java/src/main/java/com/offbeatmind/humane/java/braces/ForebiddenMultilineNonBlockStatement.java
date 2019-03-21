package com.offbeatmind.humane.java.braces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeSourceElement;

public class ForebiddenMultilineNonBlockStatement extends JavaViolation {

    protected ForebiddenMultilineNonBlockStatement(NodeSourceElement<?> violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Statement must be enclosed in {...} due to its multiline context: " + getRange();
    }
}
