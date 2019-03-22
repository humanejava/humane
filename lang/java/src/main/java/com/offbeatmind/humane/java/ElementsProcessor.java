package com.offbeatmind.humane.java;

import java.util.List;

/**
 * Base class for processors that iterate over
 * {@linkplain SourceElement source elements}. 
 * 
 * @author humanejava
 *
 */
public abstract class ElementsProcessor extends JavaFileProcessor {

    private NodeElement<?> currentNode;

    public ElementsProcessor(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        initProcessing();
        processElements(javaFile.getElements());
        finalizeProcessing();
    }

    protected void initProcessing() {
    }

    protected NodeElement<?> getCurrentNode() {
        return currentNode;
    }

    protected final void processElements(List<SourceElement> elements) {
        final NodeElement<?> initialNode = currentNode;

        boolean firstInNode = true;
        
        for (SourceElement e : elements) {
            if (e.isToken()) {
                processToken((TokenElement<?>) e, firstInNode);
                firstInNode = false;
            } else if (e.isNode()) {
                NodeElement<?> node = (NodeElement<?>) e;
                currentNode = node;
                processNode(node);
                currentNode = initialNode;
            }
        }
    }

    protected abstract void processToken(TokenElement<?> token, boolean firstInNode);

    protected void processNode(NodeElement<?> node) {
        //System.out.println("BEGIN " + node.getNode().getClass().getName() + "#" + System.identityHashCode(node));
        processElements(node.getElements());
        //System.out.println("END " + node.getNode().getClass().getSimpleName() + "#" + System.identityHashCode(node));
    }

    protected void finalizeProcessing() {
    }

}
