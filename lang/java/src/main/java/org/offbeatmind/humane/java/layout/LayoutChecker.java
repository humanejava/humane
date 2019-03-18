package org.offbeatmind.humane.java.layout;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.TreeSet;

import org.offbeatmind.humane.java.ElementsChecker;

import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenTypes;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.TokenSourceElement;

public class LayoutChecker extends ElementsChecker {
    private static final Boolean ALLOW_UNINDENTED_LINE_COMMENTS = true;

    final int[] requiredIndentations;
    final int[] actualIndentations;
    final Deque<SourceElement> scopeStack = new LinkedList<SourceElement>();

    final int indentWidth = 4;
    final int paragraphWrapIndentWidth = 4;

    int currentLineNumber = 0;
    int currentLineIndentation = 0;

    private final TreeSet<Integer> startLinesOfScopesEnded = new TreeSet<>();
    private final LinkedList<TokenSourceElement> violatingScopeEnderTokens = new LinkedList<>();

    public LayoutChecker(JavaFile javaFile) {
        super(javaFile);

        requiredIndentations = new int[javaFile.getLineCount() + 1];
        Arrays.fill(requiredIndentations, 0);

        actualIndentations = new int[javaFile.getLineCount() + 1];
        Arrays.fill(actualIndentations, 0);
    }

    @Override
    protected void finalizeProcessing() {
        checkScopesEnded();
    }

    @Override
    protected void processNode(NodeSourceElement<?> node) {
        checkVerticalSeparation(node);

        if (node.getNode() instanceof SwitchEntry) {
            super.processNode(node);
            final SourceElement poppedElement = scopeStack.pop();

            if (poppedElement != node) {
                throw new RuntimeException("Internal scoping error detected after switch entry.");
            }
        } else {
            super.processNode(node);
        }
    }

    private boolean isVerticallySignificant(NodeSourceElement<?> node) {
        final Node n = node.getNode();

        boolean significant =
            (n instanceof TypeDeclaration)
                ||
                (n instanceof BodyDeclaration) ||
                ((n instanceof Statement) && !isIfOrElseBlock((Statement) n)) ||
                (n instanceof VariableDeclarator) ||
                (n instanceof SwitchEntry);

        return significant;
    }

    private boolean isIfOrElseBlock(Statement node) {
        if (node instanceof BlockStmt) {
            Node parent = node.getParentNode().get();
            return parent instanceof IfStmt;
        } else {
            return false;
        }
    }

    private void checkVerticalSeparation(NodeSourceElement<?> node) {
        NodeSourceElement<?> lastSignificant = null;
        LinkedList<SourceElement> separators = new LinkedList<>();

        for (SourceElement e : node.getElements()) {
            if (e.isNode()) {
                NodeSourceElement<?> nodeElement = e.asNodeElement();

                if (isVerticallySignificant(nodeElement)) {
                    if (lastSignificant != null) {
                        if (nodeElement.isMultiline() || lastSignificant.isMultiline()) {
                            final int separation =
                                nodeElement.getFirstLineNumber() - lastSignificant.getLastLineNumber() - 1;

                            if (separation < 0) {
                                addViolation(new VerticalSeparationViolation(nodeElement, lastSignificant));
                            } else if (separation == 0) {
                                // TODO Some reduced separation may be OK, e.g.fall-through switch-case or after end of a block
                                if (
                                    nodeElement.isNode() && 
                                    (nodeElement.getNode() instanceof SwitchEntry) &&
                                    lastSignificant.isNode() &&
                                    (lastSignificant.getNode() instanceof SwitchEntry)
                                ) {
                                    final SwitchEntry lastEntry = (SwitchEntry)lastSignificant.getNode();

                                    if (!lastEntry.isEmpty()) {
                                        addViolation(new VerticalSeparationViolation(nodeElement, lastSignificant));
                                    }
                                } else {
                                    SourceElement lastElement = lastSignificant;
    
                                    while ((lastElement != null) && lastElement.isNode()) {
                                        lastElement = lastElement.asNodeElement().getLastElement();
                                    }
    
                                    if (
                                        (lastElement != null)
                                            &&
                                            lastElement.asTokenElement().getText().contentEquals("}") &&
                                            allWhitespace(separators)
                                    ) {
                                        // This may be OK. Warning?
                                    } else {
                                        addViolation(new VerticalSeparationViolation(nodeElement, lastSignificant));
                                    }
                                }
                            }
                        }
                    }
                    separators.clear();
                    lastSignificant = nodeElement;
                }
            } else {
                separators.add(e);
            }
        }
    }

    private boolean allWhitespace(LinkedList<SourceElement> elements) {
        for (SourceElement e : elements) if (!e.isWhitespace()) return false;
        return true;
    }

    protected void processToken(final TokenSourceElement currentTokenElement, boolean firstInNode) {
        final JavaToken currentJavaToken = currentTokenElement.getToken();
        final int currentTokenKind = currentJavaToken.getKind();
        final String currentTokenText = currentJavaToken.getText();
        final NodeSourceElement<?> currentOwner = currentTokenElement.getParent();

        final int tokenLine = currentTokenElement.getFirstLineNumber();

        final boolean isSwitchEntryStart =
            currentJavaToken.getCategory().isKeyword()
            && ("case".contentEquals(currentTokenText) || "default".contentEquals(currentTokenText))
            && currentOwner.getNode() instanceof SwitchEntry;

        boolean scopeEnded = false;

        if (!TokenTypes.isWhitespace(currentTokenKind)) {
            if (tokenLine > currentLineNumber) {
                checkScopesEnded();

                // We've arrived to a new line
                currentLineNumber = tokenLine;
                currentLineIndentation = currentTokenElement.getFirstColumnNumber() - 1;
                actualIndentations[currentLineNumber] = currentLineIndentation;
                
                if (firstInNode) {
                    getCurrentNode().setBaseIndentation(currentLineIndentation);
                }

                int requiredIndentation;

                // See if we should unindent first
                if (currentTokenElement.isScopeEnder()) {
                    TokenSourceElement startingToken = endBracedScope(currentTokenElement, true);
                    scopeEnded = true;
                    // Indentation must match the indentation of the LINE with the starting token
                    // Note that there could be additional open scopes from the same line.
                    // We ignore those and do NOT indent more relative to them as all of them
                    // together resulted with a single additional indentation just to be undone.
                    requiredIndentation = actualIndentations[startingToken.getFirstLineNumber()];
                } else {
                    if (scopeStack.isEmpty()) {
                        requiredIndentation = 0;
                    } else {
                        final SourceElement scopeStart = scopeStack.peekFirst();
                        requiredIndentation = actualIndentations[scopeStart.getFirstLineNumber()] + indentWidth;
                    }
                }

                // Check if additional, wrapping indentation is needed.
                NodeSourceElement<? extends Node> paragraph = getCurrentNode().getParagraphNode();
                
                if ((paragraph != null) && (paragraph.getFirstLineNumber() < currentLineNumber)) {
                    if (currentTokenElement.isOneOf(")", "]", "}", "else", ">")) {
                        // don't add
                    } else {
                        requiredIndentation =
                            Math
                                .max(
                                    requiredIndentation,
                                    paragraph.getBaseIndentation() + paragraphWrapIndentWidth
                                );
                    }
                }

                requiredIndentations[currentLineNumber] = requiredIndentation;

                final boolean allowExtraIndentation;

                if ("{".equals(currentTokenText)) {
                    allowExtraIndentation = currentOwner.getNode() instanceof LambdaExpr;
                } else if (isSwitchEntryStart) {
                    allowExtraIndentation = false;
                } else if (scopeStack.peekFirst() instanceof SwitchEntry) {
                    allowExtraIndentation = false;
                } else {
                    final SourceElement currentScope = scopeStack.peekFirst();

                    if ((currentScope != null) && currentScope.isNode()) {
                        if (((NodeSourceElement<?>) currentScope).getNode() instanceof SwitchEntry) {
                            allowExtraIndentation = false;
                        } else {
                            allowExtraIndentation = !currentTokenElement.isScopeEnder();
                        }
                    } else {
                        allowExtraIndentation = !currentTokenElement.isScopeEnder();
                    }
                }

                final boolean lineComment =
                    currentOwner.isLineComment()
                        ||
                        currentJavaToken.getCategory().isComment() && currentTokenText.startsWith("//");

                if (lineComment && (currentLineIndentation == 0) && ALLOW_UNINDENTED_LINE_COMMENTS) {
                    // Special case, Eclipse does this on Ctrl+/
                } else {
                    if (allowExtraIndentation) {
                        if (currentLineIndentation < requiredIndentation) {
                            addViolation(
                                new InsufficientIndentationViolation(
                                    currentTokenElement,
                                    currentLineIndentation,
                                    requiredIndentation
                                )
                            );
                            //System.err.println("Insufficient indentation (" + currentLineIndentation + " vs " + requiredIndentation + ") in line " + currentLineNumber);
                        } else if (currentLineIndentation > requiredIndentation) {
                            System.out
                                .println(
                                    "Allowed extra indentation ("
                                        + currentLineIndentation + " vs " + requiredIndentation + ") in line "
                                        + currentLineNumber
                                );
                        }
                    } else {
                        if (currentLineIndentation != requiredIndentation) {
                            addViolation(
                                new ExactIndentationViolation(
                                    currentTokenElement,
                                    currentLineIndentation,
                                    requiredIndentation
                                )
                            );
                            //System.err.println("Too much indentation (" + currentLineIndentation + " vs " + requiredIndentation + ") in line " + currentLineNumber);
                        }
                    }
                }
            } else if (isSwitchEntryStart) {
                addViolation(new SwitchCaseNotFirstInLineViolation(currentTokenElement));
            }
        }

        //System.out.println(token.getRange().get() + " " + token.getCategory() + "/" + kind + ": |" + text + "|");
        if (isSwitchEntryStart) {
            scopeStack.push(currentOwner);
        } else if (currentTokenElement.isScopeStarter()) {
            startBracedScope(currentTokenElement);
//            openScopesCountDelta++;

        } else if (currentTokenElement.isScopeEnder() && !scopeEnded) {
            endBracedScope(currentTokenElement, false);
//            openScopesCountDelta--;
            // TODO verify
        }
    }

    void checkScopesEnded() {
        if (!violatingScopeEnderTokens.isEmpty()) {
            for (TokenSourceElement t : violatingScopeEnderTokens) {
                addViolation(new ClosingMoreThanOneOtherStartLineViolation(t, startLinesOfScopesEnded));
            }
//            throw new RuntimeException("Line " + currentLineNumber + " ends scopes starting in multiple prior lines: " + startLinesOfScopesEnded);
        }
        startLinesOfScopesEnded.clear();
        violatingScopeEnderTokens.clear();
    }

    void startBracedScope(final TokenSourceElement starterToken) {
        scopeStack.push(starterToken);
        //System.out.println("++++++++++++++ [-->#" + scopeStack.size() + "] " + starterToken.getText() + " of " + starterToken.getParent().getNode().getClass().getSimpleName() + "@" + starterToken.getParent().getRange().get() + " at " + starterToken.getRange().get());
    }

    TokenSourceElement endBracedScope(final TokenSourceElement enderToken, final boolean firstInLine) {
        //System.out.println("~~~~~~~~~~~~~~ [-->#" + scopeStack.size() + "] " + enderToken.getText() + " of " + enderToken.getParent().getNode().getClass().getSimpleName() + "@" + enderToken.getParent().getRange().get() + " at " + enderToken.getRange().get());
        final SourceElement starterElement = scopeStack.pop();

        if (!starterElement.isToken()) {
            throw new RuntimeException(
                "Closing brace '"
                    + enderToken.getText() + "' at " + enderToken.getRange().get().begin +
                    " ending a scope that started with a non-token at " + starterElement.getRange().get().begin
            );
        }
        final TokenSourceElement starterToken = (TokenSourceElement) starterElement;
        //System.out.println("-------------- [-->#" + scopeStack.size() + "] " + starterToken.getText() + " of " + starterToken.getParent().getNode().getClass().getSimpleName() + "@" + starterToken.getParent().getRange().get() + " at " + starterToken.getRange().get());
        if (enderToken.getParent() != starterToken.getParent()) {
            throw new RuntimeException(
                "Closing token "
                    + enderToken.getText()
                    + " of " + enderToken.getParent().getNode().getClass().getSimpleName()
                    + " " + enderToken.getParent().toString()
                    + " at " + enderToken.getRange().get()
                    + " is not in the same node as the starting token "
                    + starterToken.getText()
                    + " of " + starterToken.getParent().getNode().getClass().getSimpleName()
                    + " " + starterToken.getParent().toString()
                    + " at " + starterToken.getRange().get()
            );
        }

        String expectedEnd;

        switch (starterToken.getText()) {
            case "{":
                expectedEnd = "}";
                break;

            case "[":
                expectedEnd = "]";
                break;

            case "(":
                expectedEnd = ")";
                break;

            case "<":
                expectedEnd = ">";
                break;

            default:
                throw new RuntimeException("Internal error: unrecognized scope start: " + starterToken.getText());
        }
        if (!expectedEnd.contentEquals(enderToken.getText())) {
            throw new RuntimeException(
                "Scope end '"
                    + enderToken.getText() + "' " +
                    "does not match its start: '" + starterToken.getText() + "'"
            );
        }

        final int startLine = starterToken.getFirstLineNumber();
        final int endLine = enderToken.getFirstLineNumber();

        if (startLine == endLine) {
            // Entire scope is in a single line, no indentation considerations.
        } else {
            // Scope started in a prior line. Indentation must match.
            final int endLineIndentation = actualIndentations[endLine];
            final int endTokenPosition = enderToken.getFirstColumnNumber();

            final int indentationMin =
                Math
                    .max(
                        requiredIndentations[startLine],
                        actualIndentations[startLine]
                    );

            final int startTokenPosition = Math.max(indentationMin + 1, starterToken.getFirstColumnNumber());
            // or more *IF* this is NOT the first token in line

            if (endLineIndentation < indentationMin) {
                addViolation(new InsufficientIndentationViolation(enderToken, endLineIndentation, indentationMin));
//                throw new RuntimeException(
//                        "Insufficient indentation (" + endLineIndentation + " vs " + indentationMin +") of the line with the ending token: " + enderToken.getRange().get());
            } else if (!firstInLine) {
                // Only the first token in line can close a scope that started in a
                // line starting scopes not already closed by other tokens in this line.
                if (!startLinesOfScopesEnded.contains(startLine)) {
                    startLinesOfScopesEnded.add(startLine);
                    addViolation(new TrailingScopeEndOnSeparateLineViolation(enderToken));
                }
            }

            // There is at least one line in between the scope start and end.
            // Indentation of this line must be at least one level less than the indentation of lines inside the scope.
            for (int line = startLine + 1; line < endLine; line++) {
                if (!javaFile.getLine(line).trim().isEmpty()) {
                    int indentation = actualIndentations[line];
                    int indentationMax = indentation - indentWidth;

                    if ((indentation > indentationMin) && (endLineIndentation > indentationMax)) {
                        addViolation(
                            new InsufficientUnindentationViolation(enderToken, endLineIndentation, indentationMax)
                        );
//                        throw new RuntimeException(
//                                "Line that ends a scope (" + endLine + ")" + 
//                                " must be indented less than lines inside the scope," +
//                                " e.g. line #" + line + " is indented " + indentation + " spaces."
//                        );
                    }
                }
            }

            if (endTokenPosition == startTokenPosition) {
                // We're good.
            } else if (firstInLine) {
                // Figure out the alternate allowable indentation of this token.
                // We always want '}' and ']' to match exactly the same indentation as the line that opened them
                // and not allow alternatives. However, we allow ')' to match the position of the opening '('.
                int altIndentation = ")".contentEquals(enderToken.getText()) ? indentationMin : startTokenPosition - 1;

                if (altIndentation == indentationMin) {
                    if (endLineIndentation != indentationMin) {
                        addViolation(new ExactIndentationViolation(enderToken, endLineIndentation, indentationMin));
//                        throw new RuntimeException(
//                                "Line " + endLine +" must be indented " + indentationMin + 
//                                " spaces or match the placement of the opening token."
//                        );
                    }
                } else if ((endLineIndentation != indentationMin) && (endLineIndentation != altIndentation)) {
                    addViolation(
                        new ChoiceIndentationViolation(enderToken, endLineIndentation, indentationMin, altIndentation)
                    );
//                    throw new RuntimeException(
//                            "Line " + endLine +" must be indented either " + 
//                            indentationMin + ", to match the scope starting line indentation, or " + 
//                            altIndentation + ", to match the position of the opening token.");
                }
            }

            // Prevent ending scopes started in multiple prior lines in a single line
            if (startLine != endLine) {
                startLinesOfScopesEnded.add(startLine);

                if (startLinesOfScopesEnded.size() > 1) {
                    violatingScopeEnderTokens.add(enderToken);
                }
            }
        }

        return starterToken;
    }

}
