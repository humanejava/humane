package com.offbeatmind.humane.java;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.SourceFile;
import com.offbeatmind.humane.core.Violation;

public abstract class JavaFileProcessor {
    protected final JavaFile javaFile;

    public JavaFileProcessor(JavaFile javaFile) {
        this.javaFile = javaFile;
    }

    public SourceFile getJavaFile() {
        return javaFile;
    }

    public abstract void process(boolean fixErrors);

    protected void addViolation(Violation violation) {
        javaFile.addViolation(violation);
    }

}
