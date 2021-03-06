package com.offbeatmind.humane.java.annot;

import java.util.HashMap;

import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.offbeatmind.humane.java.JavaFile;
import com.offbeatmind.humane.java.JavaFileProcessor;
import com.offbeatmind.humane.java.NodeElement;
import com.offbeatmind.humane.java.SourceElement;

/**
 * Validates that complex annotations are on their own lines.  
 * 
 * @author humanejava
 *
 */
public class AnnotationLayoutProcessor extends JavaFileProcessor {

    public AnnotationLayoutProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        javaFile.walkElements(TypeDeclaration.class, this::checkMaxOneAnnotationPerLine);
        javaFile.walkElements(CallableDeclaration.class, this::checkMaxOneAnnotationPerLine);
        javaFile.walkElements(VariableDeclarationExpr.class, this::checkMaxOneAnnotationPerLine);
    }

    private void checkMaxOneAnnotationPerLine(
        @SuppressWarnings("rawtypes") NodeElement<? extends NodeWithAnnotations> e
    ) {
        @SuppressWarnings("unchecked")
        final NodeList<AnnotationExpr> annotations = e.getNode().getAnnotations();
        
        final HashMap<Integer, AnnotationExpr> linesToAnnotations = new HashMap<>();

        for (AnnotationExpr annotation : annotations) {
            final Range range = annotation.getRange().get();
            
            for (int line = range.begin.line; line <= range.end.line; line++) {
                final AnnotationExpr preexistingAnnotation = linesToAnnotations.get(line);
                
                if (preexistingAnnotation != null) {
                    addViolation(
                        new SingleAnnotationPerLineViolation(
                            NodeElement.of(annotation),
                            NodeElement.of(preexistingAnnotation)
                        )
                    );
                } else {
                    linesToAnnotations.put(line, annotation);
                }
            }
        }

        final NodeElement<?> parent = e.getParent();
        
        for (SourceElement s : parent.getElements()) {
            if ((s != e) & !s.isComment() && !s.isWhitespace()) {
                final Range range = s.getRange().get();
                
                for (int line = range.begin.line; line <= range.end.line; line++) {
                    if (linesToAnnotations.containsKey(line)) {
                        final AnnotationExpr preexistingAnnotation = linesToAnnotations.get(line);
                        
                        addViolation(
                            new AnnotationMixViolation(
                                s,
                                NodeElement.of(preexistingAnnotation)
                            )
                        );
                    }
                }
            }
        }
    }

}
