package com.offbeatmind.humane.java;

import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Range;
import com.github.javaparser.TokenTypes;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ReferenceType;

public class TokenElement<N extends Node> extends SourceElement {

    protected final JavaToken token;
    protected final NodeElement<N> owner;
    
    public TokenElement(JavaToken token, NodeElement<N> owner) {
        this.token = token;
        this.owner = owner;
    }

    @Override
    public boolean isToken() {
        return true;
    }

    @Override
    public String getName() {
        if (getToken().getCategory().isComment()) return getText().substring(0, 2);
        return getText();
    }

    public Optional<Range> getRange() {
        return getToken().getRange();
    }

    public String getText() {
        return getToken().getText();
    }

    public NodeElement<?> asNodeSourceElement() {
        throw new RuntimeException("Cannot return a token as a node.");
    }

    public boolean isKeyword() {
        final Category category = getToken().getCategory();
        return category.isKeyword();
    }

    public boolean isOperator() {
        final Category category = getToken().getCategory();
        return category.isOperator();
    }

    public boolean isComment() {
        final Category category = getToken().getCategory();
        return category.isComment();
    }

    public boolean isWhitespace() {
        final Category category = getToken().getCategory();
        return category.isWhitespace() || category.isEndOfLine();
    }

    public boolean isWhitespaceOrComment() {
        final Category category = getToken().getCategory();
        return category.isWhitespaceOrComment() || category.isEndOfLine();
    }

    public boolean isSeparator() {
        final Category category = getToken().getCategory();
        return category.isSeparator();
    }

    public boolean isIdentifier() {
        final Category category = getToken().getCategory();
        return category.isIdentifier();
    }

    public NodeElement<? extends Node> getParagraphNode() {
        return getParent().getParagraphNode();
    }

    public boolean isOneOf(String... possibilities) {
        final String text = getText();

        for (String possibility : possibilities) {
            if (possibility.contentEquals(text)) return true;
        }
        return false;
    }

    public JavaToken getToken() {
        return token;
    }

    @Override
    public NodeElement<?> getParent() {
        return owner;
    }

    public boolean isScopeStarter() {
        final JavaToken token = getToken();
        final int kind = token.getKind();
        final String text = token.getText();
    
        if ((text.length() == 1) && !TokenTypes.isComment(kind) && !owner.isComment()) {
            if ("{[(".contains(text)) {
                return true;
            } else if ("<".contentEquals(text) && owner.isChildOfClass(ReferenceType.class)) {
                return true;
            }
        }
        return false;
    }

    public boolean isScopeEnder() {
        final int kind = token.getKind();
        final String text = token.getText();
    
        if ((text.length() == 1) && !TokenTypes.isComment(kind) && !owner.isComment()) {
            if (")]}".contains(text)) {
                return true;
            } else if (">".contentEquals(text) && owner.isChildOfClass(ReferenceType.class)) {
                return true;
            }
        }
        return false;
    }

    public TokenElement<?> asTokenElement() {
        return this;
    }

    @Override
    public String toString() {
        return "TokenElement{" + token.toString() + '}';
    }
}
