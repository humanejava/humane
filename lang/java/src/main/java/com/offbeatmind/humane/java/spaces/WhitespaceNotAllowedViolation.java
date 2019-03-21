package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class WhitespaceNotAllowedViolation extends JavaViolation {

    private final SourceElement elementAfter;

    protected WhitespaceNotAllowedViolation(SourceElement elementBefore, SourceElement elementAfter) {
        super(elementBefore);
        this.elementAfter = elementAfter;
    }

    @Override
    public String getMessage() {
        return "Forebidden whitespace before " + elementAfter.getRange().get().begin;
    }

}
