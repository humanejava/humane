package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;

import com.github.javaparser.Position;

public class SourceFile {

    private final SourceTree sourceTree;
    private final File sourceFile;
    private final String source;
    private final EndOfLineStyle eol;
    private final String[] lines;
    private final TreeMap<Position, Violation> violations = new TreeMap<>();

    public SourceFile(SourceTree sourceTree, File sourceFile) throws IOException {
        this.sourceTree = sourceTree;
        this.sourceFile = sourceFile;

        source = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);

        eol = EndOfLineStyle.recognize(source);

        if (eol == null) {
            // All in one line.
            lines = new String[] { source };
        } else {
            lines = source.split(eol.getSequencePattern());
        }
    }

    public SourceTree getSourceTree() {
        return sourceTree;
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
        violations.put(violation.getPosition(), violation);
    }

    public void printViolations() {
        for (Violation v : violations.values()) {
            SourceElement e = v.getViolatingElement();

            if (e.isNode()) {
                System.err.println(e.getName() + " @ " + v.getRange() + ": " + v.getMessage());
            } else {
                System.err.println(e.getName() + " @ " + v.getPosition() + ": " + v.getMessage());
            }

        }

    }

}
