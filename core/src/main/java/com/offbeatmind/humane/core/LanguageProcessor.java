package com.offbeatmind.humane.core;

import java.io.IOException;
import java.util.Collection;

public abstract class LanguageProcessor {
    
    public abstract void process(boolean fixErrors, SourceTree... sourceTrees) throws IOException;
    
    public final void process(boolean fixErrors, Collection<SourceTree> sourceTrees) throws IOException {
        process(false, sourceTrees.toArray(new SourceTree[sourceTrees.size()]));
    }

}
