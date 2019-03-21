package com.offbeatmind.humane.java;

import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

public interface SourceElement {
    public default boolean isNode() {
        return false;
    }

    public default boolean isToken() {
        return false;
    }

    public NodeSourceElement<?> getParent();

    public Optional<Range> getRange();

    public default int getFirstLineNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().begin.line;
    }

    public default int getLastLineNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().end.line;
    }

    public default int getFirstColumnNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().begin.column;
    }

    public default boolean isMultiline() {
        return getRange().get().begin.line != getRange().get().end.line;
    }

    public NodeSourceElement<?> asNodeElement();

    public TokenSourceElement asTokenElement();

    public String getName();

    public boolean isComment();

    public boolean isWhitespace();

    public boolean isWhitespaceOrComment();

    public NodeSourceElement<? extends Node> getParagraphNode();

    public default int getParagraphIndentation() {
        NodeSourceElement<? extends Node> paragraph = getParagraphNode();
        return (paragraph == null) ? 0 : paragraph.getBaseIndentation();
    }
}
