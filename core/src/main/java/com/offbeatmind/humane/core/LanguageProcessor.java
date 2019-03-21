package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public abstract class LanguageProcessor<T extends SourceTree<F>, F extends SourceFile> {
    
    public final void process(boolean fixErrors, @SuppressWarnings("unchecked") T... sourceTrees) throws IOException {
        process(fixErrors, Arrays.asList(sourceTrees));
    }
    
    public abstract void process(boolean fixErrors, Collection<T> sourceTrees) throws IOException;

    public abstract T constructSourceTree(File file);

}
