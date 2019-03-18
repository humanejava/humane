package com.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class WhitespaceMustExistOnExactlyOneSideViolation extends Violation {

    public WhitespaceMustExistOnExactlyOneSideViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Whitespace must exist on exactly one side of this element.";
    }

}
