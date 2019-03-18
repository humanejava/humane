package com.offbeatmind.humane.java.layout;

import java.util.TreeSet;

import com.offbeatmind.humane.core.TokenSourceElement;
import com.offbeatmind.humane.core.Violation;

public class ClosingMoreThanOneOtherStartLineViolation extends Violation {

    private final TreeSet<Integer> startLinesOfScopesEnded;

    public ClosingMoreThanOneOtherStartLineViolation(
        TokenSourceElement enderToken, TreeSet<Integer> startLinesOfScopesEnded
    ) {
        super(enderToken);

        this.startLinesOfScopesEnded = new TreeSet<>(startLinesOfScopesEnded);
    }

    @Override
    public String getMessage() {
        return "Multiple scopes starting in multiple different prior lines "
            +
            startLinesOfScopesEnded +
            " ended in line " +
            getLineNumber();
    }

}
