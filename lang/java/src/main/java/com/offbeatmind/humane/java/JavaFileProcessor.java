package com.offbeatmind.humane.java;

import com.offbeatmind.humane.core.SourceFile;
import com.offbeatmind.humane.core.Violation;

/**
 * Base class for Java file processors that can find violations and, ideally,
 * fix them.
 * 
 * Processor instances are created separately for each file, to allow them
 * to easily have file-related state.
 * 
 * @author humanejava
 *
 */
public abstract class JavaFileProcessor {
	/**
	 * File being processed.
	 */
    protected final JavaFile javaFile;

    public JavaFileProcessor(JavaFile javaFile) {
        this.javaFile = javaFile;
    }

    /**
     * Returns the file being processed.
     */
    public SourceFile getJavaFile() {
        return javaFile;
    }

    /**
     * Invoked to process the file.
     * 
     * @param fixErrors Whether to attempt to fix discovered violations.
     */
    public abstract void process(boolean fixErrors);

    /**
     * Convenience method to add a violation to the file.
     */
    protected void addViolation(Violation violation) {
        javaFile.addViolation(violation);
    }

}
