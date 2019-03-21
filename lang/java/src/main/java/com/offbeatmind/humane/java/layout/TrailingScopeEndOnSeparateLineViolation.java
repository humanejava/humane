package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.TokenSourceElement;

public class TrailingScopeEndOnSeparateLineViolation extends JavaViolation {

    public TrailingScopeEndOnSeparateLineViolation(TokenSourceElement enderToken) {
        super(enderToken);
    }

    @Override
    public String getMessage() {
        return "Scope end token at "
            + getPosition() +
            " cannot be in this line - it must match the scope opening.";
    }

}
