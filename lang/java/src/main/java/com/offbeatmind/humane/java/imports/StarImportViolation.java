package com.offbeatmind.humane.java.imports;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class StarImportViolation extends Violation {

    protected StarImportViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Star imports are not allowed.";
    }

}
