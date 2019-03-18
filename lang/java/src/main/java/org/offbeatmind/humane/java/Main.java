package org.offbeatmind.humane.java;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import org.offbeatmind.humane.java.annot.AnnotationLayoutChecker;
import org.offbeatmind.humane.java.braces.DoWhileLoopBracesChecker;
import org.offbeatmind.humane.java.braces.ForEachLoopBracesChecker;
import org.offbeatmind.humane.java.braces.ForLoopBracesChecker;
import org.offbeatmind.humane.java.braces.IfElseBracesChecker;
import org.offbeatmind.humane.java.braces.WhileLoopBracesChecker;
import org.offbeatmind.humane.java.compilationunit.CompilationUnitContentChecker;
import org.offbeatmind.humane.java.empty.EmptyStatementsChecker;
import org.offbeatmind.humane.java.imports.ImportsChecker;
import org.offbeatmind.humane.java.layout.ItemsPerLineChecker;
import org.offbeatmind.humane.java.layout.LayoutChecker;
import org.offbeatmind.humane.java.multidecl.MultipleDeclarationsChecker;
import org.offbeatmind.humane.java.spaces.BasicWhitespaceChecker;

import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.SourceTree;

public class Main {

    public static void main(String[] args) throws IOException {
        for (String sourceTreePath: args) {
            SourceTree tree = new SourceTree(new File(sourceTreePath));
    
            tree.acceptFileVisitor(new Consumer<JavaFile>() {
    
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

}
