package org.offbeatmind.humane.java.braces;

import java.util.function.Consumer;

import org.offbeatmind.humane.java.Checker;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;

public class IfElseBracesChecker extends Checker {

    public IfElseBracesChecker(JavaFile javaFile) {
        super(javaFile);
        // TODO Auto-generated constructor stub
    }
    
    private static boolean isMultiline(Node node) {
        return node.getEnd().get().line != node.getBegin().get().line;
    }
    

    @Override
    public void check() {
        javaFile.getCompilationUnit().walk(IfStmt.class, new Consumer<IfStmt>() {

            @Override
            public void accept(IfStmt ifStatement) {
                if (isMultiline(ifStatement)) {
                    Statement thenStatement = ifStatement.getThenStmt();
                    Statement elseStatement = ifStatement.getElseStmt().orElse(null);
                    
                    if (!(thenStatement.isBlockStmt() || thenStatement.isEmptyStmt())) {
                        addViolation(new ForebiddenMultilineNonBlockStatement(NodeSourceElement.of(thenStatement)));
                    }
                    
                    if (elseStatement != null) {
                        if (!(elseStatement.isBlockStmt() || elseStatement.isEmptyStmt() || (elseStatement instanceof IfStmt))) {
                            addViolation(new ForebiddenMultilineNonBlockStatement(NodeSourceElement.of(elseStatement)));
                        }
                    }
                }
            }
        });
    }
}
