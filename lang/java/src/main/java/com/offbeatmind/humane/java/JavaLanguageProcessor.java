package com.offbeatmind.humane.java;

import java.io.IOException;
import java.util.function.Consumer;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.LanguageProcessor;
import com.offbeatmind.humane.core.SourceTree;
import com.offbeatmind.humane.java.annot.AnnotationLayoutChecker;
import com.offbeatmind.humane.java.braces.DoWhileLoopBracesChecker;
import com.offbeatmind.humane.java.braces.ForEachLoopBracesChecker;
import com.offbeatmind.humane.java.braces.ForLoopBracesChecker;
import com.offbeatmind.humane.java.braces.IfElseBracesChecker;
import com.offbeatmind.humane.java.braces.WhileLoopBracesChecker;
import com.offbeatmind.humane.java.compilationunit.CompilationUnitContentChecker;
import com.offbeatmind.humane.java.empty.EmptyStatementsChecker;
import com.offbeatmind.humane.java.imports.ImportsChecker;
import com.offbeatmind.humane.java.layout.ItemsPerLineChecker;
import com.offbeatmind.humane.java.layout.LayoutChecker;
import com.offbeatmind.humane.java.multidecl.MultipleDeclarationsChecker;
import com.offbeatmind.humane.java.spaces.BasicWhitespaceChecker;

public class JavaLanguageProcessor extends LanguageProcessor {

    public static final JavaLanguageProcessor INSTANCE = new JavaLanguageProcessor();
    
    @Override
    public void process(boolean fixErrors, SourceTree... sourceTrees) throws IOException {
        
        for (SourceTree sourceTree: sourceTrees) {
            sourceTree.acceptFileVisitor(new Consumer<JavaFile>() {
    
                @Override
                public void accept(JavaFile f) {
                    (new LayoutChecker(f)).process(fixErrors);
                    (new IfElseBracesChecker(f)).process(fixErrors);
                    (new WhileLoopBracesChecker(f)).process(fixErrors);
                    (new DoWhileLoopBracesChecker(f)).process(fixErrors);
                    (new ForLoopBracesChecker(f)).process(fixErrors);
                    (new ForEachLoopBracesChecker(f)).process(fixErrors);
                    (new ImportsChecker(f)).process(fixErrors);
                    (new CompilationUnitContentChecker(f)).process(fixErrors);
                    (new EmptyStatementsChecker(f)).process(fixErrors);
                    (new MultipleDeclarationsChecker(f)).process(fixErrors);
                    (new BasicWhitespaceChecker(f)).process(fixErrors);
                    (new ItemsPerLineChecker(f)).process(fixErrors);
                    (new AnnotationLayoutChecker(f)).process(fixErrors);
    
                    f.printViolations();
                }
            });
        }
    }
}
