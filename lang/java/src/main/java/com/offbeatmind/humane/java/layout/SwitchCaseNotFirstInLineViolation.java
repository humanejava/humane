package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class SwitchCaseNotFirstInLineViolation extends JavaViolation {

    protected SwitchCaseNotFirstInLineViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "The 'case' token of a switch statement must be the first non-blank in line " + getLineNumber();
    }

}
