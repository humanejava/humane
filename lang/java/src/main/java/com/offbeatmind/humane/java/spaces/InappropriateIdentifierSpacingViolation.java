package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class InappropriateIdentifierSpacingViolation extends JavaViolation {

    public InappropriateIdentifierSpacingViolation(SourceElement violatingElement) {
        super(violatingElement);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getMessage() {
        return "Identifier has spaces where they are not permitted";
    }

}
