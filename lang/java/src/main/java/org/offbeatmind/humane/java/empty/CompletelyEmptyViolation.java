package org.offbeatmind.humane.java.empty;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class CompletelyEmptyViolation extends Violation {

    protected CompletelyEmptyViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Completely empty blocks are not permitted - add a comment at least";
        
    }

}
