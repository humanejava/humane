package org.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class WhitespaceNotAllowedViolation extends Violation {

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
