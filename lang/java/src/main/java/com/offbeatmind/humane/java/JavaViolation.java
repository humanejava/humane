package com.offbeatmind.humane.java;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.offbeatmind.humane.core.CodeLocation;
import com.offbeatmind.humane.core.Violation;

public abstract class JavaViolation extends Violation {
    private final SourceElement violatingElement;

    protected JavaViolation(SourceElement violatingElement) {
        this.violatingElement = violatingElement;
    }

    public final SourceElement getViolatingElement() {
        return violatingElement;
    }

    public final Range getRange() {
        return violatingElement.getRange().get();
    }

    public final Position getPosition() {
        return getRange().begin;
    }
    
    public final int getLineNumber() {
        return getPosition().line;
    }

    public final int getColumnNumber() {
        return getPosition().column;
    }

    @Override
    public CodeLocation getLocation() {
        return new CodeLocation(getLineNumber(), getColumnNumber());
    }
}
