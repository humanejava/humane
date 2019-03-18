package com.offbeatmind.humane.java.braces;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.java.Checker;

public abstract class LoopBracesChecker extends Checker {

    protected static final Boolean REQUIRE_BRACES_FOR_MULTILINE_LOOPS = false;

    public LoopBracesChecker(JavaFile javaFile) {
        super(javaFile);
    }

    protected void checkAllMultiline(Node entireLoop, final Statement loopBody) {
        if (REQUIRE_BRACES_FOR_MULTILINE_LOOPS) {
            final int statementStartLine = entireLoop.getEnd().get().line;

            if (statementStartLine != loopBody.getEnd().get().line) {
                addViolation(new ForebiddenMultilineNonBlockStatement(NodeSourceElement.of(loopBody)));
            }
        }
    }

}
