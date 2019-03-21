package com.offbeatmind.humane.java.empty;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeSourceElement;
import com.offbeatmind.humane.java.SourceElement;

/**
 * Validates that there are no completely empty blocks
 * (method bodies, classes, interfaces and enums) without even a comment.
 * 
 * @author humanejava
 *
 */
public class EmptyStatementsProcessor extends JavaFileProcessor {

    private static final boolean ALLOW_EMPTY_METHOD_BODIES = true;
    private static final boolean ALLOW_EMPTY_CLASSIFIERS = true;
    private static final boolean ALLOW_EMPTY_ENUMS = true;

    public EmptyStatementsProcessor(JavaFile javaFile) {
        super(javaFile);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void process(boolean fixErrors) {
        javaFile.getCompilationUnit().walk(Statement.class, new Consumer<Statement>() {
            @Override
            public void accept(Statement statement) {
                if (statement.isBlockStmt()) {
                    checkEmpty(statement);
                }
            }
        });

        javaFile.getCompilationUnit().walk(CatchClause.class, new Consumer<CatchClause>() {
            @Override
            public void accept(CatchClause clause) {
                checkEmpty(clause);
            }
        });

        javaFile.getCompilationUnit().walk(IfStmt.class, new Consumer<IfStmt>() {
            @Override
            public void accept(IfStmt ifStatement) {
                checkEmpty(ifStatement.getThenStmt());
                Statement elseStatement = ifStatement.getElseStmt().orElse(null);
                if (elseStatement != null) checkEmpty(elseStatement);
            }
        });

        javaFile
            .getCompilationUnit()
            .walk(ClassOrInterfaceDeclaration.class, new Consumer<ClassOrInterfaceDeclaration>() {
                @Override
                public void accept(ClassOrInterfaceDeclaration declaration) {
                    checkEmpty(declaration);
                }
            });

        javaFile.getCompilationUnit().walk(EnumDeclaration.class, new Consumer<EnumDeclaration>() {
            @Override
            public void accept(EnumDeclaration declaration) {
                checkEmpty(declaration);
            }
        });
    }

    private <N extends Node> void checkEmpty(N node) {
        final NodeSourceElement<N> element = NodeSourceElement.of(node);
        List<SourceElement> children = new LinkedList<>(element.getElements());
        SourceElement first = children.get(0);
        
        if (first.isToken() && first.asTokenElement().getText().contentEquals("{")) {
            children.remove(0);
            children.remove(children.size() - 1);
        }
        for (SourceElement child : children) {
            if (!child.isWhitespace()) return;
        }

        Node parent = node.getParentNode().get();
        
        if (parent instanceof MethodDeclaration) {
            if (ALLOW_EMPTY_METHOD_BODIES) return;
        } else if (parent instanceof ClassOrInterfaceDeclaration) {
            if (ALLOW_EMPTY_CLASSIFIERS) return;
        } else if (parent instanceof EnumDeclaration) {
            if (ALLOW_EMPTY_ENUMS) return;
        }

        addViolation(new CompletelyEmptyViolation(element));
    }
}
