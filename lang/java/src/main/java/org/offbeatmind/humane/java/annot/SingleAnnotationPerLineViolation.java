package org.offbeatmind.humane.java.annot;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class SingleAnnotationPerLineViolation extends Violation {

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
