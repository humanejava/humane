package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public abstract class IndentationViolation extends JavaViolation {
    private final int actualIndentation;

    public IndentationViolation(
        SourceElement violatingElement,
        int actualIndentation
    ) {
        super(violatingElement);
        this.actualIndentation = actualIndentation;
    }

    public int getActualIndentation() {
        return actualIndentation;
    }
}
