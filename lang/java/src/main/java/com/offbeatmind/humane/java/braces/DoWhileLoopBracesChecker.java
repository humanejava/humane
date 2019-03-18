package com.offbeatmind.humane.java.braces;

import java.util.function.Consumer;

import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;

public class DoWhileLoopBracesChecker extends LoopBracesChecker {

    public DoWhileLoopBracesChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(final boolean fixErrors) {
        javaFile.getCompilationUnit().walk(DoStmt.class, new Consumer<DoStmt>() {

            @Override
            public void accept(DoStmt doStatement) {
                final Statement body = doStatement.getBody();
                if (body.isBlockStmt() || body.isEmptyStmt()) return;

                final int doStartLine = doStatement.getCondition().getBegin().get().line;
                final int conditionStartLine = doStatement.getCondition().getBegin().get().line;

                if (doStartLine != conditionStartLine) {
                    addViolation(new ForebiddenMultilineNonBlockStatement(NodeSourceElement.of(body)));
                }

                checkAllMultiline(doStatement, body);
            }
        });
    }
}
