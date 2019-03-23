package com.offbeatmind.humane.java.specialchars;

import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeElement;

/**
 * Processes special/invisible characters in text (char, String) literals - e.g. \t, \b and \f.
 * 
 * @author humanejava
 *
 */
public class SpecialCharsInTextLiteralsProcessor extends JavaFileProcessor {
    
    public SpecialCharsInTextLiteralsProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(final boolean fixErrors) {
        javaFile.walkNodes(StringLiteralExpr.class, this::processString);
    }

    protected final void processString(StringLiteralExpr stringLiteral) {
        String original = stringLiteral.getValue(); // TODO Check if this is as specified or parsed?
        if (original == null) return;
        
        String correct = original
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")  // Should not be possible but here for completeness
            .replace("\r", "\\r"); // Same as above
        
        for (char ch = '\u0000'; ch < ' '; ch++) {
            String unicode = Integer.toHexString(ch);
            while (unicode.length() < 4) unicode = "0" + unicode;
            correct = correct.replace(String.valueOf(ch), "\\u" + unicode);
        }
        
        if (!original.contentEquals(correct)) {
            addViolation(new SpecialCharsInTextLiteralViolation(NodeElement.of(stringLiteral), correct));
            //if (fixErrors) stringLiteral.setValue(correct);
        }
    }
}
