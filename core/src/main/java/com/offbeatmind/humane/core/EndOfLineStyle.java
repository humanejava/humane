package com.offbeatmind.humane.core;

import java.util.regex.Pattern;

public enum EndOfLineStyle {
    NONE("(?!)"),
    CRLF("\\r\\n"),
    CR("\\r"),
    LF("\\n"),
    CRLF_AND_CR("\\r\\n?"),
    CRLF_AND_LF("\\r?\\n"),
    CRLF_AND_CR_AND_LF("(\\r\\n)|\\r|\\n");

    private final Pattern sequencePattern;
    private final Pattern pattern;

    private EndOfLineStyle(String sequencePattern) {
        this.sequencePattern = Pattern.compile(sequencePattern);
        this.pattern = Pattern.compile("^(([^\\r\\n]*)(" + sequencePattern + "))+[^\\r\\n]*$");
    }

    public String getSequencePattern() {
        return sequencePattern.pattern();
    }

    public boolean isUsedIn(String text) {
        return pattern.matcher(text).matches();
    }

    public static EndOfLineStyle recognize(String text) {
        if (text.isEmpty()) return NONE;

        for (EndOfLineStyle candidate : EndOfLineStyle.values()) {
            if (candidate.isUsedIn(text)) return candidate;
        }
        return NONE;
    }

}
