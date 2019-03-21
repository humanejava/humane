package com.offbeatmind.humane.java.annot;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.offbeatmind.humane.java.JavaViolation;
import com.offbeatmind.humane.java.NodeSourceElement;
import com.offbeatmind.humane.java.SourceElement;

public class SingleAnnotationPerLineViolation extends JavaViolation {

    private final NodeSourceElement<AnnotationExpr> preexistingAnnotation;

    public SingleAnnotationPerLineViolation(
        SourceElement violatingElement,
        NodeSourceElement<AnnotationExpr> preexistingAnnotation
    ) {
        super(violatingElement);
        this.preexistingAnnotation = preexistingAnnotation;
    }

    @Override
    public String getMessage() {
        return "There should be no more than one annotation per line and another is already there at "
            +
            preexistingAnnotation.getRange().get();
    }

}
