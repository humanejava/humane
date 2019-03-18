package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class MissingWhitespaceViolation extends Violation {

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
