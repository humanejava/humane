package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class MissingWhitespaceViolation extends JavaViolation {

    private final SourceElement elementAfter;

    protected MissingWhitespaceViolation(SourceElement elementBefore, SourceElement elementAfter) {
        super(elementBefore);
        this.elementAfter = elementAfter;
    }

    @Override
    public String getMessage() {
        return "Missing pure whitespace before " + elementAfter.getRange().get().begin;
    }

}
