package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;

public class SourceFile {
    private final File sourceFile;
    private final String source;
    private final EndOfLineStyle eol;
    private final String[] lines;
    private final TreeMap<CodeLocation, Violation> violations = new TreeMap<>();

    public SourceFile(File sourceFile) throws IOException {
        this.sourceFile = sourceFile;

        source = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);

        eol = EndOfLineStyle.recognize(source);

        if (eol == null) {
            // All in one line.
            lines = new String[] { source };
        } else {
            lines = source.split(eol.getSequenceRegEx());
        }
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getSource() {
        return source;
    }

    public EndOfLineStyle getEndOfLineStyle() {
        return eol;
    }

    public String getLine(int lineNumber) {
        return lines[lineNumber - 1];
    }

    public int getLineCount() {
        return lines.length;
    }

    public void addViolation(Violation violation) {
        violations.put(violation.getLocation(), violation);
    }

    public void printViolations() {
        for (Violation v : violations.values()) {
            System.err.println(v.getLocation() + ": " + v.getMessage());
        }
    }

}
