package org.offbeatmind.humane.java;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.Violation;

public abstract class Checker {
    protected final JavaFile javaFile;
    
    public static <T extends Object> void foo() {
        int x =
        11 * 13;
    }

    public Checker(JavaFile javaFile) {
        this.javaFile = javaFile;
    }

    public JavaFile getJavaFile() {
        return javaFile;
    }

    public abstract void check();
    
    protected void addViolation(Violation violation) {
        javaFile.addViolation(violation);
    }

}