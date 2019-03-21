package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * A base class for language-specific processors.
 * 
 * @author humanejava
 *
 * @param <T> Type of the {@linkplain SourceTree source tree} used by the language.
 * 
 * @param <F> Type of the {@linkplain SourceFile source file} used by the language.
 */
public abstract class LanguageProcessor<T extends SourceTree<F>, F extends SourceFile> {
    
	/**
	 * Invokes the processor for the specified source trees.
	 * 
	 * @param fixErrors
	 *     Whether or not to only validate ({@code false}) or also attempt to
	 *     correct errors ({@code true}).
	 * 
	 * @param sourceTrees
	 *     Source trees to process.
	 *     
	 * @return
	 *     Returns {@code true} if no violations have been detected and left unfixed.
	 *     
	 * @throws IOException
	 * 
	 * @see #process(boolean, Collection)
	 */
	@SafeVarargs
    public final boolean process(boolean fixErrors, T... sourceTrees) throws IOException {
        return process(fixErrors, Arrays.asList(sourceTrees));
    }
    
	/**
	 * Invokes the processor for the specified source trees.
	 * 
	 * @param fixErrors
	 *     Whether or not to only validate ({@code false}) or also attempt to
	 *     correct errors ({@code true}).
	 * 
	 * @param sourceTrees
	 *     Source trees to process.
	 *     
	 * @return
	 *     Returns {@code true} if no violations have been detected and left unfixed.
	 *     
	 * @throws IOException
	 * 
	 * @see {@link #process(boolean, SourceTree...)}
	 */
    public abstract boolean process(boolean fixErrors, Collection<T> sourceTrees) throws IOException;

    /**
     * A factory method for this language's {@linkplain SourceTree source tree}
     * objects.
     * 
     * @param treeRoot A root of the source tree to create.
     */
    public abstract T constructSourceTree(File treeRoot);

}
