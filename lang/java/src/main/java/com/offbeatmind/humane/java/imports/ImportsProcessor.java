package com.offbeatmind.humane.java.imports;

import java.util.function.Consumer;

import com.github.javaparser.ast.ImportDeclaration;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeElement;

/**
 * Validates that star (and optionally static) imports aren't used.
 * 
 * @author humanejava
 *
 */
public class ImportsProcessor extends JavaFileProcessor {
    private static final boolean ALLOW_STATIC_IMPORTS = Boolean.getBoolean("allowStaticImports");

    public ImportsProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        javaFile.walkElements(ImportDeclaration.class, new Consumer<NodeElement<ImportDeclaration>>() {

            @Override
            public void accept(NodeElement<ImportDeclaration> importDeclaration) {
                if (importDeclaration.getNode().isAsterisk()) {
                    addViolation(new StarImportViolation(importDeclaration));
                }
                if (importDeclaration.getNode().isStatic() && !ALLOW_STATIC_IMPORTS) {
                    addViolation(new StaticImportViolation(importDeclaration));
                }
            }
        });
    }
}
