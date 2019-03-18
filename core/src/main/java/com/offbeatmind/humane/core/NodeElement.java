package com.offbeatmind.humane.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;

class NodeElement<N extends Node>  implements NodeSourceElement<N> {
    static final DataKey<NodeElement<?>> DATAKEY = new DataKey<NodeElement<?>>() {};
    
    private final N node;
    private final ArrayList<SourceElement> sourceElements;

    private int baseIndentation;

    NodeElement(N node) {
        this.node = node;
        sourceElements = new ArrayList<SourceElement>();
        baseIndentation = 0;
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
        return "NodeElement{" + getNode() + '}';
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeElement<T> of(T node) {
        NodeElement<T> e;
        if (node.containsData(DATAKEY)) {
            e = (NodeElement<T>)node.getData(DATAKEY);
        } else {
            e = new NodeElement<T>(node);
            node.setData(DATAKEY, e);
        }
        return e;
    }
    
    void addElement(TokenSourceElement element) {
        sourceElements.add(element);
        
        ensureParentElement();
    }
    
    void addElement(NodeSourceElement<?> element) {
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
    
    public NodeElement<N> asNodeElement() {
        return this;
    }
    
    public void setBaseIndentation(int baseIndentation) {
        this.baseIndentation = baseIndentation;
    }
    
    public int getBaseIndentation() {
        return baseIndentation;
    }
}
