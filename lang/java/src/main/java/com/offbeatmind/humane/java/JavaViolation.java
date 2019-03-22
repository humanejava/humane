package com.offbeatmind.humane.java;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.offbeatmind.humane.core.CodeLocation;
import com.offbeatmind.humane.core.CodeRange;
import com.offbeatmind.humane.core.Violation;

/**
 * Base class for Java {@linkplain Violation violations}
 * 
 * @author humanejava
 *
 */
public abstract class JavaViolation extends Violation {
    /**
     * Source element that is in violation.
     */
    private final SourceElement violatingElement;

    protected JavaViolation(SourceElement violatingElement) {
        this.violatingElement = violatingElement;
    }

    /**
     * Returns the source element that is in violation of set standards.
     */
    public final SourceElement getViolatingElement() {
        return violatingElement;
    }

    /**
     * Returns the JavaParser-typed range of locations where the violation is. 
     */
    public final Range getRange() {
        return violatingElement.getRange().get();
    }

    /**
     * Returns the JavaParser-typed position of the beginning of the violation.
     * 
     * @see #getViolationLocation()
     */
    public final Position getPosition() {
        return getRange().begin;
    }
    
    /**
     * Returns the line number of the beginning of the violation.
     */
    public final int getLineNumber() {
        return getPosition().line;
    }

    /**
     * Returns the column number of the beginning of the violation.
     */
    public final int getColumnNumber() {
        return getPosition().column;
    }

    @Override
    public CodeLocation getViolationLocation() {
        return new CodeLocation(getLineNumber(), getColumnNumber());
    }
    
    /**
     * Returns the location of the beginning of the violation.
     * 
     * @see #getLineNumber()
     * @see #getColumnNumber()
     */
    public CodeRange getViolatingCodeRange() {
        Range range = violatingElement.getRange().get();
        
        return new CodeRange(
            new CodeLocation(range.begin.line, range.begin.column),
            new CodeLocation(range.end.line, range.end.column)
        );
    }
}
