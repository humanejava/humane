package com.offbeatmind.humane.java.compilationunit;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeElement;

/**
 * Validates that there is at most top level one compilation unit
 * (i.e. class or interface) per file. 
 * 
 * @author humanejava
 *
 */
public class CompilationUnitContentProcessor extends JavaFileProcessor {

    public CompilationUnitContentProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        final String allowedName = javaFile.getUnitName();

        for (Node child : javaFile.getCompilationUnit().getChildNodes()) {
            boolean violating;
            
            if (child instanceof ClassOrInterfaceDeclaration) {
                final ClassOrInterfaceDeclaration decl = (ClassOrInterfaceDeclaration) child;
                violating = !decl.getName().getIdentifier().contentEquals(allowedName);
            } else if (child instanceof EnumDeclaration) {
                final EnumDeclaration decl = (EnumDeclaration) child;
                violating = !decl.getName().getIdentifier().contentEquals(allowedName);
            } else {
                violating = false;
            }

            if (violating) {
                addViolation(new CompilationUnitContentViolation(NodeElement.of(child)));
            }
        }
    }
}
