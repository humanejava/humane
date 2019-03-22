package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.TokenElement;

public class TrailingScopeEndOnSeparateLineViolation extends JavaViolation {

    public TrailingScopeEndOnSeparateLineViolation(TokenElement<?> enderToken) {
        super(enderToken);
    }

    @Override
    public String getMessage() {
        return "Scope end token at "
            + getPosition() +
            " cannot be in this line - it must match the scope opening.";
    }

}
