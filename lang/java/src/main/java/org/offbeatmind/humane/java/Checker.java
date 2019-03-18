package org.offbeatmind.humane.java;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.SourceFile;
import com.offbeatmind.humane.core.Violation;

public abstract class Checker {
    protected final JavaFile javaFile;

    public Checker(JavaFile javaFile) {
        this.javaFile = javaFile;
    }

    public SourceFile getJavaFile() {
        return javaFile;
    }

    public abstract void check();

    protected void addViolation(Violation violation) {
        javaFile.addViolation(violation);
    }

}
