package com.offbeatmind.humane.java.imports;

import java.util.function.Consumer;

import com.github.javaparser.ast.ImportDeclaration;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeSourceElement;

public class ImportsChecker extends JavaFileProcessor {
    private static final boolean ALLOW_STATIC_IMPORTS = Boolean.getBoolean("allowStaticImports");

    public ImportsChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        javaFile.walkElements(ImportDeclaration.class, new Consumer<NodeSourceElement<ImportDeclaration>>() {

            @Override
            public void accept(NodeSourceElement<ImportDeclaration> importDeclaration) {
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
