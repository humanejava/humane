package com.offbeatmind.humane.java.spaces;

import java.util.function.Consumer;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.java.JavaFileProcessor;

public class NoSpaceAfterMethodNamesChecker extends JavaFileProcessor {

    public NoSpaceAfterMethodNamesChecker(JavaFile javaFile) {
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
        javaFile.walkElements(MethodCallExpr.class, new Consumer<NodeSourceElement<MethodCallExpr>>() {
            @Override
            public void accept(NodeSourceElement<MethodCallExpr> element) {
                checkMethodCall(element);
            }
        });
    }

    private void checkMethodCall(NodeSourceElement<MethodCallExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkObjectCreationExpressions() {
        javaFile.walkElements(ObjectCreationExpr.class, new Consumer<NodeSourceElement<ObjectCreationExpr>>() {
            @Override
            public void accept(NodeSourceElement<ObjectCreationExpr> element) {
                checkObjectCreationExpression(element);
            }
        });
    }

    private void checkObjectCreationExpression(NodeSourceElement<ObjectCreationExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkExplicitConstructorCalls() {
        javaFile
            .walkElements(
                ExplicitConstructorInvocationStmt.class,
                new Consumer<NodeSourceElement<ExplicitConstructorInvocationStmt>>() {
                    @Override
                    public void accept(NodeSourceElement<ExplicitConstructorInvocationStmt> element) {
                        checkExplicitConstructorCall(element);
                    }
                }
            );
    }

    private void checkExplicitConstructorCall(NodeSourceElement<ExplicitConstructorInvocationStmt> element) {
        // TODO Auto-generated method stub

    }

    private void checkConstructorDeclarations() {
        javaFile.walkElements(ConstructorDeclaration.class, new Consumer<NodeSourceElement<ConstructorDeclaration>>() {
            @Override
            public void accept(NodeSourceElement<ConstructorDeclaration> element) {
                checkConstructorDeclaration(element);
            }
        });
    }

    private void checkConstructorDeclaration(NodeSourceElement<ConstructorDeclaration> element) {
        // TODO Auto-generated method stub

    }

    private void checkMethodDeclarations() {
        javaFile.walkElements(MethodDeclaration.class, new Consumer<NodeSourceElement<MethodDeclaration>>() {
            @Override
            public void accept(NodeSourceElement<MethodDeclaration> element) {
                checkMethodDeclaration(element);
            }
        });
    }

    private void checkMethodDeclaration(NodeSourceElement<MethodDeclaration> element) {
        // TODO Auto-generated method stub

    }

    private void checkArrayCreationExpressions() {
        javaFile.walkElements(ArrayCreationExpr.class, new Consumer<NodeSourceElement<ArrayCreationExpr>>() {
            @Override
            public void accept(NodeSourceElement<ArrayCreationExpr> element) {
                checkArrayCreationExpression(element);
            }
        });
    }

    private void checkArrayCreationExpression(NodeSourceElement<ArrayCreationExpr> element) {
        // TODO Auto-generated method stub

    }

    private void checkArrayAccessExpressions() {
        javaFile.walkElements(ArrayAccessExpr.class, new Consumer<NodeSourceElement<ArrayAccessExpr>>() {
            @Override
            public void accept(NodeSourceElement<ArrayAccessExpr> element) {
                checkArrayAccessExpression(element);
            }
        });
    }

    private void checkArrayAccessExpression(NodeSourceElement<ArrayAccessExpr> element) {
        // TODO Auto-generated method stub

    }
}
