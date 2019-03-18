package com.offbeatmind.humane.java.multidecl;

import java.util.HashSet;
import java.util.function.Consumer;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.java.JavaFileProcessor;

public class MultipleDeclarationsChecker extends JavaFileProcessor {

    private final HashSet<Position> observedPositions;

    public MultipleDeclarationsChecker(JavaFile javaFile) {
        super(javaFile);
        observedPositions = new HashSet<>();
    }

    @Override
    public void process(boolean fixErrors) {
        javaFile.getCompilationUnit().walk(VariableDeclarator.class, new Consumer<VariableDeclarator>() {
            @Override
            public void accept(VariableDeclarator decl) {
                checkMultiple(decl);
            }
        });
    }

    private void checkMultiple(Node node) {
        final Node parent = node.getParentNode().orElse(null);
        // Parent may be either FieldDeclaration or VariableDeclarationExpr

        final Position position = parent.getBegin().orElse(null);

        if (position != null) {
            if (!observedPositions.add(position)) {
                addViolation(new MultipleDeclarationsViolation(NodeSourceElement.of(node)));
            }
        }
    }
}
