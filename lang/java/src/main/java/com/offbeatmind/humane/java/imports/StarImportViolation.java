package com.offbeatmind.humane.java.imports;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class StarImportViolation extends JavaViolation {

    protected StarImportViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Star imports are not allowed.";
    }

}
