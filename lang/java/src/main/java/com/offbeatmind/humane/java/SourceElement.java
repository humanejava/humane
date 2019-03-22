package com.offbeatmind.humane.java;

import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

public abstract class SourceElement {
    public boolean isNode() {
        return false;
    }

    public boolean isToken() {
        return false;
    }

    public abstract NodeElement<?> getParent();

    public abstract Optional<Range> getRange();

    public int getFirstLineNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().begin.line;
    }

    public int getLastLineNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().end.line;
    }

    public int getFirstColumnNumber() {
        Optional<Range> range = getRange();
        if (!range.isPresent()) return -1;
        return range.get().begin.column;
    }

    public boolean isMultiline() {
        return getRange().get().begin.line != getRange().get().end.line;
    }

    public abstract NodeElement<?> asNodeSourceElement();

    public abstract TokenElement<?> asTokenElement();

    public abstract String getName();

    public abstract boolean isComment();

    public abstract boolean isWhitespace();

    public abstract boolean isWhitespaceOrComment();

    public abstract NodeElement<? extends Node> getParagraphNode();

    public int getParagraphIndentation() {
        NodeElement<? extends Node> paragraph = getParagraphNode();
        return (paragraph == null) ? 0 : paragraph.getBaseIndentation();
    }
}
