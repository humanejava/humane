package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SourceTree<F extends SourceFile> {

    protected final File rootDir;

    public SourceTree(File rootDir) {
        this.rootDir = rootDir;
    }
    
    public File getRootDir() {
        return rootDir;
    }

    public void acceptFileVisitor(Consumer<F> visitor) throws IOException {
        walk(rootDir, new ArrayList<String>(0), visitor);
    }

    private void walk(File file, List<String> relativePath, Consumer<F> visitor) throws IOException {
        if (file.isFile()) {
            if (isSupportedFile(file)) {
                final F javaFile = constructFile(file, relativePath);
                visitor.accept(javaFile);
            }
        } else if (file.isDirectory()) {
            List<String> newPackagePath = new ArrayList<String>(relativePath.size() + 1);
            newPackagePath.add(file.getName());
            for (File entry : file.listFiles()) walk(entry, newPackagePath, visitor);
        }
    }
    
    protected abstract boolean isSupportedFile(File file) throws IOException;

    protected abstract F constructFile(File file, List<String> packagePath) throws IOException;

}
