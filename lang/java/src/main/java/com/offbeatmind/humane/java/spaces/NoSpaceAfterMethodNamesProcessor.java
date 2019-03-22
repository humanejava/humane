package com.offbeatmind.humane.java.spaces;

import java.util.function.Consumer;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeElement;

/**
 * Vaidates that there are no spaces between method names and their parameters.
 * 
 * @author humanejava
 *
 */
public class NoSpaceAfterMethodNamesProcessor extends JavaFileProcessor {

    public NoSpaceAfterMethodNamesProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        // No spaces
        checkMethodCalls();
        checkObjectCreationExpressions();
        checkExplicitConstructorCalls();
        checkConstructorDeclarations();
        checkMethodDeclarations();
        checkArrayCreationExpressions();
        checkArrayAccessExpressions();
    }

    private void checkMethodCalls() {
        javaFile.walkElements(MethodCallExpr.class, new Consumer<NodeElement<MethodCallExpr>>() {
            @Override
            public void accept(NodeElement<MethodCallExpr> element) {
                checkMethodCall(element);
            }
        });
    }

    private void checkMethodCall(NodeElement<MethodCallExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkObjectCreationExpressions() {
        javaFile.walkElements(ObjectCreationExpr.class, new Consumer<NodeElement<ObjectCreationExpr>>() {
            @Override
            public void accept(NodeElement<ObjectCreationExpr> element) {
                checkObjectCreationExpression(element);
            }
        });
    }

    private void checkObjectCreationExpression(NodeElement<ObjectCreationExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkExplicitConstructorCalls() {
        javaFile
            .walkElements(
                ExplicitConstructorInvocationStmt.class,
                new Consumer<NodeElement<ExplicitConstructorInvocationStmt>>() {
                    @Override
                    public void accept(NodeElement<ExplicitConstructorInvocationStmt> element) {
                        checkExplicitConstructorCall(element);
                    }
                }
            );
    }

    private void checkExplicitConstructorCall(NodeElement<ExplicitConstructorInvocationStmt> element) {
        // TODO Auto-generated method stub

    }

    private void checkConstructorDeclarations() {
        javaFile.walkElements(ConstructorDeclaration.class, new Consumer<NodeElement<ConstructorDeclaration>>() {
            @Override
            public void accept(NodeElement<ConstructorDeclaration> element) {
                checkConstructorDeclaration(element);
            }
        });
    }

    private void checkConstructorDeclaration(NodeElement<ConstructorDeclaration> element) {
        // TODO Auto-generated method stub

    }

    private void checkMethodDeclarations() {
        javaFile.walkElements(MethodDeclaration.class, new Consumer<NodeElement<MethodDeclaration>>() {
            @Override
            public void accept(NodeElement<MethodDeclaration> element) {
                checkMethodDeclaration(element);
            }
        });
    }

    private void checkMethodDeclaration(NodeElement<MethodDeclaration> element) {
        // TODO Auto-generated method stub

    }

    private void checkArrayCreationExpressions() {
        javaFile.walkElements(ArrayCreationExpr.class, new Consumer<NodeElement<ArrayCreationExpr>>() {
            @Override
            public void accept(NodeElement<ArrayCreationExpr> element) {
                checkArrayCreationExpression(element);
            }
        });
    }

    private void checkArrayCreationExpression(NodeElement<ArrayCreationExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkArrayAccessExpressions() {
        javaFile.walkElements(ArrayAccessExpr.class, new Consumer<NodeElement<ArrayAccessExpr>>() {
            @Override
            public void accept(NodeElement<ArrayAccessExpr> element) {
                checkArrayAccessExpression(element);
            }
        });
    }

    private void checkArrayAccessExpression(NodeElement<ArrayAccessExpr> element) {
        // TODO Auto-generated method stub

    }
}
