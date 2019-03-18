package com.offbeatmind.humane.java;

import java.io.IOException;
import java.util.function.Consumer;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.Language;
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

public class JavaLanguage extends Language {

    public static final JavaLanguage INSTANCE = new JavaLanguage();
    
    @Override
    public void check(SourceTree sourceTree) throws IOException {
        
        sourceTree.acceptFileVisitor(new Consumer<JavaFile>() {

            @Override
            public void accept(JavaFile f) {
                (new LayoutChecker(f)).check();
                (new IfElseBracesChecker(f)).check();
                (new WhileLoopBracesChecker(f)).check();
                (new DoWhileLoopBracesChecker(f)).check();
                (new ForLoopBracesChecker(f)).check();
                (new ForEachLoopBracesChecker(f)).check();
                (new ImportsChecker(f)).check();
                (new CompilationUnitContentChecker(f)).check();
                (new EmptyStatementsChecker(f)).check();
                (new MultipleDeclarationsChecker(f)).check();
                (new BasicWhitespaceChecker(f)).check();
                (new ItemsPerLineChecker(f)).check();
                (new AnnotationLayoutChecker(f)).check();

                f.printViolations();
            }
        });
    }
}
