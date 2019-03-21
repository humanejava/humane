package com.offbeatmind.humane.java.empty;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class CompletelyEmptyViolation extends JavaViolation {

    protected CompletelyEmptyViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Completely empty blocks are not permitted - add a comment at least";

    }

}
