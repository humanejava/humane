package com.offbeatmind.humane.java.braces;

import java.util.function.Consumer;

import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.NodeElement;
import com.offbeatmind.humane.java.SourceElement;
import com.offbeatmind.humane.java.TokenElement;

/**
 * Vaidates that multiline for-each ({@code for (...: ...)}) statements use braces.
 * 
 * @author humanejava
 *
 */
public class ForEachLoopBracesProcessor extends LoopBracesProcessor {

    public ForEachLoopBracesProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(final boolean fixErrors) {
        javaFile.getCompilationUnit().walk(ForEachStmt.class, new Consumer<ForEachStmt>() {

            @Override
            public void accept(ForEachStmt forStatement) {
                final Statement body = forStatement.getBody();
                if (body.isBlockStmt() || body.isEmptyStmt()) return;

                NodeElement<ForEachStmt> forElement = NodeElement.of(forStatement);
                TokenElement<?> loopControlEnd = null;

                for (SourceElement e : forElement.getElements()) {
                    if (e.isToken()) {
                        final TokenElement<?> token = (TokenElement<?>) e;

                        if (")".contentEquals(token.getText())) {
                            loopControlEnd = token;
                            break;
                        }
                    }
                }

                if (loopControlEnd == null) {
                    throw new RuntimeException("Didn't find the end of the for loop control block.");
                }

                final int controlEndLine = loopControlEnd.getRange().get().end.line;
                final int bodyEndLine = body.getEnd().get().line;

                if (controlEndLine != bodyEndLine) {
                    addViolation(new ForebiddenMultilineNonBlockStatement(NodeElement.of(body)));
                }

                checkAllMultiline(forStatement, body);
            }
        });
    }
}
