package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SourceTree {
    private final File rootDir;

    public SourceTree(File rootDir) {
        this.rootDir = rootDir;
    }

    public void acceptFileVisitor(Consumer<JavaFile> visitor) throws IOException {
        walk(rootDir, new ArrayList<String>(0), visitor);
    }

    private void walk(File file, List<String> packagePath, Consumer<JavaFile> visitor) throws IOException {
        if (file.isFile()) {
            if (file.getName().toLowerCase().endsWith(".java")) {
                final JavaFile javaFile = new JavaFile(this, packagePath, file);
                visitor.accept(javaFile);
            }
        } else if (file.isDirectory()) {
            List<String> newPackagePath = new ArrayList<String>(packagePath.size() + 1);
            newPackagePath.add(file.getName());
            for (File entry : file.listFiles()) walk(entry, newPackagePath, visitor);
        }
    }
}
