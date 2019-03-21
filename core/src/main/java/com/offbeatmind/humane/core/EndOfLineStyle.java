package com.offbeatmind.humane.core;

import java.util.regex.Pattern;

/**
 * End of line terminator/marker type.
 * 
 * @author humanejava
 *
 */
public enum EndOfLineStyle {
    /**
     * None - the file is empty or all in one line.
     */
    NONE("(?!)", false),
    
    /**
     * CR followed by LF (standard).
     */
    CRLF("\\r\\n", false),
    
    /**
     * CR-only, used by old/classic Macs.
     */
    CR("\\r", false),
    
    /**
     * LF-only, used in Linux.
     */
    LF("\\n", false),
    
    /**
     * File uses an undesirable mixture of {@link #CRLF} and {@link CR}-only.
     */
    CRLF_AND_CR("\\r\\n?", false),

    /**
     * File uses an undesirable mixture of {@link #CRLF} and {@link LF}-only.
     */
    CRLF_AND_LF("\\r?\\n", false),

    /**
     * File uses an undesirable mixture of {@link #CRLF}, {@link CR}-only and {@link LF}-only.
     */
    CRLF_AND_CR_AND_LF("(\\r\\n)|\\r|\\n", false);

    /**
     * A {@link Pattern pattern} that matches a single occurrence of the end of line terminator.
     */
    private final Pattern sequencePattern;
    
    /**
     * A {@link Pattern pattern} that matches the entire file terminated using
     * {@code this} style of terminators.
     */
    private final Pattern pattern;
    
    /**
     * Whether this is a non-standard mixture of terminators, i.e. undesirable.
     */
    private final boolean mixture;

    /**
     * Sole constructor.
     * 
     * @param sequencePattern
     *     Value for {@link #sequencePattern}.
     * 
     * @param mixture
     *     Value for {@link #mixture}.
     *    
     */
    private EndOfLineStyle(String sequencePattern, boolean mixture) {
        this.sequencePattern = Pattern.compile(sequencePattern);
        this.pattern = Pattern.compile("^(([^\\r\\n]*)(" + sequencePattern + "))+[^\\r\\n]*$");
        this.mixture = mixture;
    }

    /**
     * Returns the regular expression that matches a single occurrence of the end of line terminator.
     */
    public String getSequenceRegEx() {
        return sequencePattern.pattern();
    }

    /**
     * Checks if '@{code this}' terminator is the one used throughout the specified text.
     * 
     * @param text Text to check for end of line style (e.g. the entire content of a source file).
     * 
     * @return {@code true} if '@{code this}' terminator is the one used throughout the specified text.
     * 
     * @see #recognize(String)
     */
    public boolean isUsedIn(String text) {
        return pattern.matcher(text).matches();
    }
    
    /**
     * Returns {@code true} if this is a non-standard mixture of terminators, i.e. undesirable.
     * 
     * @see #CRLF_AND_CR
     * @see #CRLF_AND_LF
     * @see #CRLF_AND_CR_AND_LF
     */
    public boolean isMixture() {
        return mixture;
    }

    /**
     * Attempts to recognize the end of line style used in the specified text.
     * 
     * @param text Text to check for end of line style (e.g. the entire content of a source file).
     * 
     * @return The style of the end of line terminator used throughout {@code text}.
     * 
     * @see #isUsedIn(String)
     */
    public static EndOfLineStyle recognize(String text) {
        if (text.isEmpty()) return NONE;

        for (EndOfLineStyle candidate : EndOfLineStyle.values()) {
            if (candidate.isUsedIn(text)) return candidate;
        }
        return NONE;
    }

}
