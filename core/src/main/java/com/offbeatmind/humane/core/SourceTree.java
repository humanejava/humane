package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for language-specific source trees.
 * 
 * @author humanejava
 *
 * @param <F> Type of the {@linkplain SourceFile source file} in this source tree.
 */
public abstract class SourceTree<F extends SourceFile> {

	/**
	 * Root directory of the source tree.
	 */
    protected final File rootDir;

    public SourceTree(File rootDir) {
        this.rootDir = rootDir;
    }
    
    /**
     * Returns the root directory of this tree.
     */
    public File getRootDir() {
        return rootDir;
    }

    /**
     * Invokes {@code visitor.}{@link Consumer#accept(Object) accept(sourceFile)}
     * for each source file in the tree.
     * 
     * @param visitor
     *     Visitor to call {@link Consumer#accept(Object)} on for each file.
     * 
     * @throws IOException
     */
    public void acceptFileVisitor(Consumer<F> visitor) throws IOException {
        walk(rootDir, new ArrayList<String>(0), visitor);
    }
    
    /**
     * To be implemented by subclasses: returns {@code true} if the specified
     * file is a source file for this tree's language.
     * 
     * @param file
     *     File to check.
     *     
     * @param recursePath
     *     Directories recursed from root to this file.
     *     
     * @return
     *     {@code true} if and only if the file specified is to be processed.
     * 
     * @throws IOException
     */
    protected abstract boolean isSupportedFile(File file, List<String> recursePath) throws IOException;

    /**
     * A factory method that constructs an instance of a correct subtype of
     * {@link SourceFile} for the given arguments.
     * 
     * @param file
     *     File to construct a {@link SourceFile} instance for.
     *     
     * @param recursePath
     *     Directories recursed from root to this file.
     *     
     * @return
     *     A corresponding {@link SourceFile}-subtype instance.
     *     
     * @throws IOException
     */
    protected abstract F constructFile(File file, List<String> recursePath) throws IOException;

    /**
     * Recurses the directory structure in search of source files and
     * calls {@code visitor.}{@link Consumer#accept(Object) accept(sourceFile)}
     * for each.
     * 
     * @param file
     *     File to process or directory to recurse into.
     *     
     * @param relativePath
     *     Names of directories recursed so far. 
     *     
     * @param visitor
     *     Visitor to call {@link Consumer#accept(Object)} on for each file.
     *     
     * @throws IOException
     */
    private void walk(File file, List<String> recursePath, Consumer<F> visitor) throws IOException {
        if (file.isFile()) {
            if (isSupportedFile(file, recursePath)) {
                final F javaFile = constructFile(file, recursePath);
                visitor.accept(javaFile);
            }
        } else if (file.isDirectory()) {
            List<String> newPackagePath = new ArrayList<String>(recursePath.size() + 1);
            newPackagePath.add(file.getName());
            for (File entry : file.listFiles()) walk(entry, newPackagePath, visitor);
        }
    }
}
