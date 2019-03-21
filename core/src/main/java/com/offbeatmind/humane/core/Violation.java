package com.offbeatmind.humane.core;

public abstract class Violation {
    
    public Violation() {
        super();
    }
    
    public abstract CodeLocation getLocation();

    public abstract int getLineNumber();

    public abstract int getColumnNumber();

    public abstract String getMessage();

}
