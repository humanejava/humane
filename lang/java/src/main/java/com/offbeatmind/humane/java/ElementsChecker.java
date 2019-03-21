package com.offbeatmind.humane.java;

import java.util.List;

public abstract class ElementsChecker extends JavaFileProcessor {

    private NodeSourceElement<?> currentNode;

    public ElementsChecker(JavaFile javaFile) {
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

    protected NodeSourceElement<?> getCurrentNode() {
        return currentNode;
    }

    protected final void processElements(List<SourceElement> elements) {
        final NodeSourceElement<?> initialNode = currentNode;

        boolean firstInNode = true;
        
        for (SourceElement e : elements) {
            if (e.isToken()) {
                processToken((TokenSourceElement) e, firstInNode);
                firstInNode = false;
            } else if (e.isNode()) {
                NodeSourceElement<?> node = (NodeSourceElement<?>) e;
                currentNode = node;
                processNode(node);
                currentNode = initialNode;
            }
        }
    }

    protected abstract void processToken(TokenSourceElement token, boolean firstInNode);

    protected void processNode(NodeSourceElement<?> node) {
        //System.out.println("BEGIN " + node.getNode().getClass().getName() + "#" + System.identityHashCode(node));
        processElements(node.getElements());
        //System.out.println("END " + node.getNode().getClass().getSimpleName() + "#" + System.identityHashCode(node));
    }

    protected void finalizeProcessing() {
    }

}
