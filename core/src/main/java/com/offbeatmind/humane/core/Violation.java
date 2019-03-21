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
    public abstract CodeLocation getLocation();

    /**
     * Returns the number of the line where the violation begins.
     * 
     * @see #getColumnNumber()
     * @see #getLocation()
     */
    public int getLineNumber() {
    	return getLocation().getLineNumber();
    }

    /**
     * Returns the number of the column where the violation begins.
     * 
     * @see #getLineNumber()
     * @see #getLocation()
     */
    public int getColumnNumber() {
    	return getLocation().getColumnNumber();
    }

    /**
     * Returns the descriptive message of the violation.
     */
    public abstract String getMessage();

}
