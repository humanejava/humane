package com.offbeatmind.humane.java;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.offbeatmind.humane.core.SourceTree;

/**
 * Represents a source tree - a walkable directory structure containing source files.
 * @author humanejava
 *
 * @param <T>
 */
public class JavaSourceTree extends SourceTree<JavaFile> {
    public JavaSourceTree(File rootDir) {
        super(rootDir);
    }

    @Override
    protected boolean isSupportedFile(File file, List<String> recursePath) throws IOException {
        return file.getName().toLowerCase().endsWith(".java");
    }
    
    @Override
    protected JavaFile constructFile(File file, List<String> packagePath) throws IOException {
        return new JavaFile(this, packagePath, file);
    }
}
