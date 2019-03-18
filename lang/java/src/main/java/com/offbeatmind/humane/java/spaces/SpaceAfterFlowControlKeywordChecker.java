package com.offbeatmind.humane.java.spaces;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.java.Checker;

public class SpaceAfterFlowControlKeywordChecker extends Checker {

    public SpaceAfterFlowControlKeywordChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void check() {
        checkIfSpacing();
        checkWhileLoopSpacing();
        //checkDoWhileLoopSpacing();
        checkForLoopSpacing();
        checkForLoopSpacing();
    }

    private boolean hasWhitespace(List<SourceElement> elements, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) return false;

        // Making sure that we didn't skip over things        
        for (int index = fromIndex + 1; index < toIndex; index++) {
            SourceElement e = elements.get(index);
            
            if (!e.isWhitespaceOrComment()) {
                throw new RuntimeException("Non-comment found where whitespace or comment is expected: " + e);
            }
        }

        // We want whitespace at start, before any comments or anything else
        SourceElement e = elements.get(fromIndex);
        if (!e.isToken()) return false;
        if (!e.asTokenElement().isWhitespace()) return false;
        if (toIndex == fromIndex) return true;

        // We also need it at the end, after any comments or anything else
        e = elements.get(toIndex);
        if (!e.isToken()) return false;
        if (!e.asTokenElement().isWhitespace()) return false;

        return true;
    }

    public void checkIfSpacing() {
        javaFile.walkElements(IfStmt.class, element -> {
            final List<SourceElement> children = element.getElements();
            final IfStmt node = element.getNode();
            final Expression condition = node.getCondition();

            int ifKeywordIndex = element.indexOf("if");
            int conditionIndex = element.indexOf(condition, ifKeywordIndex + 1);
            int conditionStartIndex = element.lastIndexOf("(", ifKeywordIndex + 1, conditionIndex);

            if (!hasWhitespace(children, ifKeywordIndex + 1, conditionStartIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(ifKeywordIndex),
                        children.get(conditionStartIndex)
                    )
                );
            }

            int conditionEndIndex = element.lastIndexOf(")", conditionIndex + 1, -1);

            final Statement thenStatement = node.getThenStmt();
            int thenStatementIndex = element.indexOf(thenStatement, conditionEndIndex + 1);

            if (!hasWhitespace(children, conditionEndIndex + 1, thenStatementIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(conditionEndIndex),
                        children.get(thenStatementIndex)
                    )
                );
            }

            Statement elseStatement = node.getElseStmt().orElse(null);
            
            if (elseStatement != null) {
                int elseKeywordIndex = element.indexOf("else", thenStatementIndex + 1);

                if (!hasWhitespace(children, thenStatementIndex + 1, elseKeywordIndex - 1)) {
                    addViolation(
                        new MissingWhitespaceViolation(
                            children.get(thenStatementIndex),
                            children.get(elseKeywordIndex)
                        )
                    );
                }

                int elseStatementIndex = element.indexOf(elseStatement, elseKeywordIndex + 1);

                if (!hasWhitespace(children, elseKeywordIndex + 1, elseStatementIndex - 1)) {
                    addViolation(
                        new MissingWhitespaceViolation(
                            children.get(elseKeywordIndex),
                            children.get(elseStatementIndex)
                        )
                    );
                }
            }
        });
    }

    public void checkWhileLoopSpacing() {
        javaFile.walkElements(WhileStmt.class, element -> {
            final List<SourceElement> children = element.getElements();
            final WhileStmt node = element.getNode();
            final Expression condition = node.getCondition();
            final Statement bodyStatement = node.getBody();

            int whileKeywordIndex = element.indexOf("while");
            int conditionIndex = element.indexOf(condition, whileKeywordIndex + 1);
            int conditionStartIndex = element.lastIndexOf("(", whileKeywordIndex + 1, conditionIndex);

            if (!hasWhitespace(children, whileKeywordIndex + 1, conditionStartIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(whileKeywordIndex),
                        children.get(conditionStartIndex)
                    )
                );
            }

            int conditionEndIndex = element.lastIndexOf(")", conditionIndex + 1, -1);
            int bodyStatementIndex = element.indexOf(bodyStatement, conditionEndIndex + 1);

            if (!hasWhitespace(children, conditionEndIndex + 1, bodyStatementIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(conditionEndIndex),
                        children.get(bodyStatementIndex)
                    )
                );
            }
        });
    }

    public void checkForLoopSpacing() {
        javaFile.walkElements(WhileStmt.class, element -> {
            final List<SourceElement> children = element.getElements();
            final WhileStmt node = element.getNode();
            final Expression condition = node.getCondition();
            final Statement bodyStatement = node.getBody();

            int whileKeywordIndex = element.indexOf("while");
            int conditionIndex = element.indexOf(condition, whileKeywordIndex + 1);
            int conditionStartIndex = element.lastIndexOf("(", whileKeywordIndex + 1, conditionIndex);

            if (!hasWhitespace(children, whileKeywordIndex + 1, conditionStartIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(whileKeywordIndex),
                        children.get(conditionStartIndex)
                    )
                );
            }

            int conditionEndIndex = element.lastIndexOf(")", conditionIndex + 1, -1);
            int bodyStatementIndex = element.indexOf(bodyStatement, conditionEndIndex + 1);

            if (!hasWhitespace(children, conditionEndIndex + 1, bodyStatementIndex - 1)) {
                addViolation(
                    new MissingWhitespaceViolation(
                        children.get(conditionEndIndex),
                        children.get(bodyStatementIndex)
                    )
                );
            }
        });
    }
}
