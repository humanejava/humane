package com.offbeatmind.humane.java.layout;

import com.offbeatmind.humane.core.TokenSourceElement;
import com.offbeatmind.humane.core.Violation;

public class TrailingScopeEndOnSeparateLineViolation extends Violation {

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
