package com.offbeatmind.humane.java.layout;

import java.util.TreeSet;

import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.TokenElement;

public class ClosingMoreThanOneOtherStartLineViolation extends JavaViolation {

    private final TreeSet<Integer> startLinesOfScopesEnded;

    public ClosingMoreThanOneOtherStartLineViolation(
        TokenElement<?> enderToken, TreeSet<Integer> startLinesOfScopesEnded
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
