package com.offbeatmind.humane.java.multidecl;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class MultipleDeclarationsViolation extends JavaViolation {

    protected MultipleDeclarationsViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Multiple field/variable declarations are not allowed.";
    }

}
