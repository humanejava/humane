package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;

public class ExactIndentationViolation extends IndentationViolation {

    private final int requiredIndentation;

    public ExactIndentationViolation(
        SourceElement violatingElement,
        int actualIndentation,
        int requiredIndentation
    ) {
        super(violatingElement, actualIndentation);
        this.requiredIndentation = requiredIndentation;
    }

    @Override
    public String getMessage() {
        return "Indentation of line #"
            + getLineNumber() + " (" + getActualIndentation() +
            ") does not match required  " + requiredIndentation +
            ".";
    }
}
