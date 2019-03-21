package com.offbeatmind.humane.java.compilationunit;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.SourceElement;

public class CompilationUnitContentViolation extends JavaViolation {

    protected CompilationUnitContentViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Declarations with name different than the file must be moved to their own files";
    }

}
