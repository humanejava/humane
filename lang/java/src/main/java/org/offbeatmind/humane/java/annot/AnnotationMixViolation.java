package org.offbeatmind.humane.java.annot;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.Violation;

public class AnnotationMixViolation extends Violation {
    
    private final NodeSourceElement<AnnotationExpr> preexistingAnnotation;

    public AnnotationMixViolation(
            SourceElement violatingElement,
            NodeSourceElement<AnnotationExpr> preexistingAnnotation
    ) {
        super(violatingElement);
        this.preexistingAnnotation = preexistingAnnotation;
    }

    @Override
    public String getMessage() {
        return
                "Non-annotation code should not be present in the same line as annotation: " + 
                preexistingAnnotation.getRange().get();
    }

}
