package com.offbeatmind.humane.java;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import com.offbeatmind.humane.core.LanguageProcessor;
import com.offbeatmind.humane.java.annot.AnnotationLayoutProcessor;
import com.offbeatmind.humane.java.braces.DoWhileLoopBracesProcessor;
import com.offbeatmind.humane.java.braces.ForEachLoopBracesProcessor;
import com.offbeatmind.humane.java.braces.ForLoopBracesProcessor;
import com.offbeatmind.humane.java.braces.IfElseBracesProcessor;
import com.offbeatmind.humane.java.braces.WhileLoopBracesChecker;
import com.offbeatmind.humane.java.compilationunit.CompilationUnitContentProcessor;
import com.offbeatmind.humane.java.empty.EmptyStatementsProcessor;
import com.offbeatmind.humane.java.imports.ImportsProcessor;
import com.offbeatmind.humane.java.layout.ItemsPerLineProcessor;
import com.offbeatmind.humane.java.layout.LayoutProcessor;
import com.offbeatmind.humane.java.multidecl.MultipleDeclarationsProcessor;
import com.offbeatmind.humane.java.spaces.BasicWhitespaceProcessor;

public class JavaLanguageProcessor extends LanguageProcessor<JavaSourceTree, JavaFile> {

    public static final JavaLanguageProcessor INSTANCE = new JavaLanguageProcessor();
    
    public final JavaSourceTree constructSourceTree(File file) {
        return new JavaSourceTree(file);
    }
    
    @Override
    public boolean process(boolean fixErrors, Collection<JavaSourceTree> sourceTrees) throws IOException {
        
    	final boolean[] success = new boolean[] { true };
    	
        for (JavaSourceTree sourceTree: sourceTrees) {
            sourceTree.acceptFileVisitor(new Consumer<JavaFile>() {
    
                @Override
                public void accept(JavaFile f) {
                    (new IfElseBracesProcessor(f)).process(fixErrors);
                    (new WhileLoopBracesChecker(f)).process(fixErrors);
                    (new DoWhileLoopBracesProcessor(f)).process(fixErrors);
                    (new ForLoopBracesProcessor(f)).process(fixErrors);
                    (new ForEachLoopBracesProcessor(f)).process(fixErrors);
                    (new ImportsProcessor(f)).process(fixErrors);
                    (new CompilationUnitContentProcessor(f)).process(fixErrors);
                    (new EmptyStatementsProcessor(f)).process(fixErrors);
                    (new MultipleDeclarationsProcessor(f)).process(fixErrors);
                    (new BasicWhitespaceProcessor(f)).process(fixErrors);
                    (new ItemsPerLineProcessor(f)).process(fixErrors);
                    (new AnnotationLayoutProcessor(f)).process(fixErrors);
                    
                    if (f.hasViolations()) success[0] = false;
    
                    f.printViolations();
                }
            });
        }
        
        return success[0];
    }
}
