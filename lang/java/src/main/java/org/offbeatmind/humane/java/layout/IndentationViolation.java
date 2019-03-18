package org.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public abstract class IndentationViolation extends Violation {
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
