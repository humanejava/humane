package com.offbeatmind.humane.core;

/**
 * Specifies the place in the source file where the code is in terms of
 * the starting and ending {@linkplain CodeLocation locations}
 * (line and column numbers).
 * 
 * @author humanejava
 *
 */
public class CodeRange implements Comparable<CodeRange> {
    private final CodeLocation start;
    private final CodeLocation end;
    
    public CodeRange(CodeLocation start, CodeLocation end) {
        super();
        this.start = start;
        this.end = end;
    }

    public CodeLocation getStart() {
        return start;
    }

    public CodeLocation getEnd() {
        return end;
    }

    @Override
    public int compareTo(CodeRange o) {
        return getStart().compareTo(o.getStart());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CodeRange other = (CodeRange) obj;
        if (end == null) {
            if (other.end != null) return false;
        } else if (!end.equals(other.end)) return false;
        if (start == null) {
            if (other.start != null) return false;
        } else if (!start.equals(other.start)) return false;
        return true;
    }
    
    public String toString() {
        return "[" + start.toString() + "..." + end.toString() + "]";
    }
    
}
