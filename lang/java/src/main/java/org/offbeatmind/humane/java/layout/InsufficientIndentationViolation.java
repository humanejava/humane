package org.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;

public class InsufficientIndentationViolation extends IndentationViolation {
    
    private final int minIndentation;
    
    public InsufficientIndentationViolation(
        SourceElement violatingElement,
        int actualIndentation,
        int minIndentation
    ) {
        super(violatingElement, actualIndentation);
        this.minIndentation = minIndentation;
    }

    @Override
    public String getMessage() {
        return "Indentation of line #" + getLineNumber() + " (" + getActualIndentation() + 
                ") is less than the required minimum  " + minIndentation + 
                ".";
    }
}