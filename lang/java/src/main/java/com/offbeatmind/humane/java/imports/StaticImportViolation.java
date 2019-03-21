package com.offbeatmind.humane.java.imports;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class StaticImportViolation extends JavaViolation {

    protected StaticImportViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Static imports are not allowed.";
    }
}
