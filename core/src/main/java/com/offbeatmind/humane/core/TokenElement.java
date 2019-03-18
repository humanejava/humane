package com.offbeatmind.humane.core;

import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenTypes;
import com.github.javaparser.ast.type.ReferenceType;

public class TokenElement implements TokenSourceElement {
    private final JavaToken token;
    private final NodeElement<?> owner;

    public TokenElement(JavaToken token, NodeElement<?> owner) {
        this.token = token;
        this.owner = owner;
    }
    
    public JavaToken getToken() {
        return token;
    }
    
    @Override
    public NodeSourceElement<?> getParent() {
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
    
    public TokenElement asTokenElement() {
        return this;
    }
    
    @Override
    public String toString() {
        return "TokenElement{" + token.toString() + '}';
    }
}
