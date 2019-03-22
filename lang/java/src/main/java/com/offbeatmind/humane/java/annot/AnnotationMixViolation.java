package com.offbeatmind.humane.java.annot;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeElement;
import com.offbeatmind.humane.java.SourceElement;

public class AnnotationMixViolation extends JavaViolation {

    private final NodeElement<AnnotationExpr> preexistingAnnotation;

    public AnnotationMixViolation(
        SourceElement violatingElement,
        NodeElement<AnnotationExpr> preexistingAnnotation
    ) {
        super(violatingElement);
        this.preexistingAnnotation = preexistingAnnotation;
    }

    @Override
    public String getMessage() {
        return "Non-annotation code should not be present in the same line as annotation: "
            +
            preexistingAnnotation.getRange().get();
    }

}
