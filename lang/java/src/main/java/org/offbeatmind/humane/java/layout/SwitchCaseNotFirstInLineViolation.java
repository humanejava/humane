package org.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class SwitchCaseNotFirstInLineViolation extends Violation {

    protected SwitchCaseNotFirstInLineViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "The 'case' token of a switch statement must be the first non-blank in line " + getLineNumber();
    }

}
