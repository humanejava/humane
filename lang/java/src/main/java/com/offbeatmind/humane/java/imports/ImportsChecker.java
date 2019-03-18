package com.offbeatmind.humane.java.imports;

import java.util.function.Consumer;

import com.github.javaparser.ast.ImportDeclaration;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.java.Checker;

public class ImportsChecker extends Checker {
    private static final boolean ALLOW_STATIC_IMPORTS = Boolean.getBoolean("allowStaticImports");

    public ImportsChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void check() {
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
