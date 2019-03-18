package org.offbeatmind.humane.java.multidecl;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class MultipleDeclarationsViolation extends Violation {

    protected MultipleDeclarationsViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Multiple field/variable declarations are not allowed.";
    }

}
