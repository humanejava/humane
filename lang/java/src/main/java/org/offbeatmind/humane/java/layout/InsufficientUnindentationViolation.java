package org.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;

public class InsufficientUnindentationViolation extends IndentationViolation {

    private final int maxIndentation;

    public InsufficientUnindentationViolation(
        SourceElement violatingElement,
        int actualIndentation,
        int maxIndentation
    ) {
        super(violatingElement, actualIndentation);
        this.maxIndentation = maxIndentation;
    }

    @Override
    public String getMessage() {
        return "Line #"
            + getLineNumber() +
            " is not unindented enough and its indentation (" + getActualIndentation() + ")" +
            " is more than the maximum allowed (" + maxIndentation + ")" +
            ".";
    }

}
