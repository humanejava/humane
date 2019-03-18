package com.offbeatmind.humane.java.braces;

import java.util.function.Consumer;

import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;

public class WhileLoopBracesChecker extends LoopBracesChecker {

    public WhileLoopBracesChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(final boolean fixErrors) {
        javaFile.getCompilationUnit().walk(WhileStmt.class, new Consumer<WhileStmt>() {

            @Override
            public void accept(WhileStmt whileStatement) {
                final Statement body = whileStatement.getBody();
                if (body.isBlockStmt() || body.isEmptyStmt()) return;

                final int conditionEndLine = whileStatement.getCondition().getEnd().get().line;
                final int bodyEndLine = body.getEnd().get().line;

                if (conditionEndLine != bodyEndLine) {
                    addViolation(new ForebiddenMultilineNonBlockStatement(NodeSourceElement.of(body)));
                }

                checkAllMultiline(whileStatement, body);
            }
        });
    }
}
