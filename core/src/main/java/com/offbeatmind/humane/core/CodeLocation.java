package com.offbeatmind.humane.core;

/**
 * Specifies a location of code inside the source file (line and column number).
 * 
 * @see CodeRange
 * 
 * @author humanejava
 *
 */
public class CodeLocation implements Comparable<CodeLocation> {
    private final int lineNumber;
    private final int columnNumber;
    
    public CodeLocation(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public int compareTo(CodeLocation o) {
        int c = Integer.compare(lineNumber, o.lineNumber);
        if (c != 0) return c;
        return Integer.compare(columnNumber, o.columnNumber);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + columnNumber;
        result = prime * result + lineNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CodeLocation other = (CodeLocation) obj;
        if (columnNumber != other.columnNumber) return false;
        if (lineNumber != other.lineNumber) return false;
        return true;
    }
    
    public String toString() {
        return lineNumber + ":" + columnNumber;
    }
}
