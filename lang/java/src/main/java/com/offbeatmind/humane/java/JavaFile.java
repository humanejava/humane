package com.offbeatmind.humane.java;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.Problem;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.UnknownType;
import com.offbeatmind.humane.core.SourceFile;

public class JavaFile extends SourceFile {
    
    private final JavaSourceTree sourceTree;
    private final List<String> packagePath;
    private final String expectedPackage;
    private final String unitName;
    private final CompilationUnit compilationUnit;

    public JavaFile(JavaSourceTree sourceTree, List<String> packagePath, File sourceFile) throws IOException {
        this(sourceTree, packagePath, sourceFile, getDefaultParserConfiguration());
    }

    private JavaFile(JavaSourceTree sourceTree, List<String> packagePath, File sourceFile, ParserConfiguration config)
    throws IOException {
        super(sourceFile);
        
        this.sourceTree = sourceTree;

        this.packagePath = Collections.unmodifiableList(packagePath);
        StringBuilder pp = new StringBuilder();

        for (String p : packagePath) {
            if (pp.length() > 0) pp.append('.');
            pp.append(p);
        }
        this.expectedPackage = (pp.length() > 0) ? pp.toString() : null;
        String fileName = sourceFile.getName();
        this.unitName = fileName.substring(0, fileName.length() - ".java".length());

        JavaParser parser = new JavaParser(config);
        ParseResult<CompilationUnit> result = parser.parse(getSourceText());

        if (!result.isSuccessful()) {
            for (Problem p : result.getProblems()) {
                System.err.println(p.getLocation() + ": " + p.getVerboseMessage());
            }
            throw new RuntimeException("Could not parse: " + sourceFile.getPath() + ": " + result.toString());
        }
        compilationUnit = result.getResult().get();
        System.out.println("Parsed:  " + sourceFile.getPath());

        organizeElements();
    }
    
    public JavaSourceTree getSourceTree() {
        return sourceTree;
    }

    private static ParserConfiguration getDefaultParserConfiguration() {
        ParserConfiguration config = new ParserConfiguration();
        config.setAttributeComments(false);
        config.setDoNotAssignCommentsPrecedingEmptyLines(true);
        config.setIgnoreAnnotationsWhenAttributingComments(true);
        config.setTabSize(1); // We'll have to handle this and we don't want tabs at all
        config.setLexicalPreservationEnabled(true);
        config.setLanguageLevel(LanguageLevel.JAVA_8);
        config.setPreprocessUnicodeEscapes(false);
        return config;
    }

    public List<String> getPackagePath() {
        return packagePath;
    }

    public String getExpectedPackage() {
        return expectedPackage;
    }

    public String getUnitName() {
        return unitName;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public List<SourceElement> getElements() {
        return NodeElement.of(compilationUnit).getElements();
    }

    private void organizeElements() {
        for (JavaToken token : compilationUnit.getTokenRange().get()) {
            Range tokenRange = token.getRange().orElseThrow(
                () -> new RuntimeException("Token without range: " + token)
            );

            Node owner = findNodeForToken(compilationUnit, tokenRange);

            if (owner == null) {
                throw new RuntimeException("Token without node owning it: " + token);
            }
            NodeElement<?> ownerElement = NodeElement.of(owner);
            ownerElement.addElement(new TokenElement(token, ownerElement));
        }
    }

    private static Node findNodeForToken(Node node, Range tokenRange) {
        if (isPhantomNode(node)) {
            return null;
        }
        if (node.getRange().get().contains(tokenRange)) {
            for (Node child : node.getChildNodes()) {
                Node found = findNodeForToken(child, tokenRange);

                if (found != null) {
                    return found;
                }
            }
            return node;
        } else {
            return null;
        }
    }

    /**
     * Warning: does NOT consider if parents are phantom!
     * They must be filtered on their own.
     */
    private static boolean isPhantomNode(Node node) {
        if (node instanceof UnknownType) return true;
        final Optional<Node> parent = node.getParentNode();

        if (parent.isPresent()) {
            if (!parent.get().getRange().get().contains(node.getRange().get())) return true;
        }
        return false;
    }

    public <T extends Node> void walkNodes(Class<T> nodeType, Consumer<T> consumer) {
        getCompilationUnit().walk(nodeType, consumer);
    }

    public <T extends Node> void walkElements(Class<T> nodeType, Consumer<NodeSourceElement<T>> consumer) {
        walkNodes(nodeType, new Consumer<T>() {

            @Override
            public void accept(T node) {
                consumer.accept(NodeElement.of(node));
            }
        });
    }
}
