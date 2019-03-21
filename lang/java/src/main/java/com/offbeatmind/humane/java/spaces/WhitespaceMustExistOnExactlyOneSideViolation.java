package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class WhitespaceMustExistOnExactlyOneSideViolation extends JavaViolation {

    public WhitespaceMustExistOnExactlyOneSideViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Whitespace must exist on exactly one side of this element.";
    }

}
