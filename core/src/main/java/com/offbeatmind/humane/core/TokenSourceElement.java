package com.offbeatmind.humane.core;

import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

public interface TokenSourceElement extends SourceElement {

    @Override
    public default boolean isToken() {
        return true;
    }

    @Override
    public default String getName() {
        if (getToken().getCategory().isComment()) return getText().substring(0, 2);
        return getText();
    }

    public JavaToken getToken();

    public default Optional<Range> getRange() {
        return getToken().getRange();
    }

    public boolean isScopeStarter();

    public boolean isScopeEnder();

    public default String getText() {
        return getToken().getText();
    }

    public default NodeSourceElement<?> asNodeElement() {
        throw new RuntimeException("Cannot return a token as a node.");
    }

    public default TokenSourceElement asTokenElement() {
        return this;
    }

    public default boolean isKeyword() {
        final Category category = getToken().getCategory();
        return category.isKeyword();
    }

    public default boolean isOperator() {
        final Category category = getToken().getCategory();
        return category.isOperator();
    }

    public default boolean isComment() {
        final Category category = getToken().getCategory();
        return category.isComment();
    }

    public default boolean isWhitespace() {
        final Category category = getToken().getCategory();
        return category.isWhitespace() || category.isEndOfLine();
    }

    public default boolean isWhitespaceOrComment() {
        final Category category = getToken().getCategory();
        return category.isWhitespaceOrComment() || category.isEndOfLine();
    }

    public default boolean isSeparator() {
        final Category category = getToken().getCategory();
        return category.isSeparator();
    }

    public default boolean isIdentifier() {
        final Category category = getToken().getCategory();
        return category.isIdentifier();
    }

    public default NodeSourceElement<? extends Node> getParagraphNode() {
        return getParent().getParagraphNode();
    }

    public default boolean isOneOf(String... possibilities) {
        final String text = getText();

        for (String possibility : possibilities) {
            if (possibility.contentEquals(text)) return true;
        }
        return false;
    }
}
