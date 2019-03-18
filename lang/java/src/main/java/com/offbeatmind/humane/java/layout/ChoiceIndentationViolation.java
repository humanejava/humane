package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;

public class ChoiceIndentationViolation extends IndentationViolation {

    private final int standardIndentation;
    private final int alternateIndentation;

    public ChoiceIndentationViolation(
        SourceElement violatingElement,
        int actualIndentation,
        int standardIndentation,
        int alternateIndentation
    ) {
        super(violatingElement, actualIndentation);
        this.standardIndentation = standardIndentation;
        this.alternateIndentation = alternateIndentation;
    }

    @Override
    public String getMessage() {
        if (standardIndentation == alternateIndentation) {
            return "Indentation of line #"
                + getLineNumber() + " (" + getActualIndentation() +
                ") does not match expected  " + standardIndentation +
                ".";
        } else {
            return "Indentation of line #"
                + getLineNumber() + " (" + getActualIndentation() +
                ") does not match expected  " + standardIndentation +
                " or " + alternateIndentation +
                ".";
        }
    }
}
