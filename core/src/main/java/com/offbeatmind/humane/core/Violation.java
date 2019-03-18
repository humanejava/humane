package com.offbeatmind.humane.core;

import com.github.javaparser.Position;
import com.github.javaparser.Range;

public abstract class Violation {
    private final SourceElement violatingElement;
    
    protected Violation(SourceElement violatingElement) {
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
    
    public abstract String getMessage();
}
