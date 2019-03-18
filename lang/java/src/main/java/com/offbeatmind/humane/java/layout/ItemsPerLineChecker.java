package com.offbeatmind.humane.java.layout;

import java.util.HashMap;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.java.JavaFileProcessor;

public class ItemsPerLineChecker extends JavaFileProcessor {

    public ItemsPerLineChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        checkMaxOnePerLine(javaFile.getCompilationUnit());
    }

    private void checkMaxOnePerLine(Node node) {

        final HashMap<Integer, Node> linesToNodes = new HashMap<>();
        final List<Node> children = node.getChildNodes();

        for (Node child : children) {
            boolean doCheck =
                (child instanceof ImportDeclaration)
                    ||
                    (child instanceof Statement) ||
                    (child instanceof BodyDeclaration) ||
                    (child instanceof CatchClause) ||
                    (child instanceof ModuleDeclaration);

            if (doCheck) {
                final Range range = child.getRange().get();

                for (int line = range.begin.line; line <= range.end.line; line++) {
                    final Node preexistingNode = linesToNodes.get(line);

                    if ((preexistingNode != null) && !preexistingNode.getRange().get().contains(range)) {

                        final Node parent = child.getParentNode().get();
                        boolean exception = false;

                        exception |=
                            (parent instanceof IfStmt)
                                &&
                                (((IfStmt) parent).getThenStmt() == preexistingNode) &&
                                ((IfStmt) parent).getElseStmt().orElse(null) == child;

                        exception |= (parent instanceof TryStmt) && (child instanceof CatchClause);

                        if (!exception) {
                            addViolation(
                                new SingleItemPerLineViolation(
                                    NodeSourceElement.of(child),
                                    NodeSourceElement.of(preexistingNode)
                                )
                            );
                        }
                    }

                    linesToNodes.put(line, child);
                }
            }
            checkMaxOnePerLine(child);
        }
    }
}
