package com.offbeatmind.humane.java.braces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeElement;

public class ForebiddenMultilineNonBlockStatement extends JavaViolation {

    protected ForebiddenMultilineNonBlockStatement(NodeElement<?> violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Statement must be enclosed in {...} due to its multiline context: " + getRange();
    }
}
