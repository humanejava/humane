package org.offbeatmind.humane.java.compilationunit;

import org.offbeatmind.humane.java.Checker;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;

public class CompilationUnitContentChecker extends Checker {

    public CompilationUnitContentChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void check() {
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
                addViolation(new CompilationUnitContentViolation(NodeSourceElement.of(child)));
            }
        }
    }
}
