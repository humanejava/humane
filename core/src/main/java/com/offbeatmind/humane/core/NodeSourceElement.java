package com.offbeatmind.humane.core;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public interface NodeSourceElement<N extends Node> extends SourceElement {

    public N getNode();
    
    @Override
    public default boolean isNode() {
        return true;
    }
    
    @Override
    public default String getName() {
        return getNode().getClass().getSimpleName();
    }
    
    public default boolean isComment() {
        return getNode() instanceof Comment;
    }
    
    public default boolean isBlockComment() {
        return getNode() instanceof BlockComment;
    }
    
    public default boolean isLineComment() {
        return getNode() instanceof LineComment;
    }

    public default boolean isChildOfClass(Class<? extends Node> nodeClass) {
        return nodeClass.isInstance(getNode());
    }
    
    public default Optional<Range> getRange() {
        return getNode().getRange();
    }
    
    public List<SourceElement> getElements();
    
    public default SourceElement getFirstElement() {
        return getElements().get(0);
    }
    
    public default SourceElement getLastElement() {
        List<SourceElement> elements = getElements();
        return elements.get(elements.size() - 1);
    }
    
    public default int indexOf(String text) {
        return indexOf(text, 0, -1);
    }
    
    public default int indexOf(String text, int startIndex) {
        return indexOf(text, startIndex, -1);
    }
    
    public default int indexOf(String text, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();
        for (int index = startIndex; index < endIndex; index++) {
            SourceElement child = children.get(index);
            if (
                child.isToken() &&
                child.asTokenElement().getText().contentEquals(text) && 
                !child.asTokenElement().isWhitespaceOrComment()
            ) {
                return index;
            }
        }
        return -1;
    }
    
    public default int lastIndexOf(String text) {
        return lastIndexOf(text, 0, -1);
    }
    
    public default int lastIndexOf(String text, int endIndex) {
        return lastIndexOf(text, 0, endIndex);
    }
    
    public default int lastIndexOf(String text, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();
        for (int index = endIndex - 1; index >= startIndex; index--) {
            SourceElement child = children.get(index);
            if (
                child.isToken() &&
                child.asTokenElement().getText().contentEquals(text) && 
                !child.asTokenElement().isWhitespaceOrComment()
            ) {
                return index;
            }
        }
        return -1;
    }
    
    public default int indexOf(Node node) {
        return indexOf(node, 0, -1);
    }
    
    public default int indexOf(Node node, int startIndex) {
        return indexOf(node, startIndex, -1);
    }
    
    public default int indexOf(Node node, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();
        for (int index = startIndex; index < endIndex; index++) {
            SourceElement child = children.get(index);
            if (child.isNode() && child.asNodeElement().getNode() == node) return index;
        }
        return -1;
    }
    
    public default int lastIndexOf(Node node) {
        return lastIndexOf(node, 0, -1);
    }
    
    public default int lastIndexOf(Node node, int startIndex) {
        return lastIndexOf(node, startIndex, -1);
    }
    
    public default int lastIndexOf(Node node, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();
        for (int index = endIndex - 1; index >= startIndex; index--) {
            SourceElement child = children.get(index);
            if (child.isNode() && child.asNodeElement().getNode() == node) return index;
        }
        return -1;
    }
    
    public static <T extends Node> NodeSourceElement<T> of(T node) {
        return NodeElement.of(node);
    }
    
    public default NodeSourceElement<?> getParent() {
        Optional<Node> parent = getNode().getParentNode();
        if (parent.isPresent()) return NodeElement.of(parent.get());
        return null;
    }
    
    public default NodeSourceElement<N> asNodeElement() {
        return this;
    }
    
    public default TokenSourceElement asTokenElement() {
        throw new RuntimeException("Cannot return a node as a token.");
    }
    
    public default boolean isWhitespace() {
        return false;
    }
    
    public default boolean isWhitespaceOrComment() {
        return isComment();
    }
    
    public default boolean is(Class<? extends Node> nodeClass) {
        return nodeClass.isAssignableFrom(getNode().getClass());
    }
    
    @SuppressWarnings("unchecked")
    public default NodeSourceElement<? extends Node> getParagraphNode() {
        if (
            this.is(Statement.class) || 
            this.is(VariableDeclarationExpr.class) ||
            this.is(CatchClause.class)
        ) {
            return (NodeSourceElement<? extends Node>) this;
        }
        NodeSourceElement<?> parent = getParent();
        if (parent == null) return null;
        return parent.getParagraphNode();
    }

    public void setBaseIndentation(int baseIndentation);
    
    public int getBaseIndentation();
}
