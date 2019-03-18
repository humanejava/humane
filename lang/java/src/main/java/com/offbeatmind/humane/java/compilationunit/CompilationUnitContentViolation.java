package com.offbeatmind.humane.java.compilationunit;

import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class CompilationUnitContentViolation extends Violation {

    protected CompilationUnitContentViolation(SourceElement violatingElement) {
        super(violatingElement);
    }

    @Override
    public String getMessage() {
        return "Declarations with name different than the file must be moved to their own files";
    }

}
