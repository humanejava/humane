package com.offbeatmind.humane.core;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;

class JavaFileLocation {
    static final DataKey<JavaFileLocation> DATAKEY = new DataKey<JavaFileLocation>() {};
    
    private final JavaFile javaFile;
    private int firstElementIndex;
    private int lastElementIndex;
    
    private JavaFileLocation(JavaFile javaFile, int firstElementIndex, int lastElementIndex) {
        super();
        
        this.javaFile = javaFile;
        this.firstElementIndex = firstElementIndex;
        this.lastElementIndex = lastElementIndex;
    }

    public int getFirstElementIndex() {
        return firstElementIndex;
    }

    public void setFirstElementIndex(int firstElementIndex) {
        this.firstElementIndex = firstElementIndex;
    }

    public int getLastElementIndex() {
        return lastElementIndex;
    }

    public void setLastElementIndex(int lastElementIndex) {
        this.lastElementIndex = lastElementIndex;
    }

    public JavaFile getJavaFile() {
        return javaFile;
    }
    
    /**
     * Note - assumes there will be a single contiguous range in the end.
     */
    public void addElementRange(int from, int to) {
        if (from < firstElementIndex) firstElementIndex = from;
        if (to > lastElementIndex) lastElementIndex = to;
    }
    
    public static JavaFileLocation of(Node node) {
        if (node.containsData(DATAKEY)) return node.getData(DATAKEY);
        return null;
    }
    
    public static void add(JavaFile javaFile, int from, int to, Node node) {
        JavaFileLocation existing = of(node);
        if (existing != null) {
            if (existing.javaFile != javaFile) throw new IllegalStateException("A single node cannot span multiple files!");
            existing.addElementRange(from, to);
        } else {
            JavaFileLocation newLocation = new JavaFileLocation(javaFile, from, to);
            node.setData(DATAKEY, newLocation);
        }
    }
}
