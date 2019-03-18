package org.offbeatmind.humane.java.spaces;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class InappropriateIdentifierSpacingViolation extends Violation {

    public InappropriateIdentifierSpacingViolation(SourceElement violatingElement) {
        super(violatingElement);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getMessage() {
        return "Identifier has spaces where they are not permitted";
    }

}
