package com.offbeatmind.humane.core;

/**
 * A base class for source code violations.
 * 
 * @author humanejava
 *
 */
public abstract class Violation {
    
    public Violation() {
        super();
    }
    
    /**
     * Returns the location of the beginning of the violation.
     * 
     * @see #getLineNumber()
     * @see #getColumnNumber()
     */
    public CodeLocation getViolationLocation() {
        return getViolatingCodeRange().getStart();
    }

    /**
     * Returns the location of the beginning of the violation.
     * 
     * @see #getLineNumber()
     * @see #getColumnNumber()
     */
    public abstract CodeRange getViolatingCodeRange();
    
    /**
     * Returns the number of the line where the violation begins.
     * 
     * @see #getColumnNumber()
     * @see #getViolationLocation()
     */
    public int getLineNumber() {
    	return getViolationLocation().getLineNumber();
    }

    /**
     * Returns the number of the column where the violation begins.
     * 
     * @see #getLineNumber()
     * @see #getViolationLocation()
     */
    public int getColumnNumber() {
    	return getViolationLocation().getColumnNumber();
    }

    /**
     * Returns the descriptive message of the violation.
     */
    public abstract String getMessage();

}
