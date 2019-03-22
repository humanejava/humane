package com.offbeatmind.humane.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;

public class NodeElement<N extends Node> extends SourceElement {

    static final DataKey<NodeElement<?>> DATAKEY = new DataKey<NodeElement<?>>() {};
    protected final N node;
    protected final ArrayList<SourceElement> sourceElements;
    protected int baseIndentation;
    
    NodeElement(N node) {
        this.node = node;
        sourceElements = new ArrayList<SourceElement>();
        baseIndentation = 0;
    }

    @Override
    public boolean isNode() {
        return true;
    }

    @Override
    public String getName() {
        return getNode().getClass().getSimpleName();
    }

    public boolean isComment() {
        return getNode() instanceof Comment;
    }

    public boolean isBlockComment() {
        return getNode() instanceof BlockComment;
    }

    public boolean isLineComment() {
        return getNode() instanceof LineComment;
    }

    public boolean isChildOfClass(Class<? extends Node> nodeClass) {
        return nodeClass.isInstance(getNode());
    }

    public Optional<Range> getRange() {
        return getNode().getRange();
    }

    public SourceElement getFirstElement() {
        return getElements().get(0);
    }

    public SourceElement getLastElement() {
        List<SourceElement> elements = getElements();
        return elements.get(elements.size() - 1);
    }

    public int indexOf(String text) {
        return indexOf(text, 0, -1);
    }

    public int indexOf(String text, int startIndex) {
        return indexOf(text, startIndex, -1);
    }

    public int indexOf(String text, int startIndex, int endIndex) {
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

    public int lastIndexOf(String text) {
        return lastIndexOf(text, 0, -1);
    }

    public int lastIndexOf(String text, int endIndex) {
        return lastIndexOf(text, 0, endIndex);
    }

    public int lastIndexOf(String text, int startIndex, int endIndex) {
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

    public int indexOf(Node node) {
        return indexOf(node, 0, -1);
    }

    public int indexOf(Node node, int startIndex) {
        return indexOf(node, startIndex, -1);
    }

    public int indexOf(Node node, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();

        for (int index = startIndex; index < endIndex; index++) {
            SourceElement child = children.get(index);
            if (child.isNode() && child.asNodeSourceElement().getNode() == node) return index;
        }
        return -1;
    }

    public int lastIndexOf(Node node) {
        return lastIndexOf(node, 0, -1);
    }

    public int lastIndexOf(Node node, int startIndex) {
        return lastIndexOf(node, startIndex, -1);
    }

    public int lastIndexOf(Node node, int startIndex, int endIndex) {
        final List<SourceElement> children = getElements();
        if (endIndex < 0) endIndex = children.size();

        for (int index = endIndex - 1; index >= startIndex; index--) {
            SourceElement child = children.get(index);
            if (child.isNode() && child.asNodeSourceElement().getNode() == node) return index;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeElement<T> of(T node) {
        NodeElement<T> e;
    
        if (node.containsData(DATAKEY)) {
            e = (NodeElement<T>) node.getData(DATAKEY);
        } else {
            e = new NodeElement<T>(node);
            node.setData(DATAKEY, e);
        }
        return e;
    }

    public NodeElement<?> getParent() {
        Optional<Node> parent = getNode().getParentNode();
        if (parent.isPresent()) return NodeElement.of(parent.get());
        return null;
    }

    public TokenElement<?> asTokenElement() {
        throw new RuntimeException("Cannot return a node as a token.");
    }

    public boolean isWhitespace() {
        return false;
    }

    public boolean isWhitespaceOrComment() {
        return isComment();
    }

    public boolean is(Class<? extends Node> nodeClass) {
        return nodeClass.isAssignableFrom(getNode().getClass());
    }

    public NodeElement<? extends Node> getParagraphNode() {
        if (
            this.is(Statement.class)
                ||
                this.is(VariableDeclarationExpr.class) ||
                this.is(CatchClause.class)
        ) {
            return (NodeElement<? extends Node>) this;
        }
        NodeElement<?> parent = getParent();
        if (parent == null) return null;
        return parent.getParagraphNode();
    }

    public N getNode() {
        return node;
    }

    boolean isNode(Node node) {
        return node == this.node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        NodeElement<?> that = (NodeElement<?>) o;
    
        return node.equals(that.node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public String toString() {
        return "NodeSourceElement{" + getNode() + '}';
    }

    protected void addElement(TokenElement<?> element) {
        sourceElements.add(element);
    
        ensureParentElement();
    }

    void addElement(NodeElement<?> element) {
        // Don't duplicate elements already in place
        if (!sourceElements.isEmpty()) {
            if (sourceElements.get(sourceElements.size() - 1) == element) return;
        }
        sourceElements.add(element);
    
        ensureParentElement();
    }

    private void ensureParentElement() {
        final Optional<Node> parent = node.getParentNode();
    
        if (parent.isPresent()) {
            of(parent.get()).addElement(this);
        }
    }

    public List<SourceElement> getElements() {
        return Collections.unmodifiableList(sourceElements);
    }

    public NodeElement<N> asNodeSourceElement() {
        return this;
    }

    public void setBaseIndentation(int baseIndentation) {
        this.baseIndentation = baseIndentation;
    }

    public int getBaseIndentation() {
        return baseIndentation;
    }
}
