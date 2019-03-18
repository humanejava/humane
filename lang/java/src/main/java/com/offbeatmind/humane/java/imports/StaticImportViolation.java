package com.offbeatmind.humane.java.imports;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class StaticImportViolation extends Violation {

    protected StaticImportViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Static imports are not allowed.";
    }
}
