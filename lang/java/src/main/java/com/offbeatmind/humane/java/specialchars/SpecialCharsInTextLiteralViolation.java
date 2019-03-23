package com.offbeatmind.humane.java.specialchars;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class SpecialCharsInTextLiteralViolation extends JavaViolation {

    private final String correct;
    
    public SpecialCharsInTextLiteralViolation(SourceElement violatingElement, String correct) {
        super(violatingElement);
        this.correct = correct;
    }

    @Override
    public String getMessage() {
        return "Text literal contains special chars that should be escaped as follows: " + correct;
    }

}
