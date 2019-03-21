package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;

/**
 * Base class for source files.
 * Intended to be subclasses for each language.
 * 
 * @author humanejava
 *
 */
public abstract class SourceFile {
	/**
	 * Actual source file.
	 */
    private final File sourceFile;
    
    /**
     * Text of the source code.
     */
    private final String sourceText;
    
    /**
     * End-of-line style used in the file.
     */
    private final EndOfLineStyle eol;
    
    /**
     * Individual lines of the code, 0-based.
     */
    private final String[] lines;
    
    /**
     * Violations detected.
     */
    private final TreeMap<CodeLocation, Violation> violations = new TreeMap<>();

    /**
     * Sole constructor.
     * 
     * @param sourceFile
     *     File to read/use as source file.
     *     
     * @throws IOException
     */
    public SourceFile(File sourceFile) throws IOException {
        this.sourceFile = sourceFile;

        sourceText = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);

        eol = EndOfLineStyle.recognize(sourceText);

        if (eol == null) {
            // All in one line.
            lines = new String[] { sourceText };
        } else {
            lines = sourceText.split(eol.getSequenceRegEx());
        }
    }

    /**
     * Returns the file system file.
     */
    public File getSourceFile() {
        return sourceFile;
    }

    /**
     * Returns the entire source code text.
     */
    public String getSourceText() {
        return sourceText;
    }

    /**
     * Returns the end of line style used in the file.
     */
    public EndOfLineStyle getEndOfLineStyle() {
        return eol;
    }

    /**
     * Returns the requested line (1-based).
     * 
     * @param lineNumber
     *     Number of the line to return. First line is 1.
     */
    public String getLine(int lineNumber) {
        return lines[lineNumber - 1];
    }

    /**
     * Returns the number of lines in the file.
     */
    public int getLineCount() {
        return lines.length;
    }

    /**
     * Adds a violation for this file.
     */
    public void addViolation(Violation violation) {
        violations.put(violation.getLocation(), violation);
    }
    
    /**
     * Returns {@code true} if there are violations (remaining after any fixes).
     */
	public final boolean hasViolations() {
		return !violations.isEmpty();
	}

    /**
     * Prints all violations added via {@link #addViolation(Violation)}
     */
    public void printViolations() {
        for (Violation v : violations.values()) {
            System.err.println(v.getLocation() + ": " + v.getMessage());
        }
    }
}
